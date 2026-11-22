# Especificaciones aplicadas — Ejercicio POO-08

**Asignatura:** Lenguaje de Programación 3 (CYT646) — edición 2026
**Ejercicio:** POO-08 — CRUD REST, H2, reglas de juego, excepciones e Insomnia
**Estudiante:** Ruth Santacruz ([@ruthsantacruzuca](https://github.com/ruthsantacruzuca))
**Repositorio:** [`rsantacruz-taller-git-2026`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026)
**Dominio elegido:** entidades de **Minecraft**

**Enlace exacto al commit de la solución:**

```
https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/10e9bbfdf34e932196cc6f88eb67c8243c21dfe1
```

---

## 1. Objetivo

Publicar un servicio HTTP con **Spring Boot** sobre el modelado de entidades de Minecraft, que **guarda su estado en H2**. El servicio ofrece un **CRUD REST completo** sobre la familia `Mob` (`Creeper` y `Zombie` como hijas), **dos reglas de negocio** del juego que alimentan una **simulación persistida** de explosiones, una **jerarquía de excepciones** con un *handler* por tipo, una **interfaz de dominio** (`Danable`) usada por un endpoint de lectura, una **capa service** que concentra validaciones, reglas y excepciones, y una **colección de Insomnia** versionada para reproducir todas las pruebas.

## 2. Arranque y base

```bash
./mvnw spring-boot:run
```

- Base H2 en archivo: `jdbc:h2:file:./data/minecraft` (se crea sola).
- Consola H2: `http://localhost:8080/h2-console` (usuario `sa`, contraseña vacía).
- Tablas: `mobs` (familia `Mob` con discriminador `tipo` y columna `explotado`) y `jugadores`.

## 3. URLs

| Verbo y URL | Qué hace | Éxito | Error |
|---|---|---|---|
| `GET /` | Servicio vivo | 200 | — |
| `POST /api/minecraft/mobs` | Crea `Creeper` o `Zombie` (`tipo` en el JSON) | 201 | 400 |
| `GET /api/minecraft/mobs` | Lista lo persistido (opcional `?hostil=`) | 200 | — |
| `GET /api/minecraft/mobs/{id}` | Lee un ítem | 200 | 404 |
| `PUT /api/minecraft/mobs/{id}` | Reemplaza y persiste | 200 | 404 / 409 / 422 |
| `DELETE /api/minecraft/mobs/{id}` | Borra la fila | 204 | 404 |
| `POST /api/minecraft/simulaciones/explosion` | Simula explosiones y persiste | 200 | 404 / 409 / 422 |
| `GET /api/minecraft/danables` | Lee por la interfaz `Danable` | 200 | — |
| `POST /api/minecraft/jugadores` | Crea la segunda familia de `Danable` | 201 | 400 |
| `GET /api/minecraft/jugadores` | Lista jugadores | 200 | — |

## 4. Capa service

Los controllers no usan repositorios directamente ni deciden reglas.

| Service | Responsabilidad |
|---|---|
| `MobService` | CRUD, validación y rechazo `409` de un `Creeper` explotado. |
| `JugadorService` | Alta y listado de jugadores con validación. |
| `ExplosionService` | Las dos reglas y la simulación persistida. |
| `DanableService` | Une mobs y jugadores como `List<Danable>`. |
| `ConstruccionService` | Construcción por URL de POO-06 y validación de parámetros. |
| `ValidadorMinecraft` | Validaciones compartidas de todos los datos. |
| `FabricaEntidades` | Elección de la firma simple o sobrecargada. |

Las excepciones viven en `service/exception`. El paquete `domain` sólo guarda estado y los mensajes polimórficos de POO-06.

## 5. Las dos reglas de negocio

Participan `Mob`, `Creeper` y `Zombie`.

1. **Explosión:** un `Creeper` que detona reparte su `cargaExplosion` como daño a todos los mobs que siguen con vida.
2. **Reacción en cadena:** si un `Creeper` muere por esa explosión y todavía tenía carga, detona a su vez sobre los mobs que quedan vivos.

Ambas viven en `service/ExplosionService`. El service marca al creeper como `explotado`, deja su vida y su carga en cero, persiste el estado y devuelve el detalle de cada explosión y de sus víctimas.

## 6. Jerarquía de excepciones

| Excepción | Handler | HTTP | `tipo` en el JSON |
|---|---|---|---|
| `DominioException` (base) | `ManejadorDeErrores.dominio` | 500 | `dominio` |
| `RecursoNoEncontradoException` (hija 1) | `ManejadorDeErrores.noEncontrado` | 404 | `recurso-no-encontrado` |
| `ReglaDeJuegoException` (hija 2) | `ManejadorDeErrores.reglaDeJuego` | 422 | `regla-de-juego` |
| `CreeperConsumidoException` (hija 3) | `ManejadorDeErrores.creeperConsumido` | 409 | `creeper-consumido` |

Son excepciones **no comprobadas** (`RuntimeException`): el service no obliga a declararlas y el handler las traduce a JSON.

El `409` corrige el caso de ocultamiento detectado en la devolución: después de simular, un `PUT` con `vida: 20` sobre el creeper explotado se rechaza en `MobService` y el estado sigue en vida 0. El campo `explotado` es de sólo lectura en el JSON.

## 7. Interfaz de dominio

```java
public interface Danable {
    int getVida();
    String estado();
}
```

La implementan dos familias distintas (`Mob` y `Jugador`). `GET /api/minecraft/danables` arma una `List<Danable>` con lo persistido y pide `estado()` sin preguntar la clase concreta. El daño lo aplica `ExplosionService`.

## 8. Invariantes

| Clase / service | Regla |
|---|---|
| `ValidadorMinecraft.validarEntidad` | vida, daño base y velocidad no negativos |
| `ValidadorMinecraft.validarDistancia` | distancia no negativa |
| `ValidadorMinecraft.validarJugador` | vida máxima 20; armadura no negativa |
| `ValidadorMinecraft.validarCarga` | carga de explosión no negativa |
| `ValidadorMinecraft.validarEntidadCompleta` | precisión de arco entre 0 y 100; velocidad aumentada no negativa |
| `MobService.actualizar` | un `Creeper` explotado no admite cambios (`409`) |
| `ExplosionService` | sin carga no detona (`422`); ya explotado no vuelve a detonar (`409`) |

Los constructores y setters del dominio sólo asignan. Toda entrada HTTP pasa por un service que valida antes de persistir. El constructor vacío que JPA necesita queda `protected`.

## 9. Cómo probarlo

```bash
./mvnw -q test          # dominio + services + API (CRUD, 404, 409, 422, protocolo)
./mvnw spring-boot:run
```

Con el servicio corriendo:

```bash
# vivo
curl -s localhost:8080/

# crear dos creepers, un zombie y un jugador
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":15}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":10,"danoBase":6,"velocidad":3,"cargaExplosion":12}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"ZOMBIE","vida":30,"danoBase":4,"velocidad":3}'
curl -s -X POST localhost:8080/api/minecraft/jugadores -H 'Content-Type: application/json' \
  -d '{"vida":20,"danoBase":3,"velocidad":2,"armadura":1}'

# leer, actualizar y borrar
curl -s localhost:8080/api/minecraft/mobs
curl -s localhost:8080/api/minecraft/mobs/1
curl -s -X PUT localhost:8080/api/minecraft/mobs/1 -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":8,"danoBase":6,"velocidad":3,"cargaExplosion":10}'
curl -s -i -X DELETE localhost:8080/api/minecraft/mobs/3

# POST ilegal: 400 y la colección no crece
curl -s -i -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"ZOMBIE","vida":-5,"danoBase":4,"velocidad":3}'

# id inexistente: 404
curl -s -i localhost:8080/api/minecraft/mobs/999

# protocolo de dominio
curl -s localhost:8080/api/minecraft/danables

# simulación (dos reglas) y estado persistido
curl -s -X POST localhost:8080/api/minecraft/simulaciones/explosion -H 'Content-Type: application/json' \
  -d '{"creeperId":1}'
curl -s localhost:8080/api/minecraft/mobs

# creeper ya explotado: PUT rechazado con 409
curl -s -i -X PUT localhost:8080/api/minecraft/mobs/1 -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":10}'

# regla incumplida: 422 (el id apunta a un Zombie)
curl -s -i -X POST localhost:8080/api/minecraft/simulaciones/explosion -H 'Content-Type: application/json' \
  -d '{"creeperId":3}'
```

En Insomnia: importar `insomnia/poo-08-insomnia.json`, ajustar `base_url` y los `id` del entorno, y seguir el orden del README (crear dos creepers, zombie y jugador → leer → actualizar → simular → probar 409/422 → borrar).

## 10. Entregables de este ejercicio

1. **Este archivo** (Markdown) en Classroom, con el enlace al commit.
2. **README** al día: arranque, propiedades de H2, capa service, diagrama Mermaid, las dos reglas en una frase, respuestas a «Qué hay que pensar» e importación de Insomnia.
3. **`BITACORA.md`** con la marca del asistente, el modelo exacto y el resumen de prompts.
4. **Colección de Insomnia** en `insomnia/poo-08-insomnia.json`.
5. **Código** en el repositorio público, con historial en commits separados.

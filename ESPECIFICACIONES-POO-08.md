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

Publicar un servicio HTTP con **Spring Boot** sobre el modelado de entidades de Minecraft, que ahora **guarda su estado en H2**. El servicio ofrece un **CRUD REST completo** sobre la familia `Mob` (`Creeper` y `Zombie` como hijas), **dos reglas de negocio** del juego que alimentan una **simulación persistida** de explosiones, una **jerarquía de excepciones** del dominio con un *handler* por tipo, una **interfaz de dominio** (`Danable`) usada por un endpoint de lectura, y una **colección de Insomnia** versionada para reproducir todas las pruebas.

## 2. Arranque y base

```bash
./mvnw spring-boot:run
```

- Base H2 en archivo: `jdbc:h2:file:./data/minecraft` (se crea sola).
- Consola H2: `http://localhost:8080/h2-console` (usuario `sa`, contraseña vacía).
- Tablas: `mobs` (familia `Mob` con discriminador `tipo`) y `jugadores`.

## 3. URLs

| Verbo y URL | Qué hace | Éxito | Error |
|---|---|---|---|
| `GET /` | Servicio vivo | 200 | — |
| `POST /api/minecraft/mobs` | Crea `Creeper` o `Zombie` (`tipo` en el JSON) | 201 | 400 |
| `GET /api/minecraft/mobs` | Lista lo persistido (opcional `?hostil=`) | 200 | — |
| `GET /api/minecraft/mobs/{id}` | Lee un ítem | 200 | 404 |
| `PUT /api/minecraft/mobs/{id}` | Reemplaza y persiste | 200 | 404 / 422 |
| `DELETE /api/minecraft/mobs/{id}` | Borra la fila | 204 | 404 |
| `POST /api/minecraft/simulaciones/explosion` | Simula explosiones y persiste | 200 | 404 / 422 |
| `GET /api/minecraft/danables` | Lee por la interfaz `Danable` | 200 | — |
| `POST /api/minecraft/jugadores` | Crea la segunda familia de `Danable` | 201 | 400 |
| `GET /api/minecraft/jugadores` | Lista jugadores | 200 | — |

## 4. Las dos reglas de negocio

Participan `Mob`, `Creeper` y `Zombie`.

1. **Explosión:** un `Creeper` que detona reparte su `cargaExplosion` como daño a todos los mobs que siguen con vida.
2. **Reacción en cadena:** si un `Creeper` muere por esa explosión y todavía tenía carga, detona a su vez sobre los mobs que quedan vivos.

Ambas viven en `domain/SimuladorExplosiones` y en los mensajes del dominio (`Creeper.explotar`, `Mob.recibirDano`). El resultado se persiste y un `GET /api/minecraft/mobs` posterior muestra el estado final.

## 5. Jerarquía de excepciones

| Excepción | Handler | HTTP | `tipo` en el JSON |
|---|---|---|---|
| `DominioException` (base) | `ManejadorDeErrores.dominio` | 500 | `dominio` |
| `RecursoNoEncontradoException` (hija 1) | `ManejadorDeErrores.noEncontrado` | 404 | `recurso-no-encontrado` |
| `ReglaDeJuegoException` (hija 2) | `ManejadorDeErrores.reglaDeJuego` | 422 | `regla-de-juego` |

Son excepciones **no comprobadas** (`RuntimeException`): el dominio no obliga a declararlas y el handler las traduce a JSON.

## 6. Interfaz de dominio

```java
public interface Danable {
    int getVida();
    void recibirDano(int dano);
    String estado();
}
```

La implementan dos familias distintas (`Mob` y `Jugador`). `GET /api/minecraft/danables` arma una `List<Danable>` con lo persistido y pide `estado()` sin preguntar la clase concreta.

## 7. Invariantes

| Clase | Regla |
|---|---|
| `Entidad` | vida, daño base y velocidad no negativos |
| `Entidad.atacar(int)` | distancia no negativa |
| `Jugador` | vida máxima 20; armadura no negativa |
| `Creeper` | carga de explosión no negativa; sólo aumenta con valores positivos |
| `Esqueleto` | precisión de arco entre 0 y 100 |
| `ZombiePequeno` | velocidad aumentada no negativa |
| `Mob.recibirDano` | daño no negativo; la vida nunca baja de 0 |

JPA usa acceso por propiedad (el `@Id` está en el getter), así Hibernate llama a los **setters** que validan: la capa de tabla no puede dejar el objeto en un estado imposible. El constructor vacío que JPA necesita queda `protected`.

## 8. Cómo probarlo

```bash
./mvnw -q test          # dominio + API (CRUD, 404, 422, protocolo)
./mvnw spring-boot:run
```

Con el servicio corriendo:

```bash
# vivo
curl -s localhost:8080/

# crear un creeper y un zombie
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":24}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"ZOMBIE","vida":20,"danoBase":4,"velocidad":3}'

# leer, actualizar y borrar
curl -s localhost:8080/api/minecraft/mobs
curl -s localhost:8080/api/minecraft/mobs/1
curl -s -X PUT localhost:8080/api/minecraft/mobs/1 -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":8,"danoBase":6,"velocidad":3,"cargaExplosion":10}'
curl -s -i -X DELETE localhost:8080/api/minecraft/mobs/2

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

# regla incumplida: 422
curl -s -i -X POST localhost:8080/api/minecraft/simulaciones/explosion -H 'Content-Type: application/json' \
  -d '{"creeperId":2}'
```

En Insomnia: importar `insomnia/poo-08-insomnia.json`, ajustar `base_url` y los `id` del entorno, y seguir el orden del README (crear → leer → actualizar → simular → provocar errores).

## 9. Entregables de este ejercicio

1. **Este archivo** (Markdown) en Classroom, con el enlace al commit.
2. **README** al día: arranque, propiedades de H2, diagrama Mermaid, las dos reglas en una frase, respuestas a «Qué hay que pensar» e importación de Insomnia.
3. **`BITACORA.md`** con la marca del asistente, el modelo exacto y el resumen de prompts.
4. **Colección de Insomnia** en `insomnia/poo-08-insomnia.json`.
5. **Código** en el repositorio público, con historial en commits separados.

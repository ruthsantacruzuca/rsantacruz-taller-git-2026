# Corrección de POO-08 — observaciones cerradas

**Entrega original:** 19/24 — nota 7,9/10 — +1 de bono  
**Commit de la corrección:** [`a76999efba2e2e893fc982df54013f2334fba5d4`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/a76999efba2e2e893fc982df54013f2334fba5d4)  
**Commit que actualiza el enlace en README y especificaciones:** [`f439cc8354f1424408bdd40b45b0c59a1fcdd47d`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/f439cc8354f1424408bdd40b45b0c59a1fcdd47d)  
**Repositorio:** [`rsantacruz-taller-git-2026`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026)

## Resultado

| Criterio | Puntaje original | Observación | Estado |
|---|---|---|---|
| 1. CRUD REST | 3/3 | Sin observación | Se mantiene |
| 2. Paquetes y capas | 1/3 | Faltaba capa `service`; los controllers usaban repositorios y el dominio tenía validaciones, reglas y excepciones | **Cerrado** |
| 3. Ocultamiento | 0/3 | Un `PUT` con `vida: 20` revivía un Creeper que ya había explotado | **Cerrado** |
| 4. Reglas de juego | 3/3 | Sin observación | Se mantiene |
| 5. Simulación persistida | 3/3 | Sin observación | Se mantiene |
| 6. Jerarquía de excepciones | 3/3 | Sin observación | Se mantiene |
| 7. Interfaz `Danable` | 3/3 | Sin observación | Se mantiene |
| 8. Insomnia | 3/3 | Sin observación de puntaje | Se amplió |
| Mejora: enlace de Classroom | — | El enlace quedó truncado (36/40) | **Cerrado** |
| Mejora: colección Insomnia | — | Sólo había un Creeper, no se veía la cadena y no había Jugador | **Cerrado** |

## Criterio 2 — capa `service` y dominio sin reglas

Se creó el paquete `py.edu.uc.lp3.service`:

| Service | Responsabilidad |
|---|---|
| `MobService` | CRUD, validaciones y rechazo `409` de un Creeper explotado. |
| `JugadorService` | Alta y listado de jugadores con validación. |
| `ExplosionService` | Las dos reglas de negocio y la simulación persistida. |
| `DanableService` | Une mobs y jugadores como `List<Danable>`. |
| `ConstruccionService` | Construcción por URL de POO-06 y validación de parámetros. |
| `ValidadorMinecraft` | Validaciones compartidas: vida, daño, velocidad, carga, armadura, precisión y distancia. |
| `FabricaEntidades` | Elección de la firma simple o sobrecargada. |

Los controllers ya no importan ni usan repositorios: dependen de los services. Las excepciones se movieron a `service/exception`. El paquete `domain` quedó sólo con estado y los mensajes polimórficos de POO-06; ya no tiene `throw`, validaciones ni reglas de juego.

**Evidencia:** `src/main/java/py/edu/uc/lp3/service/`, `src/main/java/py/edu/uc/lp3/rest/controller/`, `src/main/java/py/edu/uc/lp3/domain/` y el commit `d06297f`.

## Criterio 3 — un Creeper explotado no revive

Se agregó el booleano persistido `explotado` en `Creeper`. `ExplosionService` lo marca al detonar y `MobService.actualizar` lo consulta antes de cualquier `PUT`. Si el Creeper ya explotó, lanza `CreeperConsumidoException`, que el handler traduce a **HTTP 409** con `tipo: creeper-consumido`. El campo `explotado` es de sólo lectura en el JSON, así el cliente no puede mandarlo en `false`.

**Evidencia:** `MobService.actualizar`, `CreeperConsumidoException`, `ManejadorDeErrores.creeperConsumido`, el test `ApiTest.putSobreCreeperExplotadoDevuelve409YNoLoRevive` y el commit `d06297f`.

Prueba manual:

```bash
# crear creeper, simular y luego intentar revivirlo
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":15}'
curl -s -X POST localhost:8080/api/minecraft/simulaciones/explosion -H 'Content-Type: application/json' \
  -d '{"creeperId":1}'
curl -s -i -X PUT localhost:8080/api/minecraft/mobs/1 -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":10}'
# Esperado: 409 creeper-consumido
curl -s localhost:8080/api/minecraft/mobs/1
# Esperado: vida 0, carga 0, explotado true
```

## Mejoras sin puntaje

**Enlace de Classroom.** El README y `ESPECIFICACIONES-POO-08.md` usan el hash completo de 40 caracteres. El enlace que hay que pegar es:

```text
https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/a76999efba2e2e893fc982df54013f2334fba5d4
```

**Insomnia.** La colección `insomnia/poo-08-insomnia.json` ahora crea dos creepers (el segundo arranca la cadena), un zombie y un jugador; incluye el escenario feliz con dos explosiones, el `PUT` sobre el creeper explotado (`409`) y un rechazo de regla (`422`).

## Verificación

```bash
./mvnw -q clean test
# 26 tests, 0 fallos
```

También se verificó por `curl` el flujo completo: creación, simulación con reacción en cadena, persistencia del estado, `PUT` rechazado con `409`, `GET` posterior con `vida: 0` y `explotado: true`.

## Estado final

Todas las observaciones de POO-08 quedaron cerradas. El servicio sigue arrancando con `./mvnw spring-boot:run` y mantiene CRUD, simulación, jerarquía de excepciones, interfaz `Danable` y colección de Insomnia.

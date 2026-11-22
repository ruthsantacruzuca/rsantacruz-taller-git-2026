# rsantacruz-taller-git-2026

API REST del modelado de entidades de **Minecraft** (Lenguaje de Programación 3, edición 2026).
Ejercicio **POO-08**: CRUD REST, persistencia en **H2**, reglas de juego, simulación, jerarquía de excepciones, protocolo de dominio, capa **service** y colección de **Insomnia**. Continúa el modelado de POO-06 (paquetes, constructores, sobrecarga y sobreescritura).

**Commit de la solución:** [`a76999efba2e2e893fc982df54013f2334fba5d4`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/a76999efba2e2e893fc982df54013f2334fba5d4) — código, este README, `BITACORA.md`, la colección de Insomnia y las especificaciones (el enlace también está en [`ESPECIFICACIONES-POO-08.md`](ESPECIFICACIONES-POO-08.md) y pegado en Classroom).

## Corrección de POO-08

POO-08 tuvo observaciones en los criterios 2 (capa `service`) y 3 (un Creeper explotado se podía revivir con `PUT`). Las dos quedaron cerradas: las validaciones, reglas y excepciones viven en `service`, y un `PUT` sobre un Creeper con `explotado = true` responde **409**. El detalle de cada observación, la evidencia y cómo probarlo está en [`CORRECCION-POO-08.md`](CORRECCION-POO-08.md).

## Levantar

```bash
./mvnw spring-boot:run
```

Abre en `http://localhost:8080`. La base H2 queda en `./data/minecraft` (se crea sola) y la consola web en `http://localhost:8080/h2-console`.

---

## Base H2 y propiedades

El servicio guarda su estado en **H2**, un motor relacional que corre dentro del proceso Java. Spring Boot lo conecta con `spring-boot-starter-data-jpa` y el driver de H2. JPA describe cómo cada clase se corresponde con una tabla; Spring Data genera la implementación de los repositorios.

| Propiedad | Qué significa |
|---|---|
| `spring.datasource.url=jdbc:h2:file:./data/minecraft;AUTO_SERVER=TRUE` | Base **en archivo** dentro de `./data`; `AUTO_SERVER` permite abrirla desde la consola mientras el servicio corre. |
| `spring.datasource.driver-class-name=org.h2.Driver` | Driver JDBC de H2. |
| `spring.datasource.username=sa` | Usuario por defecto de H2. |
| `spring.datasource.password=` | Sin contraseña en el perfil de desarrollo. |
| `spring.jpa.hibernate.ddl-auto=update` | Hibernate crea/actualiza las tablas a partir de las entidades. |
| `spring.jpa.show-sql=false` | No imprime cada SQL en consola (se puede poner en `true` para depurar). |
| `spring.h2.console.enabled=true` | Habilita la consola web de H2. |
| `spring.h2.console.path=/h2-console` | Ruta de la consola. |

En la consola se entra con la URL `jdbc:h2:file:./data/minecraft`, usuario `sa` y contraseña vacía. Ahí se ve la tabla `mobs` (familia persistida, con la columna `explotado`) y la tabla `jugadores`.

---

## Endpoints

| Verbo y URL | Qué hace | Éxito |
|---|---|---|
| `GET /` | Confirma que el servicio vive | 200 |
| `POST /api/minecraft/mobs` | Crea un `Creeper` o un `Zombie` (el JSON lleva `tipo`) | 201 + `Location` |
| `GET /api/minecraft/mobs` | Lista lo persistido (opcional `?hostil=true`) | 200 |
| `GET /api/minecraft/mobs/{id}` | Lee un ítem por su id | 200 / 404 |
| `PUT /api/minecraft/mobs/{id}` | Reemplaza el recurso completo y persiste | 200 / 404 / 409 / 422 |
| `DELETE /api/minecraft/mobs/{id}` | Borra la fila | 204 / 404 |
| `POST /api/minecraft/simulaciones/explosion` | Corre la simulación de explosiones y persiste | 200 / 404 / 409 / 422 |
| `GET /api/minecraft/danables` | Lee mobs y jugadores **por el tipo de la interfaz** `Danable` | 200 |
| `POST /api/minecraft/jugadores` | Crea un jugador (segunda familia de `Danable`) | 201 / 400 |
| `GET /api/minecraft/jugadores` | Lista jugadores persistidos | 200 |
| `GET /api/minecraft/construir` | (POO-06) construye una entidad por URL | 200 / 400 |
| `GET /api/minecraft/sonidos` | (POO-06) sonido de cada hija, tratadas como `Entidad` | 200 |

El recurso de la API es el sustantivo **`mobs`**; el verbo HTTP dice la operación. El número de la URL es el `id` que asigna H2 (`GenerationType.IDENTITY`), no un índice de una lista en el controller.

---

## Capa service

Los controllers **no** hablan con los repositorios ni deciden reglas: sólo reciben HTTP y delegan.

| Service | Responsabilidad |
|---|---|
| `MobService` | CRUD de `Mob`, validaciones y rechazo `409` de un `Creeper` ya explotado. |
| `JugadorService` | Alta y listado de `Jugador`, con validación de vida máxima y armadura. |
| `ExplosionService` | Las dos reglas de negocio y la simulación persistida. |
| `DanableService` | Une mobs y jugadores como `List<Danable>` para el endpoint del protocolo. |
| `ConstruccionService` | Construcción por URL de POO-06, validación de parámetros y uso de la fábrica. |
| `ValidadorMinecraft` | Validaciones compartidas: vida, daño, velocidad, carga, armadura, precisión, distancia. |
| `FabricaEntidades` | Elige la firma simple o sobrecargada según el tipo pedido. |

Las excepciones de negocio viven en `service/exception`. El paquete `domain` sólo guarda **estado** y mensajes polimórficos de POO-06; no valida ni lanza excepciones.

---

## Las dos reglas del juego

Participan las tres clases de la familia persistida: `Mob` (padre), `Creeper` y `Zombie` (hijas).

1. **Explosión:** un `Creeper` que detona reparte su `cargaExplosion` como daño a todos los mobs que siguen con vida.
2. **Reacción en cadena:** si un `Creeper` muere por esa explosión y todavía tenía carga, detona a su vez sobre los mobs que quedan vivos.

Las reglas viven en `service/ExplosionService` (`danoDeExplosion`, `propagaEnCadena`, `simular`). El service también marca al creeper como `explotado`, deja su vida y su carga en cero y persiste el estado. El controller sólo delega.

### Simulación (caso feliz)

```bash
# 1) crear dos creepers y un zombie (el segundo creeper arranca la cadena)
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":15}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":10,"danoBase":6,"velocidad":3,"cargaExplosion":12}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"ZOMBIE","vida":30,"danoBase":4,"velocidad":3}'

# 2) simular: el creeper 1 detona y el creeper 2 detona en cadena
curl -s -X POST localhost:8080/api/minecraft/simulaciones/explosion -H 'Content-Type: application/json' \
  -d '{"creeperId":1}'

# 3) el estado quedó persistido
curl -s localhost:8080/api/minecraft/mobs
```

La respuesta de la simulación lista cada explosión, sus víctimas (vida antes y después) y el estado final. El `GET` posterior muestra lo mismo ya guardado en H2. Con esos valores el zombie termina con **3** de vida y los dos creepers quedan explotados.

---

## Jerarquía de excepciones y handlers

La jerarquía es **no comprobada** (hereda de `RuntimeException`): el service no obliga a cada capa a declararla y el handler HTTP la traduce a JSON. Se eligió no comprobada porque una regla de negocio incumplida no es un error recuperable en el punto donde se lanza.

| Excepción | Rol | Handler | HTTP | JSON (`tipo`) |
|---|---|---|---|---|
| `DominioException` | base de la jerarquía | `ManejadorDeErrores.dominio` | 500 | `dominio` |
| `RecursoNoEncontradoException` | hija 1: el recurso no existe | `ManejadorDeErrores.noEncontrado` | **404** | `recurso-no-encontrado` |
| `ReglaDeJuegoException` | hija 2: una regla no se puede cumplir | `ManejadorDeErrores.reglaDeJuego` | **422** | `regla-de-juego` |
| `CreeperConsumidoException` | hija 3: el recurso existe pero ya se consumió | `ManejadorDeErrores.creeperConsumido` | **409** | `creeper-consumido` |

Las hermanas se distinguen por el **código HTTP** y por el campo `tipo` del cuerpo:

```json
{"tipo":"recurso-no-encontrado","mensaje":"No existe el mob con id 999"}
{"tipo":"regla-de-juego","mensaje":"El mob 3 es Zombie y no puede detonar: la simulación necesita un Creeper"}
{"tipo":"creeper-consumido","mensaje":"El creeper 1 ya explotó y no admite modificaciones"}
```

Además, un valor que rompe una validación del service responde `400` con `tipo: argumento-invalido` (por ejemplo `"La vida no puede ser negativa: -5"`). Un `POST` ilegal **no** agrega basura a la colección.

**La prueba del `409`:** después de simular, un `PUT` que intente devolverle `vida: 20` al creeper explotado se rechaza en `MobService` antes de tocar el estado. El `GET` posterior sigue mostrando `vida: 0` y `explotado: true`.

---

## Interfaz `Danable` (protocolo de POO-07)

```java
public interface Danable {
    int getVida();
    String estado();
}
```

La implementan **dos familias distintas**: `Mob` (y sus hijas `Creeper`/`Zombie`) y `Jugador`. El endpoint `GET /api/minecraft/danables` arma una `List<Danable>` con lo persistido de ambas familias y pide `estado()` sin preguntar la clase concreta. Así el protocolo sirve para hablar con objetos de jerarquías diferentes. El daño lo aplica `ExplosionService`; las entidades sólo exponen su estado.

---

## Persistencia y herencia JPA

La familia persistida es `Mob` (padre) con `Creeper` y `Zombie` (dos hijas independientes). Se eligió **`InheritanceType.SINGLE_TABLE`**: una sola tabla `mobs` con una columna discriminadora `tipo` (`CREEPER` / `ZOMBIE`). En este dominio hay pocos subtipos y casi todas las consultas son sobre la familia completa, así que una tabla evita *joins* y mantiene simple el CRUD.

- **Discriminador en la base:** `@DiscriminatorColumn(name = "tipo")` + `@DiscriminatorValue` en cada hija.
- **Discriminador en el JSON:** Jackson con `@JsonTypeInfo(property = "tipo")` y `@JsonSubTypes`, de modo que un `POST` con `"tipo":"CREEPER"` construye la hija correcta y un `GET` informa el subtipo.
- **Constructor sin argumentos:** JPA lo necesita. Queda **`protected`** (no público) para no abrir una puerta que deje el objeto en estado ilegal.
- **Estado consumido:** `Creeper` tiene el booleano persistido `explotado`. El service lo pone en `true` al detonar y `MobService` lo consulta antes de cualquier `PUT`. El JSON lo expone como **sólo lectura**, así el cliente no puede “revivir” un creeper mandando `explotado: false`.
- **Validación en service:** los constructores y setters del dominio sólo asignan. Toda entrada HTTP pasa por `MobService`/`JugadorService`/`ConstruccionService` y por `ValidadorMinecraft` antes de persistirse.

`Jugador` es una entidad aparte (tabla `jugadores`) que comparte `Entidad` como `@MappedSuperclass`; existe para mostrar el protocolo entre familias distintas.

---

## Diagrama

```mermaid
classDiagram
  direction LR

  class Danable {
    <<interface>>
    +int getVida()
    +String estado()
  }

  class Entidad {
    <<abstract, MappedSuperclass>>
    -int vida
    -int danoBase
    -int velocidad
    +String emitirSonido()*
    +String atacar()
    +String atacar(int distancia)
  }

  class Mob {
    <<Entity SINGLE_TABLE>>
    -Long id
    -boolean hostil
    #Mob()
    +String estado()
  }

  class Creeper {
    <<Entity>>
    -int cargaExplosion
    -boolean explotado
    +String emitirSonido()
  }

  class Zombie {
    <<Entity>>
    +String emitirSonido()
    +String atacar()
  }

  class Jugador {
    <<Entity>>
    -Long id
    -int armadura
    +String estado()
  }

  class MobService {
    +crear(Mob)
    +listar(Boolean)
    +obtener(Long)
    +actualizar(Long, Mob)
    +borrar(Long)
  }

  class ExplosionService {
    +simular(Long)
    +simular(Creeper, List~Mob~)
    +danoDeExplosion(Creeper)
    +propagaEnCadena(Mob)
  }

  class JugadorService
  class DanableService
  class ConstruccionService
  class ValidadorMinecraft

  class MobRepository {
    <<interface Spring Data>>
    +findAll()
    +findById(Long)
    +save(Mob)
    +delete(Mob)
    +findByHostil(boolean)
  }

  class JugadorRepository {
    <<interface Spring Data>>
  }

  class MobController {
    +POST /api/minecraft/mobs
    +GET /api/minecraft/mobs
    +GET /api/minecraft/mobs/{id}
    +PUT /api/minecraft/mobs/{id}
    +DELETE /api/minecraft/mobs/{id}
  }

  class SimulacionController {
    +POST /api/minecraft/simulaciones/explosion
  }

  class DanableController {
    +GET /api/minecraft/danables
  }

  class DominioException {
    <<RuntimeException>>
  }
  class RecursoNoEncontradoException
  class ReglaDeJuegoException
  class CreeperConsumidoException
  class ManejadorDeErrores {
    <<RestControllerAdvice>>
    +handler RecursoNoEncontrado : 404
    +handler ReglaDeJuego : 422
    +handler CreeperConsumido : 409
  }

  Entidad <|-- Mob
  Entidad <|-- Jugador
  Mob <|-- Creeper
  Mob <|-- Zombie

  Danable <|.. Mob : implementa
  Danable <|.. Jugador : implementa

  MobController --> MobService : delega
  SimulacionController --> ExplosionService : delega
  DanableController --> DanableService : delega
  MobService --> MobRepository : usa
  JugadorService --> JugadorRepository : usa
  DanableService --> MobRepository : usa
  DanableService --> JugadorRepository : usa
  ExplosionService --> MobRepository : usa
  MobService ..> ValidadorMinecraft : valida
  JugadorService ..> ValidadorMinecraft : valida
  ConstruccionService ..> ValidadorMinecraft : valida
  MobRepository ..> Mob : persiste
  JugadorRepository ..> Jugador : persiste
  ExplosionService ..> Creeper : reglas 1 y 2
  ExplosionService ..> Mob : daña

  DominioException <|-- RecursoNoEncontradoException
  DominioException <|-- ReglaDeJuegoException
  DominioException <|-- CreeperConsumidoException
  ManejadorDeErrores ..> RecursoNoEncontradoException : atiende
  ManejadorDeErrores ..> ReglaDeJuegoException : atiende
  ManejadorDeErrores ..> CreeperConsumidoException : atiende
```

El diagrama distingue **herencia** (`<|--`), **implementación de interfaz** (`<|..`), **delegación del controller al service** (`-->`) y **jerarquía de excepciones** (`<|--` desde `DominioException`).

---

## Respuestas a «Qué hay que pensar»

**Recurso y verbo.** El sustantivo es `mobs`; la URL nombra el recurso y el verbo HTTP dice la operación (`POST` crea, `GET` lee, `PUT` actualiza, `DELETE` borra). La simulación es un recurso propio (`simulaciones/explosion`) porque es una acción con efecto sobre varios objetos.

**Identidad.** La clave es `id`, un `Long` que asigna H2 con `IDENTITY`. El JSON de creación lo **omite**: `id` está marcado como `READ_ONLY` en Jackson, así que el cliente no lo elige ni lo pisa.

**Herencia y tablas.** Se eligió `SINGLE_TABLE` con la columna `tipo`; en este dominio los subtipos son pocos y se consulta la familia completa, y evita *joins*. El subtipo se distingue en el JSON con el discriminador `tipo` de Jackson.

**Interfaz y repositorio.** El repositorio de Spring Data **es una interfaz**: declaro los mensajes y el marco genera el proxy. El controller depende de `MobService`, y el service depende de `MobRepository`, no de una implementación.

**Protocolo del dominio.** `Danable` es la interfaz propia. La usa `GET /api/minecraft/danables`. Sí puede hablar con familias distintas: `Mob` y `Jugador` la implementan y el endpoint las trata a todas como `Danable`.

**Ocultamiento y capas.** Las validaciones viven en `service/ValidadorMinecraft`; las reglas de juego, en `ExplosionService`. Las entidades sólo guardan estado. El controller no decide nada: delega. La prueba es el `POST` ilegal (400 y la colección no crece) y el `PUT` sobre un creeper explotado (409 y el estado no cambia).

**PUT y PATCH.** Se implementó **PUT**: reemplaza el recurso entero. Un campo omitido en el cuerpo queda en su valor por defecto (`0` para los `int`), que es un estado legal; por eso conviene mandar el objeto completo. Un creeper ya explotado no admite `PUT`: el service responde **409**.

**DELETE.** Borrar significa quitar la fila (`repositorio.delete`). El service habla con el repositorio y el controller responde `204`; un `GET` posterior es `404`. No hay borrado lógico en este modelo.

**Dos reglas.** Son la explosión y la reacción en cadena (arriba). Cambian el estado de varios objetos a la vez. El recurso es `POST /api/minecraft/simulaciones/explosion`; un `GET /api/minecraft/mobs` posterior muestra el resultado ya persistido.

**Excepciones.** `DominioException` (base), `RecursoNoEncontradoException` (recurso inexistente), `ReglaDeJuegoException` (regla incumplida) y `CreeperConsumidoException` (recurso consumido). Son tipos distintos y tienen handlers distintos (404, 422 y 409). El JSON de error lleva `tipo` y `mensaje`.

**Cambiar de base.** Si mañana se cambia H2 por otra base, se tocan `pom.xml` y `application.properties` (driver, URL, credenciales). Las entidades, repositorios, services y controllers se quedan quietos.

**Quién lanza la excepción.** La lanza el **service** (`MobService`, `ExplosionService`, `ValidadorMinecraft`); el controller no decide reglas, sólo traduce con el `@RestControllerAdvice`.

---

## Colección de Insomnia

El archivo versionado es [`insomnia/poo-08-insomnia.json`](insomnia/poo-08-insomnia.json).

**Importar:** en Insomnia → *Application* → *Import* → *From File* → elegir `insomnia/poo-08-insomnia.json`. Trae un entorno `Base` con `base_url`, `creeper_id`, `creeper2_id`, `zombie_id` y `jugador_id`.

**Orden de ejecución** (con el servicio corriendo y base limpia):

1. `01 POST crear creeper detonador` (vida 20, carga 15), `02 POST crear creeper para la cadena` (vida 10, carga 12), `03 POST crear zombie` (vida 30) y `04 POST crear jugador` → copiar los `id` en las variables del entorno.
2. `05 GET coleccion` → aparecen los cuatro recursos.
3. `06 GET creeper por id` → coincide.
4. `07 PUT actualizar creeper` → cambia la vida y la carga; `06` de nuevo muestra el cambio persistido.
5. `08 POST ilegal (vida negativa)` → `400` y la colección no creció.
6. `09 GET id inexistente` → `404`.
7. `10 GET danables (interfaz)` → `Mob` y `Jugador` por el tipo `Danable`.
8. `11 POST simular explosion` → primera explosión + reacción en cadena; el zombie queda con 3 de vida.
9. `12 GET creeper explotado` → vida 0, carga 0 y `explotado: true`.
10. `13 Hija 1: RecursoNoEncontrado (404)`, `14 Hija 2: ReglaDeJuego (422)` y `15 Hija 3: CreeperConsumido (409)` → cada handler con su código.
11. `16 DELETE zombie` → `204`; un `GET` con ese id da `404`.

Los `id` pueden variar si la base ya tenía datos: en ese caso actualizá las variables del entorno con los `id` reales de la respuesta.

---

## Sobrecarga y sobreescritura (POO-06)

**Sobrecarga** — mismo nombre, otra lista de argumentos, en la misma clase; se decide al compilar.

| Clase | Firmas |
|---|---|
| `Entidad` | `Entidad(int vida)` y `Entidad(int vida, int danoBase, int velocidad)` |
| `Mob` | `Mob(int vida)`, `Mob(int vida, boolean hostil)` y `Mob(int vida, int danoBase, int velocidad, boolean hostil)` |
| `Zombie` | `Zombie(int vida)` y `Zombie(int vida, int danoBase, int velocidad)` |
| `Creeper`, `Jugador`, `Esqueleto`, `ZombiePequeno` | firma simple y firma completa |
| `Entidad` (mensaje) | `atacar()` y `atacar(int distancia)` |
| `FabricaEntidades` | `construir(tipo, vida)` y `construir(tipo, vida, danoBase, velocidad, valorExtra)` |

La firma simple delega con `this(...)` / `super(...)` en la completa, así toda firma deja el objeto con valores por defecto. La validación de esos valores ya no ocurre en el constructor: ocurre en `ValidadorMinecraft` al entrar por HTTP.

**Sobreescritura** — misma firma, implementación propia en la hija; se decide al ejecutar.

| Método de la base | Lo sobreescriben |
|---|---|
| `Entidad.emitirSonido()` *(abstracto)* | `Mob`, `Jugador`, `Zombie`, `Creeper`, `Esqueleto`, `Cerdo`, `Aldeano`, `ZombiePequeno` |
| `Entidad.atacar()` | `Zombie` |

En dos frases: **sobrecarga** = dos métodos con el mismo nombre en la misma clase, cambia la lista de parámetros, se resuelve al compilar. **Sobreescritura** = la hija redefine un método de la base con la misma firma, se resuelve al ejecutar.

---

## Paquetes

```text
src/main/java/py/edu/uc/lp3/
├── Application.java                 ← sólo arranca el servicio
├── domain/                          ← estado y mensajes polimórficos
│   ├── Entidad.java                 (@MappedSuperclass)
│   ├── Mob.java                     (@Entity, SINGLE_TABLE)
│   ├── Creeper.java                 (@Entity, con explotado)
│   ├── Zombie.java                  (@Entity)
│   ├── Jugador.java                 (@Entity, otra familia)
│   ├── Danable.java                 (interface / protocolo)
│   └── Esqueleto.java, Cerdo.java, Aldeano.java, ZombiePequeno.java
├── repository/                      ← interfaces Spring Data
│   ├── MobRepository.java
│   └── JugadorRepository.java
├── service/                         ← validaciones, reglas y excepciones
│   ├── MobService.java
│   ├── JugadorService.java
│   ├── ExplosionService.java
│   ├── DanableService.java
│   ├── ConstruccionService.java
│   ├── ValidadorMinecraft.java
│   ├── FabricaEntidades.java
│   └── exception/
│       ├── DominioException.java
│       ├── RecursoNoEncontradoException.java
│       ├── ReglaDeJuegoException.java
│       └── CreeperConsumidoException.java
└── rest/controller/                 ← entrada HTTP
    ├── IndexController.java
    ├── MobController.java           (CRUD, delega en MobService)
    ├── JugadorController.java
    ├── SimulacionController.java
    ├── DanableController.java
    ├── ManejadorDeErrores.java      (@RestControllerAdvice)
    ├── ConstruccionController.java  (POO-06)
    └── SonidoController.java        (POO-06)
```

El dominio no importa de Spring ni de los services; el repositorio vive en su propio paquete; los controllers no imponen reglas de negocio.

## Invariantes

| Clase / service | Regla |
|---|---|
| `ValidadorMinecraft.validarEntidad` | vida, daño base y velocidad no negativos |
| `ValidadorMinecraft.validarDistancia` | distancia no negativa |
| `ValidadorMinecraft.validarJugador` | vida máxima 20; armadura no negativa |
| `ValidadorMinecraft.validarCarga` | carga de explosión no negativa |
| `ValidadorMinecraft.validarEntidadCompleta` | precisión de arco entre 0 y 100; velocidad aumentada no negativa |
| `MobService.actualizar` | un `Creeper` con `explotado = true` no admite cambios (`409`) |
| `ExplosionService` | un creeper sin carga no detona (`422`); uno ya explotado no vuelve a detonar (`409`) |

La pregunta de anclaje sigue vigente: *¿puede otra clase, u otra capa, dejar el objeto en un estado imposible?* La entrada HTTP pasa siempre por un service que valida antes de tocar el repositorio; las entidades ya no tienen setters que lancen excepciones ni reglas de juego.

## Cómo probar

```bash
./mvnw -q test          # dominio + services + API (CRUD, 404, 409, 422, protocolo)
./mvnw spring-boot:run  # arranca en http://localhost:8080
```

| Prueba | Esperado |
|---|---|
| `GET /` | Mensaje de servicio vivo |
| `POST /api/minecraft/mobs` válido | `201` + `id` + `tipo` |
| `GET /api/minecraft/mobs` | Lista lo persistido en H2 |
| `GET /api/minecraft/mobs/{id}` | `200` o `404` |
| `PUT /api/minecraft/mobs/{id}` | `200`; un `GET` posterior mantiene el cambio |
| `PUT` sobre un creeper explotado | `409`; el `GET` posterior sigue con vida 0 |
| `POST` ilegal (vida negativa) | `400`; la colección no crece |
| `DELETE /api/minecraft/mobs/{id}` | `204`; `GET` posterior `404` |
| `GET /api/minecraft/danables` | `Mob` y `Jugador` por la interfaz |
| `POST /api/minecraft/simulaciones/explosion` | explosión + cadena; `GET` posterior persistido |
| Cada hija de excepción | `404`, `422` y `409`, con `tipo` distinto en el JSON |

## Bitácora de IA

Ver [`BITACORA.md`](BITACORA.md): asistente, modelo exacto y resumen de prompts.

## Licencia

Apache License 2.0 — ver [`LICENSE`](LICENSE).

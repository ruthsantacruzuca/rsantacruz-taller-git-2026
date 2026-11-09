# rsantacruz-taller-git-2026

API REST del modelado de entidades de **Minecraft** (Lenguaje de Programación 3, edición 2026).
Ejercicio **POO-08**: CRUD REST, persistencia en **H2**, reglas de juego, simulación, jerarquía de excepciones, protocolo de dominio y colección de **Insomnia**. Continúa el modelado de POO-06 (paquetes, constructores, sobrecarga y sobreescritura).

**Commit de la solución:** se indica en [`ESPECIFICACIONES-POO-08.md`](ESPECIFICACIONES-POO-08.md) y en Classroom.

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

En la consola se entra con la URL `jdbc:h2:file:./data/minecraft`, usuario `sa` y contraseña vacía. Ahí se ve la tabla `mobs` (familia persistida) y la tabla `jugadores`.

---

## Endpoints

| Verbo y URL | Qué hace | Éxito |
|---|---|---|
| `GET /` | Confirma que el servicio vive | 200 |
| `POST /api/minecraft/mobs` | Crea un `Creeper` o un `Zombie` (el JSON lleva `tipo`) | 201 + `Location` |
| `GET /api/minecraft/mobs` | Lista lo persistido (opcional `?hostil=true`) | 200 |
| `GET /api/minecraft/mobs/{id}` | Lee un ítem por su id | 200 / 404 |
| `PUT /api/minecraft/mobs/{id}` | Reemplaza el recurso completo y persiste | 200 / 404 |
| `DELETE /api/minecraft/mobs/{id}` | Borra la fila | 204 / 404 |
| `POST /api/minecraft/simulaciones/explosion` | Corre la simulación de explosiones y persiste | 200 |
| `GET /api/minecraft/danables` | Lee mobs y jugadores **por el tipo de la interfaz** `Danable` | 200 |
| `POST /api/minecraft/jugadores` | Crea un jugador (segunda familia de `Danable`) | 201 |
| `GET /api/minecraft/jugadores` | Lista jugadores persistidos | 200 |
| `GET /api/minecraft/construir` | (POO-06) construye una entidad por URL | 200 / 400 |
| `GET /api/minecraft/sonidos` | (POO-06) sonido de cada hija, tratadas como `Entidad` | 200 |

El recurso de la API es el sustantivo **`mobs`**; el verbo HTTP dice la operación. El número de la URL es el `id` que asigna H2 (`GenerationType.IDENTITY`), no un índice de una lista en el controller.

---

## Las dos reglas del juego

Participan las tres clases de la familia persistida: `Mob` (padre), `Creeper` y `Zombie` (hijas).

1. **Explosión:** un `Creeper` que detona reparte su `cargaExplosion` como daño a todos los mobs que siguen con vida.
2. **Reacción en cadena:** si un `Creeper` muere por esa explosión y todavía tenía carga, detona a su vez sobre los mobs que quedan vivos.

Las reglas viven en `domain/SimuladorExplosiones` (`danoDeExplosion`, `propagaEnCadena`, `simular`) y en los mensajes del dominio (`Creeper.explotar`, `Mob.recibirDano`). El controller sólo carga, delega y vuelve a persistir.

### Simulación (caso feliz)

```bash
# 1) crear dos creepers y un zombie (el segundo creeper arranca la cadena)
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":24}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"CREEPER","vida":10,"danoBase":6,"velocidad":3,"cargaExplosion":12}'
curl -s -X POST localhost:8080/api/minecraft/mobs -H 'Content-Type: application/json' \
  -d '{"tipo":"ZOMBIE","vida":30,"danoBase":4,"velocidad":3}'

# 2) simular: el creeper 1 detona
curl -s -X POST localhost:8080/api/minecraft/simulaciones/explosion -H 'Content-Type: application/json' \
  -d '{"creeperId":1}'

# 3) el estado quedó persistido
curl -s localhost:8080/api/minecraft/mobs
```

La respuesta de la simulación lista cada explosión, sus víctimas (vida antes y después) y el estado final. El `GET` posterior muestra lo mismo ya guardado en H2.

---

## Jerarquía de excepciones y handlers

La jerarquía es **no comprobada** (hereda de `RuntimeException`): el dominio no obliga a cada capa a declararla y el handler HTTP la traduce a JSON. Se eligió no comprobada porque una regla de negocio incumplida no es un error recuperable en el punto donde se lanza.

| Excepción | Rol | Handler | HTTP | JSON (`tipo`) |
|---|---|---|---|---|
| `DominioException` | base de la jerarquía | `ManejadorDeErrores.dominio` | 500 | `dominio` |
| `RecursoNoEncontradoException` | hija 1: el recurso no existe | `ManejadorDeErrores.noEncontrado` | **404** | `recurso-no-encontrado` |
| `ReglaDeJuegoException` | hija 2: una regla no se puede cumplir | `ManejadorDeErrores.reglaDeJuego` | **422** | `regla-de-juego` |

Las hermanas se distinguen por el **código HTTP** y por el campo `tipo` del cuerpo:

```json
{"tipo":"recurso-no-encontrado","mensaje":"No existe el mob con id 999"}
{"tipo":"regla-de-juego","mensaje":"El mob 2 es Zombie y no puede detonar: la simulación necesita un Creeper"}
```

Además, un JSON que rompe un invariante de construcción responde `400` con `tipo: json-ilegible` y el mensaje del dominio (por ejemplo `"La vida no puede ser negativa: -5"`). Un `POST` ilegal **no** agrega basura a la colección.

---

## Interfaz `Danable` (protocolo de POO-07)

```java
public interface Danable {
    int getVida();
    void recibirDano(int dano);
    String estado();
}
```

La implementan **dos familias distintas**: `Mob` (y sus hijas `Creeper`/`Zombie`) y `Jugador`. El endpoint `GET /api/minecraft/danables` arma una `List<Danable>` con lo persistido de ambas familias y pide `estado()` sin preguntar la clase concreta. Así el protocolo sirve para hablar con objetos de jerarquías diferentes.

---

## Persistencia y herencia JPA

La familia persistida es `Mob` (padre) con `Creeper` y `Zombie` (dos hijas independientes). Se eligió **`InheritanceType.SINGLE_TABLE`**: una sola tabla `mobs` con una columna discriminadora `tipo` (`CREEPER` / `ZOMBIE`). En este dominio hay pocos subtipos y casi todas las consultas son sobre la familia completa, así que una tabla evita *joins* y mantiene simple el CRUD.

- **Discriminador en la base:** `@DiscriminatorColumn(name = "tipo")` + `@DiscriminatorValue` en cada hija.
- **Discriminador en el JSON:** Jackson con `@JsonTypeInfo(property = "tipo")` y `@JsonSubTypes`, de modo que un `POST` con `"tipo":"CREEPER"` construye la hija correcta y un `GET` informa el subtipo.
- **Constructor sin argumentos:** JPA lo necesita. Queda **`protected`** (no público) para no abrir una puerta que deje el objeto en estado ilegal.
- **Ocultamiento frente a JPA:** el mapeo usa acceso por **propiedad** (el `@Id` está en el getter), así Hibernate llama a los **setters**, que validan. Si una fila trajera `vida` negativa, el setter la rechaza: la capa de tabla no puede romper el invariante que sostiene el dominio.

`Jugador` es una entidad aparte (tabla `jugadores`) que comparte `Entidad` como `@MappedSuperclass`; existe para mostrar el protocolo entre familias distintas.

---

## Diagrama

```mermaid
classDiagram
  direction LR

  class Danable {
    <<interface>>
    +int getVida()
    +void recibirDano(int dano)
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
    +recibirDano(int dano)
    +String estado()
  }

  class Creeper {
    <<Entity>>
    -int cargaExplosion
    +int explotar()
    +String emitirSonido()
  }

  class Zombie {
    <<Entity>>
    +String emitirSonido()
    +String atacar()
    +String atacar(Jugador jugador)
  }

  class Jugador {
    <<Entity>>
    -Long id
    -int armadura
    +recibirDano(int dano)
    +String estado()
  }

  class SimuladorExplosiones {
    +ResultadoExplosion simular(Creeper, List~Mob~)
    +int danoDeExplosion(Creeper)
    +boolean propagaEnCadena(Mob)
  }

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
  class ManejadorDeErrores {
    <<RestControllerAdvice>>
    +handler RecursoNoEncontrado : 404
    +handler ReglaDeJuego : 422
  }

  Entidad <|-- Mob
  Entidad <|-- Jugador
  Mob <|-- Creeper
  Mob <|-- Zombie

  Danable <|.. Mob : implementa
  Danable <|.. Jugador : implementa

  MobController --> MobRepository : usa
  SimulacionController --> MobRepository : usa
  SimulacionController --> SimuladorExplosiones : delega
  DanableController --> MobRepository : usa
  DanableController --> JugadorRepository : usa
  MobRepository ..> Mob : persiste
  JugadorRepository ..> Jugador : persiste
  SimuladorExplosiones ..> Creeper : regla 1 y 2
  SimuladorExplosiones ..> Mob : daña

  DominioException <|-- RecursoNoEncontradoException
  DominioException <|-- ReglaDeJuegoException
  ManejadorDeErrores ..> RecursoNoEncontradoException : atiende
  ManejadorDeErrores ..> ReglaDeJuegoException : atiende
```

El diagrama distingue **herencia** (`<|--`), **implementación de interfaz** (`<|..`), **uso del repositorio** (`-->` / `..>`) y **jerarquía de excepciones** (`<|--` desde `DominioException`).

---

## Respuestas a «Qué hay que pensar»

**Recurso y verbo.** El sustantivo es `mobs`; la URL nombra el recurso y el verbo HTTP dice la operación (`POST` crea, `GET` lee, `PUT` actualiza, `DELETE` borra). La simulación es un recurso propio (`simulaciones/explosion`) porque es una acción con efecto sobre varios objetos.

**Identidad.** La clave es `id`, un `Long` que asigna H2 con `IDENTITY`. El JSON de creación lo **omite**: `id` está marcado como `READ_ONLY` en Jackson, así que el cliente no lo elige ni lo pisa.

**Herencia y tablas.** Se eligió `SINGLE_TABLE` con la columna `tipo`; en este dominio los subtipos son pocos y se consulta la familia completa, y evita *joins*. El subtipo se distingue en el JSON con el discriminador `tipo` de Jackson.

**Interfaz y repositorio.** El repositorio de Spring Data **es una interfaz**: declaro los mensajes y el marco genera el proxy. El controller depende de `MobRepository`, no de una implementación, igual que en POO-07 se depende de un protocolo: puedo cambiar la implementación sin tocar el controller.

**Protocolo del dominio.** `Danable` es la interfaz propia. La usa `GET /api/minecraft/danables`. Sí puede hablar con familias distintas: `Mob` y `Jugador` la implementan y el endpoint las trata a todas como `Danable`.

**Ocultamiento.** Las reglas numéricas las sostienen los constructores y setters del dominio. JPA entra por los setters (acceso por propiedad), así que una fila no puede saltear la validación. La prueba es el `POST` ilegal de Insomnia: responde `400` y la colección **no** crece.

**PUT y PATCH.** Se implementó **PUT**: reemplaza el recurso entero. Un campo omitido en el cuerpo queda en su valor por defecto (`0` para los `int`), que es un estado legal; por eso conviene mandar el objeto completo.

**DELETE.** Borrar significa quitar la fila (`repositorio.delete`). El verbo habla con el repositorio y responde `204`; un `GET` posterior es `404`. No hay borrado lógico en este modelo.

**Dos reglas.** Son la explosión y la reacción en cadena (arriba). Cambian el estado de varios objetos a la vez. El recurso es `POST /api/minecraft/simulaciones/explosion`; un `GET /api/minecraft/mobs` posterior muestra el resultado ya persistido.

**Excepciones.** `DominioException` (base), `RecursoNoEncontradoException` (recurso inexistente) y `ReglaDeJuegoException` (regla incumplida). Son tipos distintos y tienen handlers distintos (404 y 422). El JSON de error lleva `tipo` y `mensaje`.

**Cambiar de base.** Si mañana se cambia H2 por otra base, se tocan `pom.xml` y `application.properties` (driver, URL, credenciales). Las entidades, repositorios, dominio y controllers se quedan quietos.

**Quién lanza la excepción.** La lanza el **dominio** (`SimuladorExplosiones`, constructores, setters); el controller no decide reglas, sólo traduce con el `@RestControllerAdvice`.

---

## Colección de Insomnia

El archivo versionado es [`insomnia/poo-08-insomnia.json`](insomnia/poo-08-insomnia.json).

**Importar:** en Insomnia → *Application* → *Import* → *From File* → elegir `insomnia/poo-08-insomnia.json`. Trae un entorno `Base` con `base_url`, `creeper_id` y `zombie_id`.

**Orden de ejecución** (con el servicio corriendo y base limpia):

1. `01 POST crear creeper` y `02 POST crear zombie` → copiar los `id` devueltos en las variables `creeper_id` y `zombie_id` del entorno.
2. `03 GET colección` → aparecen los dos.
3. `04 GET creeper por id` → coincide.
4. `05 PUT actualizar creeper` → cambia la vida; `04` de nuevo la muestra persistida.
5. `07 POST ilegal (vida negativa)` → `400` y la colección no creció.
6. `08 GET id inexistente` → `404`.
7. `09 GET danables (interfaz)` → `Mob` y `Jugador` por el tipo `Danable`.
8. `10 POST simular explosion` → corre las dos reglas; `03` muestra el estado persistido.
9. `11 Hija 1: RecursoNoEncontrado (404)` y `12 Hija 2: ReglaDeJuego (422)` → cada handler con su código.
10. `06 DELETE zombie` → `204`; `08`/`04` con ese id dan `404`.

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

La firma simple delega con `this(...)` / `super(...)` en la completa, así toda firma deja el objeto legal con valores por defecto.

**Sobreescritura** — misma firma, implementación propia en la hija; se decide al ejecutar.

| Método de la base | Lo sobreescriben |
|---|---|
| `Entidad.emitirSonido()` *(abstracto)* | `Mob`, `Jugador`, `Zombie`, `Creeper`, `Esqueleto`, `Cerdo`, `Aldeano`, `ZombiePequeno` |
| `Entidad.atacar()` | `Zombie` |
| `Entidad.setVida(int)` | `Jugador` (agrega el tope de 20) |

En dos frases: **sobrecarga** = dos métodos con el mismo nombre en la misma clase, cambia la lista de parámetros, se resuelve al compilar. **Sobreescritura** = la hija redefine un método de la base con la misma firma, se resuelve al ejecutar.

---

## Paquetes

```text
src/main/java/py/edu/uc/lp3/
├── Application.java                 ← sólo arranca el servicio
├── domain/                          ← reglas del juego
│   ├── Entidad.java                 (@MappedSuperclass)
│   ├── Mob.java                     (@Entity, SINGLE_TABLE)
│   ├── Creeper.java                 (@Entity)
│   ├── Zombie.java                  (@Entity)
│   ├── Jugador.java                 (@Entity, otra familia)
│   ├── Danable.java                 (interface / protocolo)
│   ├── SimuladorExplosiones.java    (las dos reglas)
│   ├── Esqueleto.java, Cerdo.java, Aldeano.java, ZombiePequeno.java, FabricaEntidades.java
│   └── exception/
│       ├── DominioException.java
│       ├── RecursoNoEncontradoException.java
│       └── ReglaDeJuegoException.java
├── repository/                      ← interfaces Spring Data
│   ├── MobRepository.java
│   └── JugadorRepository.java
└── rest/controller/                 ← entrada HTTP
    ├── IndexController.java
    ├── MobController.java           (CRUD)
    ├── JugadorController.java
    ├── SimulacionController.java
    ├── DanableController.java
    ├── ManejadorDeErrores.java      (@RestControllerAdvice)
    ├── ConstruccionController.java  (POO-06)
    └── SonidoController.java        (POO-06)
```

El dominio no importa de Spring; el repositorio vive en su propio paquete; los controllers no imponen reglas de negocio.

## Invariantes

| Clase | Regla |
|---|---|
| `Entidad` | vida, daño base y velocidad no negativos |
| `Entidad.atacar(int)` | distancia no negativa |
| `Jugador` | vida máxima 20; armadura no negativa |
| `Creeper` | carga de explosión no negativa; la carga sólo aumenta con valores positivos |
| `Esqueleto` | precisión de arco entre 0 y 100 |
| `ZombiePequeno` | velocidad aumentada no negativa |
| `Mob.recibirDano` | daño no negativo; la vida nunca baja de 0 |

La pregunta de anclaje sigue vigente: *¿puede otra clase, u otra capa, dejar el objeto en un estado imposible?* No: ni el controller (sólo pasa `int` a constructores y setters) ni JPA (entra por los setters que validan) pueden saltear las reglas.

## Cómo probar

```bash
./mvnw -q test          # dominio (constructores, sobrecarga, sobreescritura, simulación) + API (CRUD, 404, 422, protocolo)
./mvnw spring-boot:run  # arranca en http://localhost:8080
```

| Prueba | Esperado |
|---|---|
| `GET /` | Mensaje de servicio vivo |
| `POST /api/minecraft/mobs` válido | `201` + `id` + `tipo` |
| `GET /api/minecraft/mobs` | Lista lo persistido en H2 |
| `GET /api/minecraft/mobs/{id}` | `200` o `404` |
| `PUT /api/minecraft/mobs/{id}` | `200`; un `GET` posterior mantiene el cambio |
| `POST` ilegal (vida negativa) | `400`; la colección no crece |
| `DELETE /api/minecraft/mobs/{id}` | `204`; `GET` posterior `404` |
| `GET /api/minecraft/danables` | `Mob` y `Jugador` por la interfaz |
| `POST /api/minecraft/simulaciones/explosion` | explosión + cadena; `GET` posterior persistido |
| Cada hija de excepción | `404` vs `422`, con `tipo` distinto en el JSON |

## Bitácora de IA

Ver [`BITACORA.md`](BITACORA.md): asistente, modelo exacto y resumen de prompts.

## Licencia

Apache License 2.0 — ver [`LICENSE`](LICENSE).

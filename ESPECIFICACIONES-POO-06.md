# Especificaciones aplicadas — Ejercicio POO-06

**Asignatura:** Lenguaje de Programación 3 (CYT646) — edición 2026
**Ejercicio:** POO-06 — Revisión, paquetes, constructores y sobrecarga
**Estudiante:** Ruth Santacruz ([@ruthsantacruzuca](https://github.com/ruthsantacruzuca))
**Repositorio:** [`rsantacruz-taller-git-2026`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026)
**Dominio elegido:** entidades de **Minecraft**

**Enlace exacto al commit de la solución:**

```
https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/84be129c56ed2cfef0fd97479a9a2b5d03fce5ee
```

---

## 1. Objetivo

Publicar un servicio HTTP con **Spring Boot** sobre el modelado de entidades de Minecraft hecho el 2 y el 3 de septiembre, de modo que otro compañero pueda clonar el repositorio, arrancarlo con `./mvnw spring-boot:run` y usarlo por la web sin abrir un `main()` en el IDE.

El ejercicio aprieta el diseño: **paquetes con sentido** (dominio separado de la entrada HTTP), **constructores simples y sobrecargados**, **sobrecarga** de un mensaje del dominio y **sobreescritura** de un método, todo manteniendo el encapsulamiento: que ninguna capa pueda dejar el objeto en un estado imposible.

## 2. Consignas y cómo se cumplieron

| # | Consigna del enunciado | Dónde está en el código |
|---|---|---|
| 1 | Paquetes que coinciden con las carpetas, según el template: dominio en `domain`, REST en `rest/controller`, `Application` sólo para arrancar | `src/main/java/py/edu/uc/lp3/` → `Application.java`, `domain/`, `rest/controller/` |
| 2 | El modelado de septiembre entra al proyecto y compila | `domain/Entidad`, `Mob`, `Jugador`, `Zombie`, `ZombiePequeno`, `Creeper`, `Esqueleto`, `Cerdo`, `Aldeano` |
| 3 | Método **abstracto** en la clase base, **sobreescrito** por dos hijas independientes | `Entidad.emitirSonido()` (abstracto) → lo sobreescriben `Zombie` y `Creeper`, entre otras |
| 4 | Dos servicios REST: `IndexController` en `GET /` y un controller que construye desde la URL | `rest/controller/IndexController` y `rest/controller/ConstruccionController` |
| 5 | JSON con la respuesta del mensaje abstracto a cada hija, hablándole como al tipo padre, sin `if` por tipo | `rest/controller/SonidoController`, que mapea `Entidad::emitirSonido` sobre `List<Entidad>` |
| 6 | Constructores **simples y sobrecargados**; la hija llama a `super`; cada firma deja estado legal | `Entidad(int)` / `Entidad(int,int,int)`; `Mob(int)`, `Mob(int,boolean)`, `Mob(int,int,int,boolean)`; una firma simple en cada hija; todas delegan con `this(...)` o `super(...)` |
| 7 | Sobrecargar al menos un mensaje del dominio | `Entidad.atacar()` y `Entidad.atacar(int distancia)`; `Zombie` también tiene `atacar(Jugador)` |
| 8 | README con diagrama Mermaid alineado con `src/` y apartado de sobrecarga/sobreescritura | `README.md` |
| 9 | `BITACORA.md` con marca del agente, modelo exacto y resumen de prompts | `BITACORA.md` (OpenCode, `opencode/big-pickle`) |

### Constructores: firma simple y firma completa

```java
// domain/Entidad.java — firma simple delega en la completa
public Entidad(int vida) {
    this(vida, DANO_BASE_POR_DEFECTO, VELOCIDAD_POR_DEFECTO);
}

public Entidad(int vida, int danoBase, int velocidad) {
    this.vida = validarVida(vida);
    ...
}
```

La fábrica repite el mismo esquema y expone ambas firmas por URL:

```java
public static Entidad construir(String tipo, int vida)                      // simple
public static Entidad construir(String tipo, int vida, int danoBase,
                                int velocidad, int valorExtra)               // sobrecargado
```

### Sobreescritura

`Entidad.emitirSonido()` es abstracto. `Zombie` y `Creeper` lo sobreescriben con la misma firma y textos distintos. `Zombie` además sobreescribe `atacar()`.

### Sobrecarga de un mensaje del dominio

```java
public String atacar()                       // sin datos
public String atacar(int distancia)          // con distancia: baja el daño y valida >= 0
```

Ambas se exponen en el JSON: campos `ataqueSinDatos` y `ataqueConDistancia`.

## 3. Reglas de validación (invariantes)

El estado sólo cambia por constructores y mensajes que validan. Nada de esto puede romperse desde el controller.

| Clase | Regla |
|---|---|
| `Entidad` | vida, daño base y velocidad no negativos |
| `Entidad.atacar(int)` | distancia no negativa |
| `Jugador` | vida máxima 20; armadura no negativa |
| `Creeper` | carga de explosión no negativa; sólo aumenta con valores positivos |
| `Esqueleto` | precisión de arco entre 0 y 100 |
| `ZombiePequeno` | velocidad aumentada no negativa |

## 4. Cómo probarlo

```bash
./mvnw -q test                 # 12 tests: constructores, sobrecarga, sobreescritura, esqueleto
./mvnw spring-boot:run          # arranca en http://localhost:8080
```

Con el servicio corriendo (navegador, `curl` o Insomnia):

| Prueba | Esperado |
|---|---|
| `GET /` | Mensaje de «servicio vivo» con autor y endpoints |
| `GET /api/minecraft/construir?tipo=creeper&vida=20` | `200` y `"constructor": "simple (construir(tipo, vida))"` con los defectos (`danoBase: 1`, `velocidad: 1`, carga 12) |
| `GET /api/minecraft/construir?tipo=creeper&vida=20&danoBase=6&velocidad=3&valorExtra=24&distancia=30` | `200` y `"constructor": "sobrecargado (…)"`; `ataqueSinDatos` con daño 6 y `ataqueConDistancia` con daño reducido a 3 |
| `GET /api/minecraft/construir?tipo=zombie&vida=20&danoBase=4&velocidad=3` | `200`; `ataqueSinDatos` = `"muerde con las dos manos y hace 4 de daño"` (**sobreescritura** de `Zombie`) |
| `GET /api/minecraft/construir?tipo=zombie&vida=-5` | `400` → `{"error":"La vida no puede ser negativa: -5"}` |
| `GET /api/minecraft/construir?tipo=zombie&vida=20&danoBase=4` | `400` → aviso de que `danoBase` y `velocidad` van juntos o ninguno |
| `GET /api/minecraft/construir?tipo=zombie&vida=20&danoBase=4&velocidad=3&distancia=-1` | `400` → `{"error":"La distancia no puede ser negativa: -1"}` |
| `GET /api/minecraft/construir?tipo=loquesea&vida=20` | `400` → `{"error":"Tipo de entidad desconocido: loquesea"}` |
| `GET /api/minecraft/sonidos` | JSON con el sonido de cada hija, tratadas todas como `Entidad` (sin `if` por tipo) |

Ejemplo de `curl`:

```bash
curl "http://localhost:8080/api/minecraft/construir?tipo=zombie&vida=20&danoBase=4&velocidad=3&distancia=30"
```

## 5. Pregunta de anclaje

> *¿Qué ocurre si el controller asigna a mano la vida o la munición?*

**No puede.** Los atributos de `Entidad` son `private`; la capa HTTP sólo recibe `int` por la URL y esos valores entran **por el constructor**, que valida antes de asignar. Si la vida es negativa o supera el máximo de un `Jugador`, la clase lanza `IllegalArgumentException`, el controller la traduce en un `400` con el mensaje del dominio, y **el objeto inválido nunca llega a existir**. El mismo camino vale para `atacar(int distancia)`: una distancia negativa no altera nada, rechaza.

## 6. Entregables de este ejercicio

1. **Este archivo** (Markdown) en Classroom, con el enlace al commit.
2. **README** con licencia Apache 2.0, diagrama Mermaid alineado con `src/` y el apartado de sobrecarga/sobreescritura.
3. **`BITACORA.md`** con la marca del asistente, el modelo exacto y el resumen de prompts.
4. **Código** en el repositorio público, con historial legible en commits separados.
5. Usuario de GitHub en el chat del curso: **`ruthsantacruzuca`**.

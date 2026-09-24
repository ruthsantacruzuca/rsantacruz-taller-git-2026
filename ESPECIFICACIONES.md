# Especificación del Ejercicio

**Asignatura:** Lenguaje de Programación 3 (CYT646) — Edición 2026
**Repositorio:** `rsantacruz-taller-git-2026`
**Dominio modelado:** Entidades de Minecraft

---

## 1. Objetivo

Convertir un programita que corre con un `main()` local en una **API REST** que otra persona pueda usar por HTTP, versionando con **Git/GitHub** el modelado orientado a objetos hecho en clase (2 y 3 de septiembre) y agregando capas de entrada HTTP sin romper el encapsulamiento del dominio.

## 2. Consignas

### A. Repositorio y Spring Boot
- Repositorio público creado en GitHub con README inicial y licencia **Apache License 2.0**.
- Starter de **Spring Boot 3.x (Maven, Java 21, Spring Web)** generado en start.spring.io y colocado **en la raíz** del clone (`pom.xml`, `mvnw`, `.mvn/`, `src/`).
- El modelo se versiona como **clases**, no editando el README. El README muestra el diagrama Mermaid al final.

### B. Modelado previo
- Las clases del diagrama del 2 y 3 de septiembre se copian al starter ajustando el `package` a las carpetas.
- `./mvnw -q compile` hasta `BUILD SUCCESS`. Correcciones de código en commits propios (`fix: …`), sin reescribir historia ya pusheada.

### C. Colaboración
- En el repo del compañero se crea una rama (`INICIALES-contribucion-lp3`), se agrega una especialización que no pise archivos y se abre un *Compare & pull request* a su `main`. Las correcciones del reviewer se hacen en la misma rama y se pushean; el PR se actualiza solo.

### D. Criterios del modelado
1. **Ocultamiento:** el estado cambia por mensajes, no por asignación directa.
2. **Reglas en la clase:** tras el constructor y cada mensaje, el objeto sigue válido.
3. **Herencia de comportamiento:** al sobrescribir se decide si se llama al padre o se reemplaza.
4. **`private` por defecto.**
5. Tratar cualquier entidad de forma uniforme **sin un `if` por cada tipo**.

### E y F. Controllers y polimorfismo
- `IndexController`: `GET /` confirma que el servicio está vivo (autor, dominio).
- Controller de construcción por URL: los `@RequestParam` van al constructor/fábrica de una clase del modelo; si el valor es ilegal, **la clase rechaza** (400), el controller no "arregla" el estado.
- **Parte F:** método **abstracto** en la clase base (`Entidad.emitirSonido()`), implementado con realización distinta en **dos hijas independientes**. Un controller responde JSON con el resultado de pedirle ese mensaje a cada hija tratándolas por el **tipo padre**, sin `if (esCreeper)` / `if (esZombie)` para armar el texto.

### G. SSH (solo en el host)
- La clave `ed25519` se genera **en la máquina del desarrollador** (no en el sandbox del agente) y se carga en GitHub (`SSH and GPG keys`).

## 3. Criterios de aceptación

- [ ] Repo público con README inicial y Apache License 2.0.
- [ ] Spring Boot (Maven, Java 21, Spring Web) en la raíz; `./mvnw spring-boot:run` levanta en `http://localhost:8080`.
- [ ] Modelado compilando; commits de **clases** (`feat:`), no del README.
- [ ] `git log` / `git checkout HASH` / vuelta a `main`.
- [ ] Rama y PR en el repo del compañero.
- [ ] `GET /` responde.
- [ ] Endpoint de construcción por URL responde JSON y rechaza valores ilegales con `400`.
- [ ] `Entidad` abstracta, método abstracto `emitirSonido()`, dos hijas independientes implementándolo, y endpoint JSON que las trata como `Entidad` sin `if` por tipo.
- [ ] README con diagrama **Mermaid** alineado con el código.
- [ ] Especificaciones adjuntadas en Classroom (este archivo: Markdown, PDF o Word).

## 4. Cómo probar

```bash
./mvnw -q compile
./mvnw spring-boot:run
```

Abrir en el navegador o en Insomnia:

| Endpoint | Resultado esperado |
|---|---|
| `GET http://localhost:8080/` | Mensaje de "servicio vivo" |
| `GET http://localhost:8080/api/minecraft/construir?tipo=creeper&vida=20&danoBase=6&velocidad=3&valorExtra=24` | JSON `{ "tipo": "Creeper", "vida": 20, …, "sonido": "…" }` |
| `GET http://localhost:8080/api/minecraft/construir?tipo=creeper&vida=-5&danoBase=6&velocidad=3` | `400` con `{ "error": "La vida no puede ser negativa: -5" }` |
| `GET http://localhost:8080/api/minecraft/construir?tipo=jugador&vida=25&danoBase=2&velocidad=6` | `400` (vida máxima de jugador: 20) |
| `GET http://localhost:8080/api/minecraft/sonidos` | JSON con el `sonido` de las dos hijas, informado por cada objeto |

## 5. Preguntas de la revisión

- ¿El controller usa el tipo padre (`Entidad`) o pregunta el tipo concreto?
- ¿Se puede dejar el objeto inválido desde otra clase o desde el controller?

## 6. Entregables

1. Repositorio GitHub `rsantacruz-taller-git-2026` (público).
2. Este archivo de especificaciones adjuntado en Classroom (`.md`, PDF o Word).
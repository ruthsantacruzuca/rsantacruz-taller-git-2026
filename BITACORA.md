# Bitácora de uso de asistente de IA

**Ejercicio:** POO-06 — Revisión, paquetes, constructores y sobrecarga
**Asignatura:** Lenguaje de Programación 3 (CYT646) — edición 2026
**Estudiante:** Ruth Santacruz ([@ruthsantacruzuca](https://github.com/ruthsantacruzuca))

## Datos de la asistencia

| Dato | Valor |
|---|---|
| **Marca / asistente** | OpenCode (opencode.ai) |
| **Modelo LLM exacto** | `opencode/big-pickle` |
| **Entorno** | consola interactiva con acceso al terminal y a los archivos del repositorio |
| **Repo** | `rsantacruz-taller-git-2026` |

Sí se usó asistencia de IA en este ejercicio, y también en el taller de Git previo sobre el mismo repositorio. Esta bitácora resume los prompts en frases propias.

## Resumen de los prompts

Agrupados por etapa, en orden aproximado de trabajo:

1. **Entender el enunciado.** Pedí leer el enunciado del ejercicio, la rúbrica y el template de paquetes de la cátedra, y extraer qué cambiaba con respecto a lo hecho en septiembre (paquetes `domain` / `rest.controller`, constructores simples y sobrecargados, sobrecarga de un mensaje, entrega de commit + README + bitácora).
2. **Reorganizar paquetes.** Pedí mover las clases de `modelo/` y `web/` a `domain/` y `rest/controller/`, renombrar `MinecraftApplication` a `Application`, actualizar `package`/`import` y dejar el árbol igual al del template.
3. **Constructores.** Pedí agregar una firma simple a `Entidad`, `Mob`, `Jugador`, `Zombie`, `ZombiePequeno`, `Creeper`, `Esqueleto`, `Cerdo` y `Aldeano`, con valores por defecto, delegando con `this(...)` / `super(...)` en la firma completa, y crear la segunda firma de `FabricaEntidades`.
4. **Controller por URL.** Pedí que el endpoint construya con un constructor u otro según los parámetros que lleguen, que informe en el JSON cuál firma usó, y que un parámetro incompleto o ilegal devuelva `400` con el mensaje del dominio (sin que el controller "arregle" el estado).
5. **Sobrecarga de un mensaje del dominio.** Pedí agregar `atacar()` y `atacar(int distancia)` en `Entidad`, validando la distancia, y exponer ambas llamadas en el JSON de construcción.
6. **Sobreescritura.** Pedí que `Zombie` sobreescriba `atacar()` con su propio texto y que el JSON muestre textos distintos para tipos distintos sin `if` por tipo.
7. **Tests.** Pedí tests de sobrecarga de constructores (estado legal con defectos), de la firma completa, de rechazo de vida/distance negativos, y de sobreescritura (los textos no son el mismo para `Zombie` y `Creeper`).
8. **Historial Git.** Pedí separar los cambios en commits con mensaje legible: refactor de paquetes, constructores, sobrecarga/sobreescritura.
9. **Documentación.** Pedí reescribir el README con el diagrama Mermaid alineado con `src/`, una sección que explique qué cambió con la sobrecarga y con la sobreescritura y cómo distinguirlas, la respuesta a la pregunta de anclaje sobre asignar la vida a mano, y las tablas de reglas de validación.
10. **Esta bitácora.** Pedí redactarla con la marca del asistente, el modelo exacto y el resumen de prompts.
11. **Especificaciones de Classroom.** Pedí el archivo con objetivo, consignas y cómo probarlo, aplicado al dominio de Minecraft, con el enlace al commit.
12. **Verificación.** Pedí correr `./mvnw test` y arrancar el servicio para comprobar por `curl` los endpoints y los `400`, y detener el proceso después.

## Qué NO se delegó

- La **clave SSH** se generó en la terminal de la estudiante en su propia máquina (requisito del taller: fuera del sandbox del agente).
- Las decisiones de modelado (qué clases, qué reglas de validación, nombres de los mensajes) y la **defensa oral** del ejercicio.
- La revisión y aprobación de cada commit antes de pushear, y la entrega en Classroom.

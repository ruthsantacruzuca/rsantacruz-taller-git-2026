# Bitácora de uso de asistente de IA

**Ejercicios:** POO-06 (paquetes, constructores, sobrecarga y sobreescritura) y POO-08 (CRUD REST, H2, reglas de juego, excepciones e Insomnia)
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

---

# Bitácora — Ejercicio POO-08 (CRUD, H2, reglas, excepciones e Insomnia)

## Datos de la asistencia

| Dato | Valor |
|---|---|
| **Marca / asistente** | OpenCode (opencode.ai) |
| **Modelo LLM exacto** | `opencode/big-pickle` |
| **Entorno** | consola interactiva con acceso al terminal y a los archivos del repositorio |
| **Repo** | `rsantacruz-taller-git-2026` |

Sí se usó asistencia de IA también en este ejercicio. Esta sección resume los prompts en frases propias.

## Resumen de los prompts (POO-08)

1. **Leer el enunciado y la rúbrica.** Pedí traer el texto del ejercicio POO-08 y la rúbrica de ocho criterios, y listar qué piezas nuevas exige (JPA/H2, CRUD, dos reglas, simulación persistida, jerarquía de excepciones, interfaz, colección Insomnia).
2. **Dependencias y H2.** Pedí agregar `spring-boot-starter-data-jpa` y el driver de H2, configurar la base en archivo, `ddl-auto` y la consola, y explicar cada propiedad en el README.
3. **Persistencia de tres clases.** Pedí convertir la familia `Mob` con `Creeper` y `Zombie` en entidades JPA, con `SINGLE_TABLE` y columna discriminadora, manteniendo el constructor vacío en `protected` y el acceso por setters para que JPA no saltee las validaciones.
4. **Interfaz de dominio.** Pedí una interfaz propia (`Danable`) implementada por dos familias (`Mob` y `Jugador`) y un endpoint de lectura que hable con el tipo de la interfaz.
5. **Repositorios.** Pedí un repositorio Spring Data para la clase base y un par de mensajes de consulta declarados en la interfaz.
6. **CRUD REST.** Pedí `POST`, `GET` de colección, `GET` por id, `PUT` y `DELETE`, con discriminación del subtipo en el JSON (Jackson) y `id` de solo lectura.
7. **Dos reglas del juego.** Pedí modelar la explosión del creeper y la reacción en cadena como métodos del dominio, no del controller.
8. **Simulación persistida.** Pedí un endpoint que dispare la simulación, devuelva el detalle (víctimas, vida antes/después) y deje el estado guardado para un `GET` posterior.
9. **Excepciones y handlers.** Pedí una base y dos hijas con nombres de regla, un handler por tipo y códigos HTTP distintos (404 vs 422), justificando excepciones no comprobadas.
10. **Tests.** Pedí tests del simulador (dos reglas y rechazo) y de la API con MockMvc (CRUD, POST ilegal, 404, 422 y endpoint de la interfaz).
11. **Colección Insomnia.** Pedí el archivo exportado con entorno, CRUD, POST ilegal, id inexistente, endpoint de la interfaz, simulación y un pedido por cada excepción hija.
12. **Documentación.** Pedí actualizar el README (arranque, propiedades de H2, diagrama Mermaid con tres clases, interfaz, repositorio, controller y excepciones, las dos reglas en una frase, respuestas a «Qué hay que pensar» e importación de Insomnia), la bitácora y las especificaciones para Classroom.
13. **Verificación.** Pedí correr los tests, arrancar el servicio y recorrer por `curl` el CRUD, la simulación con cadena y cada error, y detener el proceso después.

## Qué NO se delegó (POO-08)

- Las decisiones de modelado (qué familia persistir, qué dos reglas contar, nombres de las excepciones) y la **defensa oral**.
- La revisión de cada commit antes de pushear y la entrega en Classroom.

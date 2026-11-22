# Especificaciones — POO-09 APIs cruzadas

**Estudiante:** Ruth Santacruz ([@ruthsantacruzuca](https://github.com/ruthsantacruzuca))
**Repo:** `rsantacruz-taller-git-2026`
**Dominio:** Minecraft

## Enunciado aplicado

- **A1 (mi servicio):** `rsantacruz-taller-git-2026`, base `http://localhost:8080`, OpenAPI `http://localhost:8080/v3/api-docs`.
- **A2 (compañero consultado):** Matías Duarte, `Cemelele/mfdc-taller-git-2026`, base `http://localhost:8081`, OpenAPI previsto `http://localhost:8081/v3/api-docs`.
- **A3 (compañero consultado):** Sebastián Gómez de la Fuente, `SGDFM/sgomezdelafuente-talle-git-2026`, base `http://localhost:8082`, OpenAPI previsto `http://localhost:8082/v3/api-docs`.

En el reparto del grupo figuré como A2, pero esta entrega es individual y cada estudiante es A1 de la suya; por eso en mi entrega A1 soy yo y mis dos consumos ajenos son Matías (A2) y Sebastián (A3).

**Escenario en una frase:** una detonación de Creeper es legal para mi servicio solo cuando A2 acepta el ciclo de vida y el daño por distancia y A3 acepta la carga mínima y el radio; en ese caso guardo la jugada en mi H2 y si alguno rechaza no guardo nada e indico de quién fue el rechazo.

## Reglas ajenas citadas

- **A2 — Matías:** la electrificación carga el Creeper y eleva el daño base de 15 a 25; la simulación baja el daño con la distancia dentro del radio y el Creeper que detona muere y no puede volver a explotar ni revivir (`409`).
- **A3 — Sebastián:** el Creeper solo detona si está vivo y con carga ≥ 3, y el daño base es `cargaExplosion * 4`; una entidad viva solo recibe daño si su distancia de Manhattan no supera el radio, con reducción de daño por la armadura del Jugador.

## Cómo probarlo

1. Levantar los tres servicios con los comandos del README: A1 en 8080, A2 en 8081 y A3 en 8082.
2. Crear entidades de prueba en A2 y A3 siguiendo los pedidos 04 a 08 de `insomnia/poo-09-insomnia.json` y copiar los IDs al entorno.
3. Ejecutar el pedido 09 (escenario feliz). A1 llama a A2 y A3; la respuesta `201` trae `guardado: true` y las respuestas de ambos compañeros.
4. Ejecutar el pedido 10: `GET /api/minecraft/escenarios` debe mostrar la jugada persistida en el H2 de A1.
5. Ejecutar el pedido 11 (rechazo de A2): A2 responde `409` por Creeper consumido, A1 devuelve `companero: A2` y el `GET` sigue mostrando la misma cantidad de jugadas.
6. Ejecutar el pedido 12 (rechazo de A3): A2 acepta con entidades nuevas, A3 rechaza por Creeper sin vida, A1 devuelve `companero: A3` y la base sigue igual.
7. Abrir `http://localhost:8080/v3/api-docs` o `http://localhost:8080/swagger-ui.html` para ver CRUD, simulación, errores de las dos reglas y el endpoint de escenario.

## Corrección de POO-08

POO-08 tuvo observaciones en los criterios 2 y 3. Quedaron cerradas con la capa `service`, el estado `explotado` persistido y el `409` correspondiente; el detalle está en `CORRECCION-POO-08.md`.

## Enlace al commit de la solución

[`99d8c29d4ef4ef901e1cb5ad41379bfe1010e6dd`](https://github.com/ruthsantacruzuca/rsantacruz-taller-git-2026/commit/99d8c29d4ef4ef901e1cb5ad41379bfe1010e6dd)

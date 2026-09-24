# rsantacruz-taller-git-2026

API REST del modelado de entidades de **Minecraft** (Lenguaje de Programación 3, 2026).

## Levantar

```bash
./mvnw spring-boot:run
```

Abre en `http://localhost:8080`.

| Endpoint | Qué hace |
|---|---|
| `GET /` | Confirma que el servicio está vivo |
| `GET /api/minecraft/construir?tipo=creeper&vida=20&danoBase=6&velocidad=3&valorExtra=24` | Construye una entidad por URL (si el valor es ilegal, la clase lo rechaza con `400`) |
| `GET /api/minecraft/sonidos` | JSON con el sonido que informa cada hija (tratadas como `Entidad`) |

---

## Diagrama de clases (2 y 3 de septiembre, actualizado)

```mermaid
classDiagram
  class Entidad {
    <<abstract>>
    -int vida
    -int danoBase
    -int velocidad
    +Entidad(int vida, int danoBase, int velocidad)
    +int getVida()
    +void setVida(int vida)
    +int getDanoBase()
    +void setDanoBase(int danoBase)
    +int getVelocidad()
    +void setVelocidad(int velocidad)
    +String emitirSonido()*
  }

  class Jugador {
    -int armadura
    +Jugador(int vida, int danoBase, int velocidad, int armadura)
    +int getArmadura()
    +void setArmadura(int armadura)
    +void setVida(int vida)
    +void recibirDano(int dano)
    +void curar(int cantidad)
    +String emitirSonido()
  }

  class Mob {
    -boolean hostil
    +Mob(int vida, int danoBase, int velocidad, boolean hostil)
    +boolean isHostil()
    +void setHostil(boolean hostil)
    +String emitirSonido()
  }

  class Zombie {
    +Zombie(int vida, int danoBase, int velocidad)
    +void atacar(Jugador jugador)
    +String emitirSonido()
  }

  class ZombiePequeno {
    -int velocidadAumentada
    +ZombiePequeno(int vida, int danoBase, int velocidad, int velocidadAumentada)
    +int getVelocidadTotal()
    +String emitirSonido()
  }

  class Creeper {
    -int cargaExplosion
    +Creeper(int vida, int danoBase, int velocidad, int cargaExplosion)
    +int getCargaExplosion()
    +void setCargaExplosion(int cargaExplosion)
    +void aumentarCarga(int cantidad)
    +void explotar()
    +String emitirSonido()
  }

  class Cerdo {
    -boolean montable
    +Cerdo(int vida, int danoBase, int velocidad, boolean montable)
    +boolean isMontable()
    +void setMontable(boolean montable)
    +String emitirSonido()
  }

  class Aldeano {
    -boolean comercializacion
    +Aldeano(int vida, int danoBase, int velocidad, boolean comercializacion)
    +boolean puedeComercializar()
    +void setComercializacion(boolean comercializacion)
    +String emitirSonido()
  }

  Entidad <|-- Jugador
  Entidad <|-- Mob
  Mob <|-- Zombie
  Mob <|-- Creeper
  Mob <|-- Cerdo
  Mob <|-- Aldeano
  Zombie <|-- ZombiePequeno
  Zombie --> Jugador : ataca
```

`*` = método abstracto. `<<abstract>>` = clase abstracta.

---

## Jerarquía

```text
Entidad (abstracta)
├── Jugador
└── Mob
    ├── Zombie
    │   └── ZombiePequeno
    ├── Creeper
    ├── Cerdo
    └── Aldeano
```

## Encapsulamiento

Todos los atributos son `private`; el estado cambia por mensajes: los `set` validan antes de modificar y el constructor deja el objeto válido.

No se permite `jugador.vida = 100`; se usa `jugador.setVida(100)` y la clase decide si es legal.

## Reglas de validación

| Clase | Regla |
|---|---|
| `Entidad` | vida, daño base y velocidad no negativos |
| `Jugador` | vida máxima 20; armadura no negativa |
| `Creeper` | carga de explosión no negativa; la carga solo aumenta con valores positivos |
| `ZombiePequeno` | velocidad aumentada no negativa |

## Conceptos de POO aplicados

- **Encapsulamiento:** atributos privados, acceso por mensajes controlados.
- **Herencia:** `Entidad → Mob → Zombie → ZombiePequeno`, etc.
- **Sobrescritura:** `Jugador` sobrescribe `setVida()` para imponer su límite de 20; cada hija sobrescribe `emitirSonido()`.
- **Polimorfismo:** el controller pide `emitirSonido()` a cada entidad por el tipo padre `Entidad`, sin `if` por tipo.
- **Método abstracto:** `Entidad.emitirSonido()` declara un comportamiento común cuya forma concreta depende de cada hija.
- **Especialización:** cada subclase agrega su característica (armadura, carga de explosión, montable, comercialización, velocidad aumentada).

## Estructura del código

```text
src/main/java/py/edu/uc/lp3/rs/minecraft/
├── …/MinecraftApplication.java
├── modelo/   (Entidad, Mob, Jugador, Zombie, ZombiePequeno, Creeper, Cerdo, Aldeano, FabricaEntidades)
└── web/      (IndexController, ConstruccionController, SonidoController)
```

## Visualización del diagrama

El bloque Mermaid se renderiza solo en GitHub y puede editarse en https://mermaid.live.

## Licencia

Apache License 2.0 — ver `LICENSE`.
package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.domain.ZombiePequeno;

/**
 * Validaciones de datos del dominio. Viven en la capa service: las entidades
 * sólo guardan estado y no lanzan excepciones.
 */
public final class ValidadorMinecraft {

    private ValidadorMinecraft() {
    }

    public static void validarEntidad(int vida, int danoBase, int velocidad) {
        if (vida < 0) {
            throw new IllegalArgumentException("La vida no puede ser negativa: " + vida);
        }
        if (danoBase < 0) {
            throw new IllegalArgumentException("El daño base no puede ser negativo: " + danoBase);
        }
        if (velocidad < 0) {
            throw new IllegalArgumentException("La velocidad no puede ser negativa: " + velocidad);
        }
    }

    public static void validarMob(Mob mob) {
        validarEntidad(mob.getVida(), mob.getDanoBase(), mob.getVelocidad());
        if (mob instanceof Creeper creeper) {
            validarCarga(creeper.getCargaExplosion());
        }
    }

    public static void validarCarga(int carga) {
        if (carga < 0) {
            throw new IllegalArgumentException("La carga de explosión no puede ser negativa: " + carga);
        }
    }

    public static void validarJugador(Jugador jugador) {
        validarEntidad(jugador.getVida(), jugador.getDanoBase(), jugador.getVelocidad());
        if (jugador.getVida() > 20) {
            throw new IllegalArgumentException(
                    "La vida de un jugador no puede superar 20: " + jugador.getVida());
        }
        if (jugador.getArmadura() < 0) {
            throw new IllegalArgumentException("La armadura no puede ser negativa: " + jugador.getArmadura());
        }
    }

    public static void validarEntidadCompleta(Entidad entidad) {
        validarEntidad(entidad.getVida(), entidad.getDanoBase(), entidad.getVelocidad());
        if (entidad instanceof Creeper creeper) {
            validarCarga(creeper.getCargaExplosion());
        }
        if (entidad instanceof Jugador jugador) {
            if (jugador.getVida() > 20) {
                throw new IllegalArgumentException(
                        "La vida de un jugador no puede superar 20: " + jugador.getVida());
            }
            if (jugador.getArmadura() < 0) {
                throw new IllegalArgumentException("La armadura no puede ser negativa: " + jugador.getArmadura());
            }
        }
        if (entidad instanceof Esqueleto esqueleto) {
            if (esqueleto.getPrecisionArco() < 0 || esqueleto.getPrecisionArco() > 100) {
                throw new IllegalArgumentException(
                        "La precision del arco debe estar entre 0 y 100: " + esqueleto.getPrecisionArco());
            }
        }
        if (entidad instanceof ZombiePequeno zombie) {
            if (zombie.getVelocidadAumentada() < 0) {
                throw new IllegalArgumentException(
                        "La velocidad aumentada no puede ser negativa: " + zombie.getVelocidadAumentada());
            }
        }
    }

    public static void validarDistancia(int distancia) {
        if (distancia < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa: " + distancia);
        }
    }
}

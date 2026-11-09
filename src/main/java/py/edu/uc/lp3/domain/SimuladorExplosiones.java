package py.edu.uc.lp3.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import py.edu.uc.lp3.domain.exception.ReglaDeJuegoException;

/**
 * Las dos reglas de negocio del juego (Minecraft, explosiones) viven acá, no en
 * el controller.
 *
 * Regla 1: un creeper que detona reparte su carga de explosión como daño a todos
 *          los mobs que siguen con vida.
 * Regla 2: si un creeper muere por esa explosión y todavía tenía carga, detona a
 *          su vez (reacción en cadena) sobre los mobs que quedan vivos.
 */
public class SimuladorExplosiones {

    public ResultadoExplosion simular(Creeper detonador, List<Mob> mobs) {
        if (detonador == null) {
            throw new ReglaDeJuegoException("No hay un creeper detonador para la simulación");
        }
        if (danoDeExplosion(detonador) <= 0) {
            throw new ReglaDeJuegoException(
                    "El creeper " + detonador.getId() + " no tiene carga para explotar");
        }

        List<EventoExplosion> explosiones = new ArrayList<>();
        Set<Long> yaDetonados = new HashSet<>();
        Deque<Creeper> pendientes = new ArrayDeque<>();
        pendientes.add(detonador);

        while (!pendientes.isEmpty()) {
            Creeper creeper = pendientes.poll();
            if (!yaDetonados.add(creeper.getId())) {
                continue;
            }
            int carga = danoDeExplosion(creeper);
            if (carga <= 0) {
                continue;
            }
            List<Victima> victimas = new ArrayList<>();
            for (Mob mob : mobs) {
                if (mob == creeper || yaDetonados.contains(mob.getId()) || mob.getVida() <= 0) {
                    continue;
                }
                int vidaAntes = mob.getVida();
                mob.recibirDano(carga);
                victimas.add(new Victima(mob.getId(), mob.getClass().getSimpleName(), vidaAntes, mob.getVida()));
                if (propagaEnCadena(mob)) {
                    pendientes.add((Creeper) mob);
                }
            }
            creeper.explotar();
            explosiones.add(new EventoExplosion(creeper.getId(), carga, victimas));
        }

        List<EstadoMob> estadoFinal = mobs.stream()
                .map(mob -> new EstadoMob(mob.getId(), mob.getClass().getSimpleName(), mob.getVida()))
                .toList();
        return new ResultadoExplosion(explosiones, estadoFinal);
    }

    /** Regla 1: el daño que reparte una explosión es la carga del creeper. */
    public int danoDeExplosion(Creeper creeper) {
        return creeper.getCargaExplosion();
    }

    /** Regla 2: una víctima creeper muerta y con carga detona a su vez. */
    public boolean propagaEnCadena(Mob victima) {
        return victima instanceof Creeper creeper
                && creeper.getVida() == 0
                && creeper.getCargaExplosion() > 0;
    }

    public record ResultadoExplosion(List<EventoExplosion> explosiones, List<EstadoMob> estadoFinal) {
    }

    public record EventoExplosion(Long creeperId, int carga, List<Victima> victimas) {
    }

    public record Victima(Long id, String tipo, int vidaAntes, int vidaDespues) {
    }

    public record EstadoMob(Long id, String tipo, int vida) {
    }
}

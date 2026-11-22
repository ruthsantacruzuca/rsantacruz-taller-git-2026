package py.edu.uc.lp3.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.repository.MobRepository;
import py.edu.uc.lp3.service.exception.CreeperConsumidoException;
import py.edu.uc.lp3.service.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.service.exception.ReglaDeJuegoException;

/**
 * Capa service de las dos reglas de negocio del juego.
 *
 * Regla 1: un creeper que detona reparte su carga de explosión como daño a todos
 *          los mobs que siguen con vida.
 * Regla 2: si un creeper muere por esa explosión y todavía tenía carga, detona a
 *          su vez (reacción en cadena) sobre los mobs que quedan vivos.
 */
@Service
public class ExplosionService {

    private final MobRepository mobs;

    public ExplosionService(MobRepository mobs) {
        this.mobs = mobs;
    }

    @Transactional
    public ResultadoExplosion simular(Long creeperId) {
        if (creeperId == null) {
            throw new ReglaDeJuegoException("Falta el campo creeperId para simular la explosión");
        }

        List<Mob> todos = mobs.findAll();
        Mob detonador = todos.stream()
                .filter(mob -> creeperId.equals(mob.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el mob con id " + creeperId));
        if (!(detonador instanceof Creeper creeper)) {
            throw new ReglaDeJuegoException(
                    "El mob " + detonador.getId() + " es " + detonador.getClass().getSimpleName()
                            + " y no puede detonar: la simulación necesita un Creeper");
        }

        ResultadoExplosion resultado = simular(creeper, todos);
        mobs.saveAll(todos);
        return resultado;
    }

    public ResultadoExplosion simular(Creeper detonador, List<Mob> mobs) {
        if (detonador == null) {
            throw new ReglaDeJuegoException("No hay un creeper detonador para la simulación");
        }
        if (detonador.isExplotado()) {
            throw new CreeperConsumidoException(
                    "El creeper " + detonador.getId() + " ya explotó y no puede volver a detonar");
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
                mob.setVida(Math.max(0, mob.getVida() - carga));
                victimas.add(new Victima(mob.getId(), mob.getClass().getSimpleName(), vidaAntes, mob.getVida()));
                if (propagaEnCadena(mob)) {
                    pendientes.add((Creeper) mob);
                }
            }
            detonar(creeper);
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
                && !creeper.isExplotado()
                && creeper.getVida() == 0
                && creeper.getCargaExplosion() > 0;
    }

    private void detonar(Creeper creeper) {
        creeper.setVida(0);
        creeper.setCargaExplosion(0);
        creeper.setExplotado(true);
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

package py.edu.uc.lp3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Danable;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.repository.MobRepository;

/**
 * Arma la lectura del protocolo Danable con objetos de dos familias distintas:
 * los mobs persistidos y los jugadores persistidos.
 */
@Service
public class DanableService {

    private final MobRepository mobs;
    private final JugadorRepository jugadores;

    public DanableService(MobRepository mobs, JugadorRepository jugadores) {
        this.mobs = mobs;
        this.jugadores = jugadores;
    }

    @Transactional(readOnly = true)
    public ResumenDanables listar() {
        List<Danable> todos = new ArrayList<>();
        todos.addAll(mobs.findAll());
        todos.addAll(jugadores.findAll());

        List<FilaDanable> filas = todos.stream()
                .map(danable -> new FilaDanable(
                        danable.getClass().getSimpleName(),
                        danable.getVida(),
                        danable.estado()))
                .toList();
        return new ResumenDanables(filas.size(), filas);
    }

    public record ResumenDanables(int cantidad, List<FilaDanable> danables) {
    }

    public record FilaDanable(String tipo, int vida, String estado) {
    }
}

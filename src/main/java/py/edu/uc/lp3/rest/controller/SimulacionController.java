package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.domain.SimuladorExplosiones;
import py.edu.uc.lp3.domain.SimuladorExplosiones.ResultadoExplosion;
import py.edu.uc.lp3.domain.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.repository.MobRepository;

/**
 * Dispara la simulación de explosiones. El controller sólo carga, delega en el
 * dominio (SimuladorExplosiones) y vuelve a persistir; las reglas no viven acá.
 */
@RestController
@RequestMapping("/api/minecraft/simulaciones")
public class SimulacionController {

    private final MobRepository repositorio;
    private final SimuladorExplosiones simulador = new SimuladorExplosiones();

    public SimulacionController(MobRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping("/explosion")
    public ResultadoExplosion explosion(@RequestBody SimulacionRequest pedido) {
        if (pedido == null || pedido.creeperId() == null) {
            throw new ReglaDeJuegoException("Falta el campo creeperId para simular la explosión");
        }

        List<Mob> mobs = repositorio.findAll();
        Mob detonador = mobs.stream()
                .filter(mob -> pedido.creeperId().equals(mob.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el mob con id " + pedido.creeperId()));
        if (!(detonador instanceof Creeper creeper)) {
            throw new ReglaDeJuegoException(
                    "El mob " + detonador.getId() + " es " + detonador.getClass().getSimpleName()
                            + " y no puede detonar: la simulación necesita un Creeper");
        }

        ResultadoExplosion resultado = simulador.simular(creeper, mobs);
        repositorio.saveAll(mobs);
        return resultado;
    }

    public record SimulacionRequest(Long creeperId) {
    }
}

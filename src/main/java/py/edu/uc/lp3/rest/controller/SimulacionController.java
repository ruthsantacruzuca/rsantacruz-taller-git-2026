package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.service.ExplosionService;
import py.edu.uc.lp3.service.ExplosionService.ResultadoExplosion;

/**
 * Dispara la simulación de explosiones. El controller sólo delega en el service;
 * las reglas y las excepciones no viven acá.
 */
@RestController
@RequestMapping("/api/minecraft/simulaciones")
public class SimulacionController {

    private final ExplosionService service;

    public SimulacionController(ExplosionService service) {
        this.service = service;
    }

    @PostMapping("/explosion")
    public ResultadoExplosion explosion(@RequestBody SimulacionRequest pedido) {
        return service.simular(pedido == null ? null : pedido.creeperId());
    }

    public record SimulacionRequest(Long creeperId) {
    }
}

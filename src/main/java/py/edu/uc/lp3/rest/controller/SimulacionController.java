package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.service.ExplosionService;
import py.edu.uc.lp3.service.ExplosionService.ResultadoExplosion;

/**
 * Dispara la simulación de explosiones. El controller sólo delega en el service;
 * las reglas y las excepciones no viven acá.
 */
@RestController
@RequestMapping("/api/minecraft/simulaciones")
@Tag(name = "Simulacion POO-08", description = "Explosion y reaccion en cadena con sus errores de regla")
public class SimulacionController {

    private final ExplosionService service;

    public SimulacionController(ExplosionService service) {
        this.service = service;
    }

    @PostMapping("/explosion")
    @Operation(summary = "Simular explosion",
            description = "Regla 1: la carga del Creeper es el dano repartido. Regla 2: un Creeper victima con carga "
                    + "y vida 0 detona en cadena. El Creeper queda explotado y no revive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulacion persistida con explosion(es) y estado final"),
            @ApiResponse(responseCode = "404", description = "No existe el Creeper: recurso-no-encontrado"),
            @ApiResponse(responseCode = "409", description = "Creeper ya explotado: creeper-consumido"),
            @ApiResponse(responseCode = "422", description = "Regla de juego incumplida: regla-de-juego")
    })
    public ResultadoExplosion explosion(@RequestBody SimulacionRequest pedido) {
        return service.simular(pedido == null ? null : pedido.creeperId());
    }

    public record SimulacionRequest(Long creeperId) {
    }
}

package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioResponse;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.JugadaGuardada;
import py.edu.uc.lp3.service.EscenarioService;

@RestController
@RequestMapping("/api/minecraft/escenarios")
@Tag(name = "Escenario POO-09", description = "Jugada que consulta a A2 y A3 antes de persistirse en A1")
public class EscenarioController {

    private final EscenarioService service;

    public EscenarioController(EscenarioService service) {
        this.service = service;
    }

    @PostMapping("/verificado")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Juega el escenario verificado",
            description = "A1 consulta primero a A2 (ciclo de vida y distancia) y despues a A3 (carga y radio). "
                    + "Solo si los dos aceptan se guarda la jugada en el H2 de A1. "
                    + "Si uno rechaza, responde el codigo del companero con el campo companero para atribuir el error.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "A2 y A3 aceptaron; la jugada quedo persistida"),
            @ApiResponse(responseCode = "400", description = "Faltan datos del escenario"),
            @ApiResponse(responseCode = "409", description = "Un companero rechazo por conflicto de estado"),
            @ApiResponse(responseCode = "422", description = "Un companero rechazo por regla de juego"),
            @ApiResponse(responseCode = "502", description = "Un companero no respondio")
    })
    public EscenarioResponse jugar(@RequestBody EscenarioRequest pedido) {
        return service.jugar(pedido);
    }

    @GetMapping
    @Operation(summary = "Lista las jugadas guardadas",
            description = "Devuelve las jugadas que pasaron por las dos validaciones ajenas y se persistieron en A1.")
    public List<JugadaGuardada> listar() {
        return service.listar();
    }
}

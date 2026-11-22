package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.service.MobService;

@RestController
@RequestMapping("/api/minecraft/mobs")
@Tag(name = "CRUD de mobs", description = "Crea, lee, reemplaza y borra Creepers y Zombies persistidos en H2")
public class MobController {

    private final MobService service;

    public MobController(MobService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear mob", description = "El JSON lleva tipo CREEPER o ZOMBIE; los datos se validan en MobService.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mob creado y persistido"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos: argumento-invalido")
    })
    public ResponseEntity<Mob> crear(@RequestBody Mob mob) {
        Mob guardado = service.crear(mob);
        return ResponseEntity.created(URI.create("/api/minecraft/mobs/" + guardado.getId())).body(guardado);
    }

    @GetMapping
    public List<Mob> listar(@RequestParam(required = false) Boolean hostil) {
        return service.listar(hostil);
    }

    @GetMapping("/{id}")
    public Mob obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar mob", description = "No revive un Creeper explotado: esa operacion responde 409 creeper-consumido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mob reemplazado"),
            @ApiResponse(responseCode = "404", description = "No existe el id"),
            @ApiResponse(responseCode = "409", description = "El Creeper ya exploto: creeper-consumido"),
            @ApiResponse(responseCode = "422", description = "Regla de juego incumplida")
    })
    public Mob actualizar(@PathVariable Long id, @RequestBody Mob cambios) {
        return service.actualizar(id, cambios);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        service.borrar(id);
    }
}

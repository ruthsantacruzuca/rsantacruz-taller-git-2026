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

import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.service.MobService;

@RestController
@RequestMapping("/api/minecraft/mobs")
public class MobController {

    private final MobService service;

    public MobController(MobService service) {
        this.service = service;
    }

    @PostMapping
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
    public Mob actualizar(@PathVariable Long id, @RequestBody Mob cambios) {
        return service.actualizar(id, cambios);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        service.borrar(id);
    }
}

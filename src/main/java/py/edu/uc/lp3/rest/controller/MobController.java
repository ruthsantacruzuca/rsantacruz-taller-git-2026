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

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.domain.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.domain.exception.ReglaDeJuegoException;
import py.edu.uc.lp3.repository.MobRepository;

@RestController
@RequestMapping("/api/minecraft/mobs")
public class MobController {

    private final MobRepository repositorio;

    public MobController(MobRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping
    public ResponseEntity<Mob> crear(@RequestBody Mob mob) {
        Mob guardado = repositorio.save(mob);
        return ResponseEntity.created(URI.create("/api/minecraft/mobs/" + guardado.getId())).body(guardado);
    }

    @GetMapping
    public List<Mob> listar(@RequestParam(required = false) Boolean hostil) {
        return hostil == null ? repositorio.findAll() : repositorio.findByHostil(hostil);
    }

    @GetMapping("/{id}")
    public Mob obtener(@PathVariable Long id) {
        return buscar(id);
    }

    @PutMapping("/{id}")
    public Mob actualizar(@PathVariable Long id, @RequestBody Mob cambios) {
        Mob actual = buscar(id);
        if (actual.getClass() != cambios.getClass()) {
            throw new ReglaDeJuegoException("No se puede cambiar el tipo del mob " + id
                    + " (" + actual.getClass().getSimpleName() + " -> " + cambios.getClass().getSimpleName() + ")");
        }
        actual.setVida(cambios.getVida());
        actual.setDanoBase(cambios.getDanoBase());
        actual.setVelocidad(cambios.getVelocidad());
        actual.setHostil(cambios.isHostil());
        if (actual instanceof Creeper actualCreeper && cambios instanceof Creeper cambiosCreeper) {
            actualCreeper.setCargaExplosion(cambiosCreeper.getCargaExplosion());
        }
        return repositorio.save(actual);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        Mob actual = buscar(id);
        repositorio.delete(actual);
    }

    private Mob buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el mob con id " + id));
    }
}

package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.service.JugadorService;

@RestController
@RequestMapping("/api/minecraft/jugadores")
public class JugadorController {

    private final JugadorService service;

    public JugadorController(JugadorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Jugador> crear(@RequestBody Jugador jugador) {
        Jugador guardado = service.crear(jugador);
        return ResponseEntity.created(URI.create("/api/minecraft/jugadores/" + guardado.getId())).body(guardado);
    }

    @GetMapping
    public List<Jugador> listar() {
        return service.listar();
    }
}

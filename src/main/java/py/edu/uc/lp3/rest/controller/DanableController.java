package py.edu.uc.lp3.rest.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Danable;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.repository.MobRepository;

/**
 * Endpoint de lectura que habla con el tipo de la interfaz Danable, no con las
 * clases concretas: suma mobs persistidos (familia Mob) y jugadores persistidos
 * (familia Jugador) en una sola List<Danable>.
 */
@RestController
@RequestMapping("/api/minecraft/danables")
public class DanableController {

    private final MobRepository mobs;
    private final JugadorRepository jugadores;

    public DanableController(MobRepository mobs, JugadorRepository jugadores) {
        this.mobs = mobs;
        this.jugadores = jugadores;
    }

    @GetMapping
    public Map<String, Object> listar() {
        List<Danable> todos = new ArrayList<>();
        todos.addAll(mobs.findAll());
        todos.addAll(jugadores.findAll());

        List<Map<String, Object>> filas = new ArrayList<>();
        for (Danable danable : todos) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("tipo", danable.getClass().getSimpleName());
            fila.put("vida", danable.getVida());
            fila.put("estado", danable.estado());
            filas.add(fila);
        }

        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("cantidad", filas.size());
        cuerpo.put("danables", filas);
        return cuerpo;
    }
}

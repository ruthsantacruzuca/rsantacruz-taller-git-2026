package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.service.DanableService;
import py.edu.uc.lp3.service.DanableService.ResumenDanables;

/**
 * Endpoint de lectura que habla con el tipo de la interfaz Danable, no con las
 * clases concretas. El service suma mobs y jugadores persistidos.
 */
@RestController
@RequestMapping("/api/minecraft/danables")
public class DanableController {

    private final DanableService service;

    public DanableController(DanableService service) {
        this.service = service;
    }

    @GetMapping
    public ResumenDanables listar() {
        return service.listar();
    }
}

package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Servicio vivo: API REST del modelo de entidades de Minecraft (Lenguaje de Programación 3 - 2026). "
                + "Autor: Ruth Santacruz. "
                + "CRUD: /api/minecraft/mobs. Protocolo: /api/minecraft/danables. "
                + "Simulación: POST /api/minecraft/simulaciones/explosion. "
                + "Consola H2: /h2-console. "
                + "También /api/minecraft/construir y /api/minecraft/sonidos.";
    }
}

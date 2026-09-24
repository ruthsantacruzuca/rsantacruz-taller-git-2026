package py.edu.uc.lp3.rs.minecraft.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Servicio vivo: API REST del modelo de entidades de Minecraft (Lenguaje de Programación 3 - 2026). "
                + "Autor: Ruth Santacruz. "
                + "Probar /api/minecraft/construir y /api/minecraft/sonidos.";
    }
}
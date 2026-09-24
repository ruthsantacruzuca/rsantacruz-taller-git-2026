package py.edu.uc.lp3.rs.minecraft.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.rs.minecraft.modelo.Creeper;
import py.edu.uc.lp3.rs.minecraft.modelo.Entidad;
import py.edu.uc.lp3.rs.minecraft.modelo.Zombie;

@RestController
public class SonidoController {

    @GetMapping("/api/minecraft/sonidos")
    public Map<String, Object> sonidos() {
        List<Entidad> entidades = List.of(
                new Creeper(20, 6, 3, 24),
                new Zombie(20, 4, 3));
        List<String> mensajes = entidades.stream()
                .map(Entidad::emitirSonido)
                .toList();
        return Map.of(
                "cantidad", entidades.size(),
                "mensajes", mensajes);
    }
}
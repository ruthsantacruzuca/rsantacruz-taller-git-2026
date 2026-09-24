package py.edu.uc.lp3.rs.minecraft.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.rs.minecraft.modelo.Entidad;
import py.edu.uc.lp3.rs.minecraft.modelo.FabricaEntidades;

@RestController
public class ConstruccionController {

    @GetMapping("/api/minecraft/construir")
    public ResponseEntity<Map<String, Object>> construir(
            @RequestParam String tipo,
            @RequestParam int vida,
            @RequestParam int danoBase,
            @RequestParam int velocidad,
            @RequestParam(defaultValue = "0") int valorExtra) {
        try {
            Entidad entidad = FabricaEntidades.construir(tipo, vida, danoBase, velocidad, valorExtra);
            return ResponseEntity.ok(describir(entidad));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private Map<String, Object> describir(Entidad entidad) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("tipo", entidad.getClass().getSimpleName());
        cuerpo.put("vida", entidad.getVida());
        cuerpo.put("danoBase", entidad.getDanoBase());
        cuerpo.put("velocidad", entidad.getVelocidad());
        return cuerpo;
    }
}
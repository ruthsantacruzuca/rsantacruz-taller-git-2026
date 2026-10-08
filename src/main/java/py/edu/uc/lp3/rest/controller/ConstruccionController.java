package py.edu.uc.lp3.rest.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.FabricaEntidades;

@RestController
public class ConstruccionController {

    @GetMapping("/api/minecraft/construir")
    public ResponseEntity<Map<String, Object>> construir(
            @RequestParam String tipo,
            @RequestParam int vida,
            @RequestParam(required = false) Integer danoBase,
            @RequestParam(required = false) Integer velocidad,
            @RequestParam(defaultValue = "0") int valorExtra,
            @RequestParam(defaultValue = "0") int distancia) {
        try {
            boolean ninguno = danoBase == null && velocidad == null;
            boolean completo = danoBase != null && velocidad != null;
            if (ninguno) {
                return responder(FabricaEntidades.construir(tipo, vida), "simple (construir(tipo, vida))", distancia);
            }
            if (completo) {
                return responder(FabricaEntidades.construir(tipo, vida, danoBase, velocidad, valorExtra),
                        "sobrecargado (construir(tipo, vida, danoBase, velocidad, valorExtra))", distancia);
            }
            return ResponseEntity.badRequest().body(Map.of("error",
                    "Pasá danoBase y velocidad juntos (firma completa) o ninguno de los dos (firma simple)"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private ResponseEntity<Map<String, Object>> responder(Entidad entidad, String constructor, int distancia) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("tipo", entidad.getClass().getSimpleName());
        cuerpo.put("constructor", constructor);
        cuerpo.put("vida", entidad.getVida());
        cuerpo.put("danoBase", entidad.getDanoBase());
        cuerpo.put("velocidad", entidad.getVelocidad());
        cuerpo.put("sonido", entidad.emitirSonido());
        cuerpo.put("ataqueSinDatos", entidad.atacar());
        cuerpo.put("ataqueConDistancia", entidad.atacar(distancia));
        return ResponseEntity.ok(cuerpo);
    }
}
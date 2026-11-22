package py.edu.uc.lp3.rest.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.service.ConstruccionService;
import py.edu.uc.lp3.service.ConstruccionService.Construccion;

@RestController
public class ConstruccionController {

    private final ConstruccionService service;

    public ConstruccionController(ConstruccionService service) {
        this.service = service;
    }

    @GetMapping("/api/minecraft/construir")
    public ResponseEntity<Map<String, Object>> construir(
            @RequestParam String tipo,
            @RequestParam int vida,
            @RequestParam(required = false) Integer danoBase,
            @RequestParam(required = false) Integer velocidad,
            @RequestParam(defaultValue = "0") int valorExtra,
            @RequestParam(defaultValue = "0") int distancia) {
        Construccion construccion = service.construir(tipo, vida, danoBase, velocidad, valorExtra, distancia);
        return ResponseEntity.ok(responder(construccion, distancia));
    }

    private Map<String, Object> responder(Construccion construccion, int distancia) {
        Entidad entidad = construccion.entidad();
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("tipo", entidad.getClass().getSimpleName());
        cuerpo.put("constructor", construccion.constructor());
        cuerpo.put("vida", entidad.getVida());
        cuerpo.put("danoBase", entidad.getDanoBase());
        cuerpo.put("velocidad", entidad.getVelocidad());
        cuerpo.put("sonido", entidad.emitirSonido());
        cuerpo.put("ataqueSinDatos", entidad.atacar());
        cuerpo.put("ataqueConDistancia", entidad.atacar(distancia));
        return cuerpo;
    }
}

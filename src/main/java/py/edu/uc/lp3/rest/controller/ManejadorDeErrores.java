package py.edu.uc.lp3.rest.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.domain.exception.DominioException;
import py.edu.uc.lp3.domain.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.domain.exception.ReglaDeJuegoException;

/**
 * Un handler por tipo de la jerarquía de excepciones del dominio. Las hermanas
 * se distinguen por el código HTTP y por el campo "tipo" del JSON.
 */
@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(cuerpo("recurso-no-encontrado", ex.getMessage()));
    }

    @ExceptionHandler(ReglaDeJuegoException.class)
    public ResponseEntity<Map<String, Object>> reglaDeJuego(ReglaDeJuegoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(cuerpo("regla-de-juego", ex.getMessage()));
    }

    @ExceptionHandler(DominioException.class)
    public ResponseEntity<Map<String, Object>> dominio(DominioException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(cuerpo("dominio", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(cuerpo("argumento-invalido", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonIlegible(HttpMessageNotReadableException ex) {
        Throwable causa = ex.getMostSpecificCause();
        String mensaje = causa.getMessage() != null ? causa.getMessage() : "El cuerpo JSON no se pudo leer";
        return ResponseEntity.badRequest()
                .body(cuerpo("json-ilegible", mensaje));
    }

    private Map<String, Object> cuerpo(String tipo, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("tipo", tipo);
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}

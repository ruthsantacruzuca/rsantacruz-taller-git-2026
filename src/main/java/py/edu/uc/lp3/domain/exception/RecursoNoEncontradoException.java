package py.edu.uc.lp3.domain.exception;

/**
 * Hija 1: el recurso pedido no existe en la persistencia.
 * El handler la traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends DominioException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

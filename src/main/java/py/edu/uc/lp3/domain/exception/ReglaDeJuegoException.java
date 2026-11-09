package py.edu.uc.lp3.domain.exception;

/**
 * Hija 2: una regla del juego no se puede cumplir (por ejemplo, detonar un
 * creeper sin carga). El handler la traduce a HTTP 422.
 */
public class ReglaDeJuegoException extends DominioException {

    public ReglaDeJuegoException(String mensaje) {
        super(mensaje);
    }
}

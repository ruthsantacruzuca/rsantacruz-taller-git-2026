package py.edu.uc.lp3.service.exception;

/**
 * El recurso existe pero ya no admite cambios porque quedó consumido
 * (por ejemplo, un Creeper que ya explotó). El handler la traduce a HTTP 409.
 */
public class CreeperConsumidoException extends DominioException {

    public CreeperConsumidoException(String mensaje) {
        super(mensaje);
    }
}

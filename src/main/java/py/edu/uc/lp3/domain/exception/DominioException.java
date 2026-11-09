package py.edu.uc.lp3.domain.exception;

/**
 * Raíz de la jerarquía de excepciones del dominio.
 * Es no comprobada (RuntimeException): el dominio no obliga a cada capa a
 * declararla, y el handler HTTP la traduce a JSON.
 */
public class DominioException extends RuntimeException {

    public DominioException(String mensaje) {
        super(mensaje);
    }

    public DominioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

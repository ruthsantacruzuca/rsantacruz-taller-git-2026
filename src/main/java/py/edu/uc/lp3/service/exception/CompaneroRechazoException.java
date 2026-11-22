package py.edu.uc.lp3.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class CompaneroRechazoException extends RuntimeException {

    private final String companero;
    private final int codigoCompanero;
    private final String detalleCompanero;

    public CompaneroRechazoException(String companero, int codigoCompanero, String detalleCompanero) {
        super("La API " + companero + " rechazo el escenario con " + codigoCompanero);
        this.companero = companero;
        this.codigoCompanero = codigoCompanero;
        this.detalleCompanero = detalleCompanero;
    }

    public String getCompanero() {
        return companero;
    }

    public int getCodigoCompanero() {
        return codigoCompanero;
    }

    public HttpStatusCode getEstadoHttp() {
        try {
            return HttpStatus.valueOf(codigoCompanero);
        } catch (IllegalArgumentException ex) {
            return HttpStatusCode.valueOf(codigoCompanero);
        }
    }

    public String getDetalleCompanero() {
        return detalleCompanero;
    }
}

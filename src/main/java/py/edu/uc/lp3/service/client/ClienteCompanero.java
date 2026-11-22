package py.edu.uc.lp3.service.client;

import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;

public interface ClienteCompanero {

    String rol();

    Validacion validar(EscenarioRequest escenario);

    record Validacion(boolean aceptada, int codigo, String cuerpo) {
    }
}

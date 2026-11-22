package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.EscenarioVerificado;
import py.edu.uc.lp3.repository.EscenarioVerificadoRepository;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioResponse;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.JugadaGuardada;
import py.edu.uc.lp3.service.client.ClienteA2Matias;
import py.edu.uc.lp3.service.client.ClienteA3Sebastian;
import py.edu.uc.lp3.service.client.ClienteCompanero.Validacion;
import py.edu.uc.lp3.service.exception.CompaneroRechazoException;

@Service
public class EscenarioService {

    private final ClienteA2Matias a2;
    private final ClienteA3Sebastian a3;
    private final EscenarioVerificadoRepository jugadas;

    public EscenarioService(ClienteA2Matias a2, ClienteA3Sebastian a3,
            EscenarioVerificadoRepository jugadas) {
        this.a2 = a2;
        this.a3 = a3;
        this.jugadas = jugadas;
    }

    @Transactional
    public EscenarioResponse jugar(EscenarioRequest pedido) {
        if (pedido == null || pedido.a2() == null || pedido.a3() == null) {
            throw new IllegalArgumentException("El escenario necesita los datos de A2 y de A3");
        }

        Validacion validacionA2 = a2.validar(pedido);
        if (!validacionA2.aceptada()) {
            throw new CompaneroRechazoException(a2.rol(), validacionA2.codigo(), validacionA2.cuerpo());
        }

        Validacion validacionA3 = a3.validar(pedido);
        if (!validacionA3.aceptada()) {
            throw new CompaneroRechazoException(a3.rol(), validacionA3.codigo(), validacionA3.cuerpo());
        }

        EscenarioVerificado registro = jugadas.save(new EscenarioVerificado(
                pedido.descripcion(), a2.rol(), a3.rol(), validacionA2.cuerpo(), validacionA3.cuerpo()));
        return new EscenarioResponse(true, registro.getId(), a2.rol(), a3.rol(),
                validacionA2.cuerpo(), validacionA3.cuerpo());
    }

    @Transactional(readOnly = true)
    public List<JugadaGuardada> listar() {
        return jugadas.findAll().stream()
                .map(jugada -> new JugadaGuardada(jugada.getId(), jugada.getDescripcion(),
                        jugada.getCompaneroA2(), jugada.getCompaneroA3(), jugada.getRespuestaA2(),
                        jugada.getRespuestaA3(), jugada.getRegistradoEn()))
                .toList();
    }
}

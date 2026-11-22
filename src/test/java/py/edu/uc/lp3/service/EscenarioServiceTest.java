package py.edu.uc.lp3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import py.edu.uc.lp3.domain.EscenarioVerificado;
import py.edu.uc.lp3.repository.EscenarioVerificadoRepository;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioResponse;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.ExplosionA2;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.ExplosionA3;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.VictimaA2;
import py.edu.uc.lp3.service.client.ClienteA2Matias;
import py.edu.uc.lp3.service.client.ClienteA3Sebastian;
import py.edu.uc.lp3.service.client.ClienteCompanero.Validacion;
import py.edu.uc.lp3.service.exception.CompaneroRechazoException;

@ExtendWith(MockitoExtension.class)
class EscenarioServiceTest {

    @Mock
    private ClienteA2Matias a2;

    @Mock
    private ClienteA3Sebastian a3;

    @Mock
    private EscenarioVerificadoRepository jugadas;

    private EscenarioService servicio() {
        return new EscenarioService(a2, a3, jugadas);
    }

    private EscenarioRequest pedido() {
        return new EscenarioRequest("Triple detonacion verificada",
                new ExplosionA2(1L, true, List.of(new VictimaA2(2L, 1.5))),
                new ExplosionA3(1L, 2L, 3L, 5));
    }

    @Test
    void guardaLaJugadaSoloSiA2YA3Aceptan() {
        when(a2.rol()).thenReturn("A2");
        when(a3.rol()).thenReturn("A3");
        when(a2.validar(pedido())).thenReturn(new Validacion(true, 201, "{\"simulacionId\":1}"));
        when(a3.validar(pedido())).thenReturn(new Validacion(true, 200, "{\"danoExplosion\":12}"));
        when(jugadas.save(any(EscenarioVerificado.class))).thenAnswer(inv -> inv.getArgument(0));

        EscenarioResponse respuesta = servicio().jugar(pedido());

        assertThat(respuesta.guardado()).isTrue();
        assertThat(respuesta.companeroA2()).isEqualTo("A2");
        assertThat(respuesta.companeroA3()).isEqualTo("A3");
        verify(jugadas).save(any(EscenarioVerificado.class));
    }

    @Test
    void rechazoDeA2NoPersisteNiLlamaA3() {
        when(a2.rol()).thenReturn("A2");
        when(a2.validar(pedido())).thenReturn(new Validacion(false, 409, "{\"tipo\":\"estado-invalido\"}"));

        assertThatThrownBy(() -> servicio().jugar(pedido()))
                .isInstanceOf(CompaneroRechazoException.class)
                .extracting("companero").isEqualTo("A2");

        verify(a3, never()).validar(any());
        verify(jugadas, never()).save(any());
    }

    @Test
    void rechazoDeA3NoPersisteLaJugada() {
        when(a3.rol()).thenReturn("A3");
        when(a2.validar(pedido())).thenReturn(new Validacion(true, 201, "ok A2"));
        when(a3.validar(pedido())).thenReturn(new Validacion(false, 422, "{\"tipo\":\"carga-insuficiente\"}"));

        assertThatThrownBy(() -> servicio().jugar(pedido()))
                .isInstanceOf(CompaneroRechazoException.class)
                .extracting("companero").isEqualTo("A3");

        verify(jugadas, never()).save(any());
    }
}

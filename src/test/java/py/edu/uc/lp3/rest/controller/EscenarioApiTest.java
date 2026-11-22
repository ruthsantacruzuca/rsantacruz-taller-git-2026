package py.edu.uc.lp3.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.repository.EscenarioVerificadoRepository;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;
import py.edu.uc.lp3.service.client.ClienteA2Matias;
import py.edu.uc.lp3.service.client.ClienteA3Sebastian;
import py.edu.uc.lp3.service.client.ClienteCompanero.Validacion;

@SpringBootTest
@AutoConfigureMockMvc
class EscenarioApiTest {

    private static final String CUERPO_FELIZ = """
            {"descripcion":"Triple detonacion verificada",
             "a2":{"creeperId":1,"cargado":true,"victimas":[{"hostilId":2,"distancia":1.5}]},
             "a3":{"creeperId":1,"jugadorId":2,"zombieId":3,"radio":5}}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EscenarioVerificadoRepository jugadas;

    @MockBean
    private ClienteA2Matias a2;

    @MockBean
    private ClienteA3Sebastian a3;

    @BeforeEach
    void limpiar() {
        jugadas.deleteAll();
    }

    @Test
    void escenarioFelizGuardaLaJugadaYSeVuelveALeer() throws Exception {
        when(a2.rol()).thenReturn("A2");
        when(a3.rol()).thenReturn("A3");
        when(a2.validar(any(EscenarioRequest.class))).thenReturn(new Validacion(true, 201, "{\"simulacionId\":1}"));
        when(a3.validar(any(EscenarioRequest.class))).thenReturn(new Validacion(true, 200, "{\"danoExplosion\":12}"));

        mockMvc.perform(post("/api/minecraft/escenarios/verificado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_FELIZ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guardado").value(true))
                .andExpect(jsonPath("$.companeroA2").value("A2"))
                .andExpect(jsonPath("$.companeroA3").value("A3"))
                .andExpect(jsonPath("$.jugadaId").isNumber());

        mockMvc.perform(get("/api/minecraft/escenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descripcion").value("Triple detonacion verificada"))
                .andExpect(jsonPath("$[0].companeroA2").value("A2"));
    }

    @Test
    void rechazoDeA2AtribuyeElErrorYNoGuarda() throws Exception {
        when(a2.rol()).thenReturn("A2");
        when(a2.validar(any(EscenarioRequest.class)))
                .thenReturn(new Validacion(false, 409, "{\"tipo\":\"estado-invalido\"}"));

        mockMvc.perform(post("/api/minecraft/escenarios/verificado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_FELIZ))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.tipo").value("rechazo-companero"))
                .andExpect(jsonPath("$.companero").value("A2"))
                .andExpect(jsonPath("$.codigoCompanero").value(409));

        mockMvc.perform(get("/api/minecraft/escenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void rechazoDeA3AtribuyeElErrorYNoGuarda() throws Exception {
        when(a2.rol()).thenReturn("A2");
        when(a3.rol()).thenReturn("A3");
        when(a2.validar(any(EscenarioRequest.class))).thenReturn(new Validacion(true, 201, "ok A2"));
        when(a3.validar(any(EscenarioRequest.class)))
                .thenReturn(new Validacion(false, 422, "{\"tipo\":\"carga-insuficiente\"}"));

        mockMvc.perform(post("/api/minecraft/escenarios/verificado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_FELIZ))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.companero").value("A3"))
                .andExpect(jsonPath("$.detalleCompanero").value("{\"tipo\":\"carga-insuficiente\"}"));

        mockMvc.perform(get("/api/minecraft/escenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}

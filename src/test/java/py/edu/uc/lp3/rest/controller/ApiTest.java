package py.edu.uc.lp3.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.repository.MobRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MobRepository mobs;

    @Autowired
    private JugadorRepository jugadores;

    @BeforeEach
    void limpiarBase() {
        mobs.deleteAll();
        jugadores.deleteAll();
    }

    @Test
    void crudCompletoPersisteLosCambios() throws Exception {
        mockMvc.perform(post("/api/minecraft/mobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"CREEPER","vida":20,"danoBase":6,"velocidad":3,"cargaExplosion":24}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("CREEPER"))
                .andExpect(jsonPath("$.id").isNumber());

        Long id = mobs.findAll().get(0).getId();

        mockMvc.perform(get("/api/minecraft/mobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/minecraft/mobs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vida").value(20));

        mockMvc.perform(put("/api/minecraft/mobs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"CREEPER","vida":8,"danoBase":6,"velocidad":3,"cargaExplosion":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vida").value(8))
                .andExpect(jsonPath("$.cargaExplosion").value(10));

        mockMvc.perform(get("/api/minecraft/mobs/{id}", id))
                .andExpect(jsonPath("$.vida").value(8));

        mockMvc.perform(delete("/api/minecraft/mobs/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/minecraft/mobs/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.tipo").value("recurso-no-encontrado"));
    }

    @Test
    void postIlegalNoAgregaBasuraALaColeccion() throws Exception {
        mockMvc.perform(post("/api/minecraft/mobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"ZOMBIE","vida":-5,"danoBase":4,"velocidad":3}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipo").value("json-ilegible"));

        mockMvc.perform(get("/api/minecraft/mobs"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void idInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/minecraft/mobs/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.tipo").value("recurso-no-encontrado"));
    }

    @Test
    void simulacionPersisteElEstadoYLasDosReglasSeVen() throws Exception {
        Creeper detonador = mobs.save(new Creeper(20, 6, 3, 24));
        Creeper victima = mobs.save(new Creeper(10, 6, 3, 12));
        Zombie zombie = mobs.save(new Zombie(30, 4, 3));

        mockMvc.perform(post("/api/minecraft/simulaciones/explosion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creeperId\":" + detonador.getId() + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.explosiones", hasSize(2)))
                .andExpect(jsonPath("$.explosiones[1].creeperId").value(victima.getId()));

        mockMvc.perform(get("/api/minecraft/mobs/{id}", zombie.getId()))
                .andExpect(jsonPath("$.vida").value(0));
    }

    @Test
    void reglaIncumplidaDevuelve422DistintoDel404() throws Exception {
        Zombie zombie = mobs.save(new Zombie(20, 4, 3));

        mockMvc.perform(post("/api/minecraft/simulaciones/explosion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creeperId\":" + zombie.getId() + "}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.tipo").value("regla-de-juego"));
    }

    @Test
    void endpointDelProtocoloHablaConLaInterfazEntreFamilias() throws Exception {
        mobs.save(new Zombie(20, 4, 3));
        jugadores.save(new Jugador(20, 3, 2, 1));

        mockMvc.perform(get("/api/minecraft/danables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.danables[0].estado").exists())
                .andExpect(jsonPath("$.danables[1].estado").exists());
    }
}

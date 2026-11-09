package py.edu.uc.lp3.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.SimuladorExplosiones.EventoExplosion;
import py.edu.uc.lp3.domain.SimuladorExplosiones.ResultadoExplosion;
import py.edu.uc.lp3.domain.exception.ReglaDeJuegoException;

class SimuladorExplosionesTest {

    private final SimuladorExplosiones simulador = new SimuladorExplosiones();

    @Test
    void regla1ReparteLaCargaComoDanoATodosLosVivos() {
        Creeper creeper = new Creeper(20, 6, 3, 24);
        creeper.setId(1L);
        Zombie zombie = new Zombie(20, 4, 3);
        zombie.setId(2L);

        List<Mob> mobs = new ArrayList<>(List.of(creeper, zombie));
        ResultadoExplosion resultado = simulador.simular(creeper, mobs);

        assertEquals(0, zombie.getVida());
        assertEquals(0, creeper.getVida());
        assertEquals(1, resultado.explosiones().size());
        EventoExplosion evento = resultado.explosiones().get(0);
        assertEquals(24, evento.carga());
        assertEquals(1, evento.victimas().size());
        assertEquals(0, evento.victimas().get(0).vidaDespues());
    }

    @Test
    void regla2UnCreeperQueMuereConCargaDetonaEnCadena() {
        Creeper detonador = new Creeper(20, 6, 3, 24);
        detonador.setId(1L);
        Creeper victima = new Creeper(10, 6, 3, 12);
        victima.setId(2L);
        Zombie zombie = new Zombie(30, 4, 3);
        zombie.setId(3L);

        List<Mob> mobs = new ArrayList<>(List.of(detonador, victima, zombie));
        ResultadoExplosion resultado = simulador.simular(detonador, mobs);

        assertEquals(2, resultado.explosiones().size());
        assertEquals(2L, resultado.explosiones().get(1).creeperId());
        assertEquals(0, zombie.getVida());
        assertEquals(0, victima.getVida());

        Creeper listaParaCadena = new Creeper(0, 6, 3, 12);
        assertTrue(simulador.propagaEnCadena(listaParaCadena));
    }

    @Test
    void rechazaDetonarUnCreeperSinCarga() {
        Creeper creeper = new Creeper(20, 6, 3, 0);
        creeper.setId(1L);

        assertThrows(ReglaDeJuegoException.class,
                () -> simulador.simular(creeper, new ArrayList<>(List.of(creeper))));
    }
}

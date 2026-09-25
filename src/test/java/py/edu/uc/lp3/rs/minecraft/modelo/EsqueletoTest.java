package py.edu.uc.lp3.rs.minecraft.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EsqueletoTest {

    @Test
    void rechazaPrecisionFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> new Esqueleto(20, 5, 2, -1));
        assertThrows(IllegalArgumentException.class, () -> new Esqueleto(20, 5, 2, 101));
    }

    @Test
    void disparaUsandoDanoBaseSinAbrirEstado() {
        Esqueleto esqueleto = new Esqueleto(20, 5, 2, 80);
        Jugador jugador = new Jugador(20, 3, 2, 1);

        esqueleto.dispararA(jugador);

        assertEquals(16, jugador.getVida());
        assertTrue(esqueleto.emitirSonido().contains("esqueleto"));
    }
}

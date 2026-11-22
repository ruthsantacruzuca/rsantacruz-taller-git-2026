package py.edu.uc.lp3.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.service.ValidadorMinecraft;

class EsqueletoTest {

    @Test
    void elServiceRechazaPrecisionFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidadorMinecraft.validarEntidadCompleta(new Esqueleto(20, 5, 2, -1)));
        assertThrows(IllegalArgumentException.class,
                () -> ValidadorMinecraft.validarEntidadCompleta(new Esqueleto(20, 5, 2, 101)));
    }

    @Test
    void conservaElSonidoPropioDeLaHija() {
        Esqueleto esqueleto = new Esqueleto(20, 5, 2, 80);

        assertTrue(esqueleto.emitirSonido().contains("esqueleto"));
    }
}

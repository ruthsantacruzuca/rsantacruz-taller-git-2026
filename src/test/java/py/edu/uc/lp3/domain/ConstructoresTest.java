package py.edu.uc.lp3.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConstructoresTest {

    @Test
    void constructorSimpleDeCreeperDejaEstadoLegalConLosDefectos() {
        Creeper creeper = new Creeper(20);

        assertEquals(20, creeper.getVida());
        assertEquals(Entidad.DANO_BASE_POR_DEFECTO, creeper.getDanoBase());
        assertEquals(Entidad.VELOCIDAD_POR_DEFECTO, creeper.getVelocidad());
        assertEquals(12, creeper.getCargaExplosion());
    }

    @Test
    void constructorSobrecargadoDeCreeperRespetaCadaValor() {
        Creeper creeper = new Creeper(20, 6, 3, 24);

        assertEquals(6, creeper.getDanoBase());
        assertEquals(3, creeper.getVelocidad());
        assertEquals(24, creeper.getCargaExplosion());
    }

    @Test
    void constructorSimpleDeZombieQuedaHostilYElCerdoNoMontable() {
        assertTrue(new Zombie(20).isHostil());
        assertFalse(new Cerdo(20).isMontable());
    }

    @Test
    void constructorSimpleDeJugadorHeredaElLimiteDeVida() {
        assertEquals(20, new Jugador(20).getVida());
        assertThrows(IllegalArgumentException.class, () -> new Jugador(25));
    }

    @Test
    void todaLaJerarquiaRechazaVidaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new Zombie(-1));
        assertThrows(IllegalArgumentException.class, () -> new Esqueleto(-1));
        assertThrows(IllegalArgumentException.class, () -> new Jugador(-1));
    }
}

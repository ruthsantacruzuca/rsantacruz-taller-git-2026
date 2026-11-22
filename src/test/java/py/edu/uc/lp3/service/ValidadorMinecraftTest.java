package py.edu.uc.lp3.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;

class ValidadorMinecraftTest {

    @Test
    void rechazaVidaNegativaEnLaFamiliaMob() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidadorMinecraft.validarMob(new Zombie(-1, 4, 3)));
    }

    @Test
    void rechazaCargaNegativaDeUnCreeper() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidadorMinecraft.validarMob(new Creeper(20, 6, 3, -1)));
    }

    @Test
    void rechazaJugadorConVidaMayorAlMaximoOArmaduraNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> ValidadorMinecraft.validarJugador(new Jugador(25, 3, 2, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> ValidadorMinecraft.validarJugador(new Jugador(20, 3, 2, -1)));
    }

    @Test
    void aceptaEntidadesLegales() {
        assertDoesNotThrow(() -> ValidadorMinecraft.validarMob(new Creeper(20, 6, 3, 24)));
        assertDoesNotThrow(() -> ValidadorMinecraft.validarJugador(new Jugador(20, 3, 2, 1)));
    }
}

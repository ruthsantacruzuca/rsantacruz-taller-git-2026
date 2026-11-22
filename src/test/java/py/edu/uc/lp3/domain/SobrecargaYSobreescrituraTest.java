package py.edu.uc.lp3.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.service.ValidadorMinecraft;

class SobrecargaYSobreescrituraTest {

    @Test
    void atacarSobrecargadoTieneDosListasDeArgumentos() {
        Zombie zombie = new Zombie(20, 4, 3);

        String sinDatos = zombie.atacar();
        String conDistancia = zombie.atacar(30);

        assertNotEquals(sinDatos, conDistancia);
        assertEquals("ataca desde 30 bloques con 1 de daño", conDistancia);
    }

    @Test
    void elServiceRechazaDistanciaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> ValidadorMinecraft.validarDistancia(-1));
    }

    @Test
    void sobreescrituraCambiaElTextoSegunLaClaseHija() {
        Entidad zombie = new Zombie(20, 4, 3);
        Entidad creeper = new Creeper(20, 6, 3, 24);

        assertNotEquals(zombie.atacar(), creeper.atacar());
        assertEquals("muerde con las dos manos y hace 4 de daño", zombie.atacar());
    }

    @Test
    void elControllerHablaConElTipoPadreSinPreguntarElTipo() {
        Entidad[] entidades = { new Zombie(20, 4, 3), new Creeper(20, 6, 3, 24), new Aldeano(20) };

        for (Entidad entidad : entidades) {
            entidad.emitirSonido();
            entidad.atacar();
            entidad.atacar(10);
        }
    }
}

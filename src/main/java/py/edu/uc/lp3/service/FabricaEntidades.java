package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.domain.ZombiePequeno;

/**
 * Fábrica de POO-06, ahora en la capa service. El dominio no conoce el tipo
 * pedido por URL ni lanza excepciones por un tipo desconocido.
 */
public final class FabricaEntidades {

    private FabricaEntidades() {
    }

    public static Entidad construir(String tipo, int vida) {
        validarTipo(tipo);
        return switch (tipo.toLowerCase()) {
            case "creeper" -> new Creeper(vida);
            case "jugador" -> new Jugador(vida);
            case "zombie" -> new Zombie(vida);
            case "zombiepequeno" -> new ZombiePequeno(vida);
            case "esqueleto" -> new Esqueleto(vida);
            case "cerdo" -> new Cerdo(vida);
            case "aldeano" -> new Aldeano(vida);
            default -> throw new IllegalArgumentException("Tipo de entidad desconocido: " + tipo);
        };
    }

    public static Entidad construir(String tipo, int vida, int danoBase, int velocidad, int valorExtra) {
        validarTipo(tipo);
        return switch (tipo.toLowerCase()) {
            case "creeper" -> new Creeper(vida, danoBase, velocidad, valorExtra);
            case "jugador" -> new Jugador(vida, danoBase, velocidad, valorExtra);
            case "zombie" -> new Zombie(vida, danoBase, velocidad);
            case "zombiepequeno" -> new ZombiePequeno(vida, danoBase, velocidad, valorExtra);
            case "esqueleto" -> new Esqueleto(vida, danoBase, velocidad, valorExtra);
            case "cerdo" -> new Cerdo(vida, danoBase, velocidad, valorExtra > 0);
            case "aldeano" -> new Aldeano(vida, danoBase, velocidad, valorExtra > 0);
            default -> throw new IllegalArgumentException("Tipo de entidad desconocido: " + tipo);
        };
    }

    private static void validarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Falta el tipo de entidad");
        }
    }
}

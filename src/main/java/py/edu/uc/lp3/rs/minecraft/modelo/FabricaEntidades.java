package py.edu.uc.lp3.rs.minecraft.modelo;

public final class FabricaEntidades {

    private FabricaEntidades() {
    }

    public static Entidad construir(String tipo, int vida, int danoBase, int velocidad, int valorExtra) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Falta el tipo de entidad");
        }
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
}

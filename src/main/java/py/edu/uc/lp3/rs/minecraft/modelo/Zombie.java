package py.edu.uc.lp3.rs.minecraft.modelo;

public class Zombie extends Mob {

    public Zombie(int vida, int danoBase, int velocidad) {
        super(vida, danoBase, velocidad, true);
    }

    @Override
    public String emitirSonido() {
        return "Un zombie gruñe: ¡Grrr... brrraaains!";
    }

    public void atacar(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("No se puede atacar a un jugador nulo");
        }
        jugador.recibirDano(getDanoBase());
    }
}
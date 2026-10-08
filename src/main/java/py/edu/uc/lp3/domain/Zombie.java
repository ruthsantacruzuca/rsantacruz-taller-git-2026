package py.edu.uc.lp3.domain;

public class Zombie extends Mob {

    public Zombie(int vida) {
        super(vida, true);
    }

    public Zombie(int vida, int danoBase, int velocidad) {
        super(vida, danoBase, velocidad, true);
    }

    @Override
    public String emitirSonido() {
        return "Un zombie gruñe: ¡Grrr... brrraaains!";
    }

    @Override
    public String atacar() {
        return "muerde con las dos manos y hace " + getDanoBase() + " de daño";
    }

    public String atacar(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("No se puede atacar a un jugador nulo");
        }
        jugador.recibirDano(getDanoBase());
        return "muerde al jugador y le queda " + jugador.getVida() + " de vida";
    }
}
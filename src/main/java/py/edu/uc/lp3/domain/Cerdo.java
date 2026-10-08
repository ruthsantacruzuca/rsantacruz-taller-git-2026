package py.edu.uc.lp3.domain;

public class Cerdo extends Mob {

    private boolean montable;

    public Cerdo(int vida) {
        this(vida, DANO_BASE_POR_DEFECTO, VELOCIDAD_POR_DEFECTO, false);
    }

    public Cerdo(int vida, int danoBase, int velocidad, boolean montable) {
        super(vida, danoBase, velocidad, false);
        this.montable = montable;
    }

    @Override
    public String emitirSonido() {
        return "Un cerdo gruñe: ¡Oink, oink!";
    }

    public boolean isMontable() {
        return montable;
    }

    public void setMontable(boolean montable) {
        this.montable = montable;
    }
}
package py.edu.uc.lp3.rs.minecraft.modelo;

public class Cerdo extends Mob {

    private boolean montable;

    public Cerdo(int vida, int danoBase, int velocidad, boolean montable) {
        super(vida, danoBase, velocidad, false);
        this.montable = montable;
    }

    public boolean isMontable() {
        return montable;
    }

    public void setMontable(boolean montable) {
        this.montable = montable;
    }
}
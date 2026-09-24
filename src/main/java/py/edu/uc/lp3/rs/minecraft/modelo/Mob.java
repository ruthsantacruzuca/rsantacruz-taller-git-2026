package py.edu.uc.lp3.rs.minecraft.modelo;

public class Mob extends Entidad {

    private boolean hostil;

    public Mob(int vida, int danoBase, int velocidad, boolean hostil) {
        super(vida, danoBase, velocidad);
        this.hostil = hostil;
    }

    public boolean isHostil() {
        return hostil;
    }

    public void setHostil(boolean hostil) {
        this.hostil = hostil;
    }
}
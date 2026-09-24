package py.edu.uc.lp3.rs.minecraft.modelo;

public class Aldeano extends Mob {

    private boolean comercializacion;

    public Aldeano(int vida, int danoBase, int velocidad, boolean comercializacion) {
        super(vida, danoBase, velocidad, false);
        this.comercializacion = comercializacion;
    }

    @Override
    public String emitirSonido() {
        return "Un aldeano murmura al hacer trueque: ¡Hrmm, hrmm!";
    }

    public boolean puedeComercializar() {
        return comercializacion;
    }

    public void setComercializacion(boolean comercializacion) {
        this.comercializacion = comercializacion;
    }
}
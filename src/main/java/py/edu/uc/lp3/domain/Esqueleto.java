package py.edu.uc.lp3.domain;

public class Esqueleto extends Mob {

    private static final int PRECISION_ARCO_POR_DEFECTO = 50;
    private int precisionArco;

    public Esqueleto(int vida) {
        this(vida, DANO_BASE_POR_DEFECTO, VELOCIDAD_POR_DEFECTO, PRECISION_ARCO_POR_DEFECTO);
    }

    public Esqueleto(int vida, int danoBase, int velocidad, int precisionArco) {
        super(vida, danoBase, velocidad, true);
        this.precisionArco = precisionArco;
    }

    @Override
    public String emitirSonido() {
        return "Un esqueleto hace sonar sus huesos antes de disparar: clac clac";
    }

    public int getPrecisionArco() {
        return precisionArco;
    }

    public void setPrecisionArco(int precisionArco) {
        this.precisionArco = precisionArco;
    }
}

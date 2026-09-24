package py.edu.uc.lp3.rs.minecraft.modelo;

public class ZombiePequeno extends Zombie {

    private int velocidadAumentada;

    public ZombiePequeno(int vida, int danoBase, int velocidad, int velocidadAumentada) {
        super(vida, danoBase, velocidad);
        setVelocidadAumentada(velocidadAumentada);
    }

    @Override
    public String emitirSonido() {
        return "Un zombie pequeño chilla: ¡Grrr-íiii!";
    }

    public int getVelocidadAumentada() {
        return velocidadAumentada;
    }

    public void setVelocidadAumentada(int velocidadAumentada) {
        if (velocidadAumentada < 0) {
            throw new IllegalArgumentException("La velocidad aumentada no puede ser negativa: " + velocidadAumentada);
        }
        this.velocidadAumentada = velocidadAumentada;
    }

    public int getVelocidadTotal() {
        return getVelocidad() + velocidadAumentada;
    }
}
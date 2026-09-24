package py.edu.uc.lp3.rs.minecraft.modelo;

public class Jugador extends Entidad {

    private static final int VIDA_MAXIMA = 20;
    private int armadura;

    public Jugador(int vida, int danoBase, int velocidad, int armadura) {
        super(vida, danoBase, velocidad);
        if (vida > VIDA_MAXIMA) {
            throw new IllegalArgumentException("La vida de un jugador no puede superar " + VIDA_MAXIMA + ": " + vida);
        }
        setArmadura(armadura);
    }

    public int getArmadura() {
        return armadura;
    }

    public void setArmadura(int armadura) {
        if (armadura < 0) {
            throw new IllegalArgumentException("La armadura no puede ser negativa: " + armadura);
        }
        this.armadura = armadura;
    }

    @Override
    public String emitirSonido() {
        return "Un jugador respira agitado y saluda: ¡Hola!";
    }

    @Override
    public void setVida(int vida) {
        if (vida > VIDA_MAXIMA) {
            throw new IllegalArgumentException("La vida de un jugador no puede superar " + VIDA_MAXIMA + ": " + vida);
        }
        super.setVida(vida);
    }

    public void recibirDano(int dano) {
        if (dano < 0) {
            throw new IllegalArgumentException("El daño recibido no puede ser negativo: " + dano);
        }
        int danoEfectivo = Math.max(0, dano - armadura);
        setVida(getVida() - danoEfectivo);
    }

    public void curar(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La curación no puede ser negativa: " + cantidad);
        }
        setVida(Math.min(VIDA_MAXIMA, getVida() + cantidad));
    }
}
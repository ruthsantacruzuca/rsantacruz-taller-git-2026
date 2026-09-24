package py.edu.uc.lp3.rs.minecraft.modelo;

public abstract class Entidad {

    private int vida;
    private int danoBase;
    private int velocidad;

    public abstract String emitirSonido();

    public Entidad(int vida, int danoBase, int velocidad) {
        this.vida = validarVida(vida);
        this.danoBase = validarDanoBase(danoBase);
        this.velocidad = validarVelocidad(velocidad);
    }

    public int getVida() {
        return vida;
    }

    public int getDanoBase() {
        return danoBase;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVida(int vida) {
        this.vida = validarVida(vida);
    }

    public void setDanoBase(int danoBase) {
        this.danoBase = validarDanoBase(danoBase);
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = validarVelocidad(velocidad);
    }

    private static int validarVida(int vida) {
        if (vida < 0) {
            throw new IllegalArgumentException("La vida no puede ser negativa: " + vida);
        }
        return vida;
    }

    private static int validarDanoBase(int danoBase) {
        if (danoBase < 0) {
            throw new IllegalArgumentException("El daño base no puede ser negativo: " + danoBase);
        }
        return danoBase;
    }

    private static int validarVelocidad(int velocidad) {
        if (velocidad < 0) {
            throw new IllegalArgumentException("La velocidad no puede ser negativa: " + velocidad);
        }
        return velocidad;
    }
}
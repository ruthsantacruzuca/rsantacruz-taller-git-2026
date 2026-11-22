package py.edu.uc.lp3.domain;

import jakarta.persistence.MappedSuperclass;

/**
 * Estado común de las entidades. Las validaciones viven en la capa service;
 * esta clase sólo guarda y expone los valores.
 */
@MappedSuperclass
public abstract class Entidad {

    public static final int DANO_BASE_POR_DEFECTO = 1;
    public static final int VELOCIDAD_POR_DEFECTO = 1;

    private int vida;
    private int danoBase;
    private int velocidad;

    public abstract String emitirSonido();

    /** JPA instancia por reflexión; queda protegido para no abrir el ocultamiento. */
    protected Entidad() {
    }

    public Entidad(int vida) {
        this(vida, DANO_BASE_POR_DEFECTO, VELOCIDAD_POR_DEFECTO);
    }

    public Entidad(int vida, int danoBase, int velocidad) {
        this.vida = vida;
        this.danoBase = danoBase;
        this.velocidad = velocidad;
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
        this.vida = vida;
    }

    public void setDanoBase(int danoBase) {
        this.danoBase = danoBase;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public String atacar() {
        return "ataca con " + danoBase + " de daño";
    }

    public String atacar(int distancia) {
        int distanciaEfectiva = Math.max(0, distancia);
        int danio = Math.max(0, danoBase - distanciaEfectiva / 10);
        return "ataca desde " + distancia + " bloques con " + danio + " de daño";
    }
}

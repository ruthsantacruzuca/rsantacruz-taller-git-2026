package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Familia distinta a Mob: también implementa Danable, así el endpoint del
 * protocolo puede hablarle a Jugador y a Mob con el mismo tipo de interfaz.
 */
@Entity
@Table(name = "jugadores")
public class Jugador extends Entidad implements Danable {

    private static final int VIDA_MAXIMA = 20;
    public static final int ARMADURA_POR_DEFECTO = 0;

    private Long id;
    private int armadura;

    protected Jugador() {
    }

    public Jugador(int vida) {
        this(vida, DANO_BASE_POR_DEFECTO, VELOCIDAD_POR_DEFECTO, ARMADURA_POR_DEFECTO);
    }

    public Jugador(int vida, int danoBase, int velocidad, int armadura) {
        super(vida, danoBase, velocidad);
        if (vida > VIDA_MAXIMA) {
            throw new IllegalArgumentException("La vida de un jugador no puede superar " + VIDA_MAXIMA + ": " + vida);
        }
        setArmadura(armadura);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
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

    @Override
    public String estado() {
        return "Jugador con " + getVida() + " de vida y " + armadura + " de armadura";
    }
}

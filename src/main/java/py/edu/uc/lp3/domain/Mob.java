package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Clase base de la familia persistida. JPA guarda toda la familia en una sola
 * tabla ("mobs") y distingue el subtipo con la columna "tipo" (SINGLE_TABLE).
 * Jackson usa ese mismo nombre como discriminador del JSON.
 */
@Entity
@Table(name = "mobs")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("MOB")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Creeper.class, name = "CREEPER"),
        @JsonSubTypes.Type(value = Zombie.class, name = "ZOMBIE")
})
public class Mob extends Entidad implements Danable {

    private Long id;
    private boolean hostil;

    /** JPA exige un constructor sin argumentos; queda protegido para no abrir el ocultamiento. */
    protected Mob() {
    }

    public Mob(int vida) {
        this(vida, false);
    }

    public Mob(int vida, boolean hostil) {
        super(vida);
        this.hostil = hostil;
    }

    public Mob(int vida, int danoBase, int velocidad, boolean hostil) {
        super(vida, danoBase, velocidad);
        this.hostil = hostil;
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

    @Override
    public String emitirSonido() {
        return "Un mob emite un ruido sordo desde la oscuridad";
    }

    public boolean isHostil() {
        return hostil;
    }

    public void setHostil(boolean hostil) {
        this.hostil = hostil;
    }

    @Override
    public void recibirDano(int dano) {
        if (dano < 0) {
            throw new IllegalArgumentException("El daño recibido no puede ser negativo: " + dano);
        }
        setVida(Math.max(0, getVida() - dano));
    }

    @Override
    public String estado() {
        return getClass().getSimpleName() + " con " + getVida() + " de vida";
    }
}

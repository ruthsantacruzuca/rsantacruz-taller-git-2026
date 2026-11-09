package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends Mob {

    private static final int CARGA_EXPLOSION_POR_DEFECTO = 12;
    private int cargaExplosion;

    protected Creeper() {
    }

    public Creeper(int vida) {
        this(vida, DANO_BASE_POR_DEFECTO, VELOCIDAD_POR_DEFECTO, CARGA_EXPLOSION_POR_DEFECTO);
    }

    @JsonCreator
    public Creeper(@JsonProperty("vida") int vida,
                   @JsonProperty("danoBase") int danoBase,
                   @JsonProperty("velocidad") int velocidad,
                   @JsonProperty("cargaExplosion") int cargaExplosion) {
        super(vida, danoBase, velocidad, true);
        setCargaExplosion(cargaExplosion);
    }

    @Override
    public String emitirSonido() {
        return "Un creeper silba mientras se carga: ¡SSSSS... BOOM!";
    }

    public int getCargaExplosion() {
        return cargaExplosion;
    }

    public void setCargaExplosion(int cargaExplosion) {
        if (cargaExplosion < 0) {
            throw new IllegalArgumentException("La carga de explosión no puede ser negativa: " + cargaExplosion);
        }
        this.cargaExplosion = cargaExplosion;
    }

    public void aumentarCarga(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La carga solo aumenta con valores positivos: " + cantidad);
        }
        setCargaExplosion(getCargaExplosion() + cantidad);
    }

    /** Detona: deja su carga y su vida en cero y devuelve el daño que repartió. */
    public int explotar() {
        int carga = getCargaExplosion();
        setCargaExplosion(0);
        setVida(0);
        return carga;
    }
}

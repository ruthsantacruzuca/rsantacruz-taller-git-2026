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
    private boolean explotado;

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
        this.cargaExplosion = cargaExplosion;
    }

    @Override
    public String emitirSonido() {
        return "Un creeper silba mientras se carga: ¡SSSSS... BOOM!";
    }

    public int getCargaExplosion() {
        return cargaExplosion;
    }

    public void setCargaExplosion(int cargaExplosion) {
        this.cargaExplosion = cargaExplosion;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isExplotado() {
        return explotado;
    }

    public void setExplotado(boolean explotado) {
        this.explotado = explotado;
    }
}

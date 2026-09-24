package py.edu.uc.lp3.rs.minecraft.modelo;

public class Creeper extends Mob {

    private int cargaExplosion;

    public Creeper(int vida, int danoBase, int velocidad, int cargaExplosion) {
        super(vida, danoBase, velocidad, true);
        setCargaExplosion(cargaExplosion);
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

    public void explotar() {
        setCargaExplosion(0);
        setVida(0);
    }
}
package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.repository.MobRepository;
import py.edu.uc.lp3.service.exception.CreeperConsumidoException;
import py.edu.uc.lp3.service.exception.RecursoNoEncontradoException;
import py.edu.uc.lp3.service.exception.ReglaDeJuegoException;

/**
 * CRUD de la familia Mob. Acá viven las validaciones y la regla que impide
 * modificar un Creeper que ya explotó (el recurso quedó consumido).
 */
@Service
public class MobService {

    private final MobRepository repositorio;

    public MobService(MobRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public Mob crear(Mob mob) {
        ValidadorMinecraft.validarMob(mob);
        return repositorio.save(mob);
    }

    @Transactional(readOnly = true)
    public List<Mob> listar(Boolean hostil) {
        return hostil == null ? repositorio.findAll() : repositorio.findByHostil(hostil);
    }

    @Transactional(readOnly = true)
    public Mob obtener(Long id) {
        return buscar(id);
    }

    @Transactional
    public Mob actualizar(Long id, Mob cambios) {
        Mob actual = buscar(id);
        if (actual instanceof Creeper creeper && creeper.isExplotado()) {
            throw new CreeperConsumidoException(
                    "El creeper " + id + " ya explotó y no admite modificaciones");
        }
        if (actual.getClass() != cambios.getClass()) {
            throw new ReglaDeJuegoException("No se puede cambiar el tipo del mob " + id
                    + " (" + actual.getClass().getSimpleName() + " -> " + cambios.getClass().getSimpleName() + ")");
        }
        ValidadorMinecraft.validarMob(cambios);

        actual.setVida(cambios.getVida());
        actual.setDanoBase(cambios.getDanoBase());
        actual.setVelocidad(cambios.getVelocidad());
        actual.setHostil(cambios.isHostil());
        if (actual instanceof Creeper actualCreeper && cambios instanceof Creeper cambiosCreeper) {
            actualCreeper.setCargaExplosion(cambiosCreeper.getCargaExplosion());
        }
        return repositorio.save(actual);
    }

    @Transactional
    public void borrar(Long id) {
        Mob actual = buscar(id);
        repositorio.delete(actual);
    }

    private Mob buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el mob con id " + id));
    }
}

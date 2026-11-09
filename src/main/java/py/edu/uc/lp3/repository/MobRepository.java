package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Mob;

/**
 * Repositorio de la clase base de la familia persistida. Los mensajes de
 * consulta se declaran acá; Spring Data genera la implementación.
 */
public interface MobRepository extends JpaRepository<Mob, Long> {

    List<Mob> findByHostil(boolean hostil);

    List<Mob> findByVidaGreaterThan(int vida);
}

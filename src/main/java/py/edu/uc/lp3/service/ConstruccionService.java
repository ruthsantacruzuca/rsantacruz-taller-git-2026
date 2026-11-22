package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Entidad;

/**
 * Construcción por URL de POO-06. Valida los parámetros en service y delega en
 * la fábrica la elección de la firma simple o sobrecargada.
 */
@Service
public class ConstruccionService {

    public Construccion construir(String tipo, int vida, Integer danoBase, Integer velocidad,
            int valorExtra, int distancia) {
        ValidadorMinecraft.validarDistancia(distancia);

        boolean ninguno = danoBase == null && velocidad == null;
        boolean completo = danoBase != null && velocidad != null;
        Entidad entidad;
        String constructor;
        if (ninguno) {
            entidad = FabricaEntidades.construir(tipo, vida);
            constructor = "simple (construir(tipo, vida))";
        } else if (completo) {
            entidad = FabricaEntidades.construir(tipo, vida, danoBase, velocidad, valorExtra);
            constructor = "sobrecargado (construir(tipo, vida, danoBase, velocidad, valorExtra))";
        } else {
            throw new IllegalArgumentException(
                    "Pasá danoBase y velocidad juntos (firma completa) o ninguno de los dos (firma simple)");
        }

        ValidadorMinecraft.validarEntidadCompleta(entidad);
        return new Construccion(entidad, constructor);
    }

    public record Construccion(Entidad entidad, String constructor) {
    }
}

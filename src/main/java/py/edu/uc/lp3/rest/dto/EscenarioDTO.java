package py.edu.uc.lp3.rest.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public final class EscenarioDTO {

    private EscenarioDTO() {
    }

    public record ExplosionA2(
            @Schema(description = "ID del Creeper persistido en la API de Matias (A2)", example = "1") Long creeperId,
            @Schema(description = "Indica si el rayo cargo el Creeper antes de detonar", example = "true") Boolean cargado,
            @Schema(description = "Objetivos y su distancia al Creeper, segun la regla de A2") List<VictimaA2> victimas) {
    }

    public record VictimaA2(
            @Schema(description = "ID del hostil persistido en A2", example = "2") Long hostilId,
            @Schema(description = "Distancia usada por la regla de dano de A2", example = "1.5") Double distancia) {
    }

    public record ExplosionA3(
            @Schema(description = "ID del Creeper persistido en la API de Sebastian (A3)", example = "1") Long creeperId,
            @Schema(description = "ID del Jugador persistido en A3", example = "2") Long jugadorId,
            @Schema(description = "ID del Zombie persistido en A3", example = "3") Long zombieId,
            @Schema(description = "Radio de la explosion segun la regla de A3", example = "5") Integer radio) {
    }

    public record EscenarioRequest(
            @Schema(description = "Nombre de la jugada que se guardara en el H2 de A1", example = "Triple detonacion verificada") String descripcion,
            @Schema(description = "Datos que A1 envia a A2 para validar el ciclo de vida y la distancia") ExplosionA2 a2,
            @Schema(description = "Datos que A1 envia a A3 para validar carga y radio") ExplosionA3 a3) {
    }

    public record EscenarioResponse(
            boolean guardado,
            Long jugadaId,
            String companeroA2,
            String companeroA3,
            String respuestaA2,
            String respuestaA3) {
    }

    public record JugadaGuardada(
            Long id,
            String descripcion,
            String companeroA2,
            String companeroA3,
            String respuestaA2,
            String respuestaA3,
            java.time.LocalDateTime registradoEn) {
    }
}

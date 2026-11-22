package py.edu.uc.lp3.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "escenarios_verificados")
public class EscenarioVerificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @Column(nullable = false)
    private String companeroA2;

    @Column(nullable = false)
    private String companeroA3;

    @Column(length = 4000)
    private String respuestaA2;

    @Column(length = 4000)
    private String respuestaA3;

    private LocalDateTime registradoEn;

    protected EscenarioVerificado() {
    }

    public EscenarioVerificado(String descripcion, String companeroA2, String companeroA3,
            String respuestaA2, String respuestaA3) {
        this.descripcion = descripcion;
        this.companeroA2 = companeroA2;
        this.companeroA3 = companeroA3;
        this.respuestaA2 = respuestaA2;
        this.respuestaA3 = respuestaA3;
        this.registradoEn = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCompaneroA2() {
        return companeroA2;
    }

    public String getCompaneroA3() {
        return companeroA3;
    }

    public String getRespuestaA2() {
        return respuestaA2;
    }

    public String getRespuestaA3() {
        return respuestaA3;
    }

    public LocalDateTime getRegistradoEn() {
        return registradoEn;
    }
}

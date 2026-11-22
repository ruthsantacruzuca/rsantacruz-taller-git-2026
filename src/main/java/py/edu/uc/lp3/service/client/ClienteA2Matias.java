package py.edu.uc.lp3.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.ExplosionA2;

@Service
public class ClienteA2Matias implements ClienteCompanero {

    private final RestClient cliente;

    public ClienteA2Matias(RestClient.Builder constructor,
            @Value("${companeros.a2.base-url}") String baseUrl) {
        this.cliente = constructor.baseUrl(baseUrl).build();
    }

    @Override
    public String rol() {
        return "A2";
    }

    @Override
    public Validacion validar(EscenarioRequest escenario) {
        ExplosionA2 explosion = escenario.a2();
        if (explosion == null) {
            return new Validacion(false, 400, "Faltan los datos de la explosion de A2");
        }
        try {
            ResponseEntity<String> respuesta = cliente.post()
                    .uri("/api/simulaciones/explosion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(explosion)
                    .retrieve()
                    .toEntity(String.class);
            return new Validacion(respuesta.getStatusCode().is2xxSuccessful(),
                    respuesta.getStatusCode().value(), respuesta.getBody());
        } catch (RestClientResponseException ex) {
            return new Validacion(false, ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            return new Validacion(false, 502, "No se pudo consultar la API de A2: " + ex.getMessage());
        }
    }
}

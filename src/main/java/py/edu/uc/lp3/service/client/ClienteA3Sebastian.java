package py.edu.uc.lp3.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import py.edu.uc.lp3.rest.dto.EscenarioDTO.EscenarioRequest;
import py.edu.uc.lp3.rest.dto.EscenarioDTO.ExplosionA3;

@Service
public class ClienteA3Sebastian implements ClienteCompanero {

    private final RestClient cliente;

    public ClienteA3Sebastian(RestClient.Builder constructor,
            @Value("${companeros.a3.base-url}") String baseUrl) {
        this.cliente = constructor.baseUrl(baseUrl).build();
    }

    @Override
    public String rol() {
        return "A3";
    }

    @Override
    public Validacion validar(EscenarioRequest escenario) {
        ExplosionA3 explosion = escenario.a3();
        if (explosion == null) {
            return new Validacion(false, 400, "Faltan los datos de la explosion de A3");
        }
        try {
            ResponseEntity<String> respuesta = cliente.post()
                    .uri("/minecraft/sgomezdelafuente/simulaciones/explosion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(explosion)
                    .retrieve()
                    .toEntity(String.class);
            return new Validacion(respuesta.getStatusCode().is2xxSuccessful(),
                    respuesta.getStatusCode().value(), respuesta.getBody());
        } catch (RestClientResponseException ex) {
            return new Validacion(false, ex.getStatusCode().value(), ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            return new Validacion(false, 502, "No se pudo consultar la API de A3: " + ex.getMessage());
        }
    }
}

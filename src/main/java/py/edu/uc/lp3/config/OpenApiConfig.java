package py.edu.uc.lp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiDeMinecraft() {
        return new OpenAPI().info(new Info()
                .title("API Minecraft - Ruth Santacruz")
                .version("1.0.0")
                .description("""
                        CRUD de mobs y jugadores, simulacion de explosiones con las dos reglas de POO-08
                        y escenario verificado de POO-09. El escenario consulta a A2 (Matias Duarte) y
                        A3 (Sebastian Gomez de la Fuente) y solo persiste la jugada si ambos aceptan.
                        """));
    }
}

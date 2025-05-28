package accel_tech.net.objectstorage_api;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@EnableMongoAuditing
public class ObjectstorageApiApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();

		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
		System.setProperty("PORT", dotenv.get("PORT"));

		SpringApplication.run(ObjectstorageApiApplication.class, args);

		System.out.println("Server started...");
	}

	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
		factory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());
		return new RestTemplate(factory);
	}


}

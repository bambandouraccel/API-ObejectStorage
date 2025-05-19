package accel_tech.net.objectstorage_api;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

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

}

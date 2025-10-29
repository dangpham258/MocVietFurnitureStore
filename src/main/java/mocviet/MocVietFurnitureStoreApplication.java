package mocviet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MocVietFurnitureStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(MocVietFurnitureStoreApplication.class, args);
	}

}

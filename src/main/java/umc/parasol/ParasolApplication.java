package umc.parasol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import umc.parasol.global.config.YamlPropertySourceFactory;

@SpringBootApplication
@PropertySource(value = {"classpath:database/application-database.yml"}, factory = YamlPropertySourceFactory.class)
@PropertySource(value = {"classpath:auth/application-auth.yml"}, factory = YamlPropertySourceFactory.class)
public class ParasolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParasolApplication.class, args);
	}

}

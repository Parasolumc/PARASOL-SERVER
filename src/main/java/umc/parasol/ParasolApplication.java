package umc.parasol;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import umc.parasol.global.config.YamlPropertySourceFactory;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
//@EnableScheduling
/*@PropertySource(value = {"classpath:database/application-database.yml"}, factory = YamlPropertySourceFactory.class)
@PropertySource(value = {"classpath:auth/application-auth.yml"}, factory = YamlPropertySourceFactory.class)*/
public class ParasolApplication {

	@PostConstruct
	public void setTimeToKTC() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ParasolApplication.class, args);
	}

}

package br.comar.ricardo.stuff.adsecuredrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@ImportResource("spring-context.xml")
@RestController
public class SpringRestSecureAdApplication {
	
	@RequestMapping(value = "/ws/hello/{user}", method = RequestMethod.GET)
	public Health getUser(@PathVariable String user) {
		return Health.status("Hello " + user + "!!!").build();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringRestSecureAdApplication.class, args);
	}
}

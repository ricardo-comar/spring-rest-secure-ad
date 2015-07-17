package br.comar.ricardo.stuff.adsecuredrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration
@ImportResource("spring-context.xml")
public class SpringRestSecureAdApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringRestSecureAdApplication.class, args);
	}
}

package br.comar.ricardo.stuff.adsecuredrest.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured("isAuthenticated()")
public class HelloController {

	@RequestMapping(value = "/ws/hello/{user}", method = RequestMethod.GET)
	public Health getUser(@PathVariable String user) {
		return Health.status("Hello " + user + "!!!").build();
	}

}

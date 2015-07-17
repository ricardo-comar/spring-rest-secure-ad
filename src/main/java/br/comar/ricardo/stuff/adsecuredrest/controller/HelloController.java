package br.comar.ricardo.stuff.adsecuredrest.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("isAuthenticated()")
public class HelloController {

	@RequestMapping(value = "/ws/hello/{user}", method = RequestMethod.GET)
	public Health getUser(@PathVariable String user) {
		return Health.status("Hello " + user + "!!!").build();
	}

}

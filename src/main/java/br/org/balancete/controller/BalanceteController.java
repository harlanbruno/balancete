package br.org.balancete.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BalanceteController {

	@GetMapping("/something")
	public ResponseEntity<String> something() {
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

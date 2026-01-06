package com.recintos.municipalidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication
public class RecintosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecintosBackendApplication.class, args);
	}

}
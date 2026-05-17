package com.recintos.municipalidad.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CsrfController {

    @GetMapping("/csrf-token")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", token);
        return ResponseEntity.ok(Map.of("token", token));
    }
}

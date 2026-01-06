package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.model.Notificacion;
import com.recintos.municipalidad.service.ServicioNotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private ServicioNotificacion servicioNotificacion;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(@PathVariable Long idUsuario) {
        List<Notificacion> notificaciones = servicioNotificacion.obtenerNotificacionesPorUsuario(idUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/{idNotificacion}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long idNotificacion) {
        servicioNotificacion.marcarComoLeida(idNotificacion);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private com.recintos.municipalidad.service.ServicioUsuario servicioUsuario;

    @PostMapping("/token")
    public ResponseEntity<Void> guardarToken(@RequestBody java.util.Map<String, Object> payload) {
        Long idUsuario = Long.valueOf(payload.get("idUsuario").toString());
        String token = (String) payload.get("token");
        servicioUsuario.guardarTokenFCM(idUsuario, token);
        return ResponseEntity.ok().build();
    }
}

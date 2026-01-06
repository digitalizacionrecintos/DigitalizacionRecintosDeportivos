package com.recintos.municipalidad.service;

public interface ServicioFirebase {
    void enviarNotificacionPush(String token, String titulo, String cuerpo);
}

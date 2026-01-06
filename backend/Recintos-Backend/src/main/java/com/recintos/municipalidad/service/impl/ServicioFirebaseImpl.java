package com.recintos.municipalidad.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.recintos.municipalidad.service.ServicioFirebase;
import org.springframework.stereotype.Service;

@Service
public class ServicioFirebaseImpl implements ServicioFirebase {

    @Override
    public void enviarNotificacionPush(String token, String titulo, String cuerpo) {
        if (token == null || token.isEmpty()) {
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.err.println("Error sending FCM message: " + e.getMessage());
        }
    }
}

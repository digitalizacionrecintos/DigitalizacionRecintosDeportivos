package com.recintos.municipalidad.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {

            String serviceAccountPath = "src/main/resources/firebase-service-account.json";

            java.io.File file = new java.io.File(serviceAccountPath);
            if (!file.exists()) {
                System.out.println("WARNING: Firebase service account file not found at " + serviceAccountPath
                        + ". Push notifications will not work.");
                return;
            }

            FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase application has been initialized");
            }
        } catch (IOException e) {
            System.err.println("ERROR: Could not initialize Firebase: " + e.getMessage());
        }
    }
}

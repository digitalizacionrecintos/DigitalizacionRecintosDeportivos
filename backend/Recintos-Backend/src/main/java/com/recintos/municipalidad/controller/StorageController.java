package com.recintos.municipalidad.controller;

import com.recintos.municipalidad.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        System.out.println("Recibiendo archivo: " + file.getOriginalFilename());
        System.out.println("Tamaño: " + file.getSize());
        String filename = storageService.store(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();
        System.out.println("Archivo guardado en: " + fileDownloadUri);
        return ResponseEntity.ok(fileDownloadUri);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleStorageFileNotFound(RuntimeException exc) {
        return ResponseEntity.notFound().build();
    }
}

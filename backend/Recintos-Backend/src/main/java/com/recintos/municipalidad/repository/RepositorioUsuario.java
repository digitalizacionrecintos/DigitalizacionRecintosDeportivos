package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    java.util.List<Usuario> findByRol(String rol);
}

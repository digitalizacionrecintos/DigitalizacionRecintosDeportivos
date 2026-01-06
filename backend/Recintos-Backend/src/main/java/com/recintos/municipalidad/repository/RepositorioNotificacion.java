package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Notificacion;
import com.recintos.municipalidad.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioNotificacion extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);
}

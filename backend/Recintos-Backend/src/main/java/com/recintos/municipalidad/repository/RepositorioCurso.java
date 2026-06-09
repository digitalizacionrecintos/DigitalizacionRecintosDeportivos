package com.recintos.municipalidad.repository;

import com.recintos.municipalidad.model.Curso;
import com.recintos.municipalidad.model.enums.EstadoCurso;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioCurso extends JpaRepository<Curso, Long> {

    List<Curso> findByEstado(EstadoCurso estadoCurso);


    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Curso c WHERE c.idCurso = :idCurso")
    Optional<Curso> findByIdParaInscribir(@Param("idCurso") Long idCurso);
}

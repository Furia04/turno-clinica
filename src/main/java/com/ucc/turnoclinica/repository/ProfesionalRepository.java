package com.ucc.turnoclinica.repository;

import com.ucc.turnoclinica.entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    Optional<Profesional> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);
    List<Profesional> findByEspecialidadContainingIgnoreCase(String especialidad);
}

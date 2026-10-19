package com.ucc.turnoclinica.repository;

import com.ucc.turnoclinica.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByPaciente_Id(Long idPaciente);
    List<Turno> findByProfesional_Id(Long idProfesional);
    boolean existsByPaciente_Id(Long idPaciente);
    boolean existsByProfesional_Id(Long idProfesional);
    boolean existsByProfesional_IdAndFechaHora(Long idProfesional, LocalDateTime fechaHora);
    boolean existsByPaciente_IdAndFechaHora(Long idPaciente, LocalDateTime fechaHora);
}

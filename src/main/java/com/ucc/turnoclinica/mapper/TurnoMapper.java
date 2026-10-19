package com.ucc.turnoclinica.mapper;

import com.ucc.turnoclinica.dto.TurnoRequestDTO;
import com.ucc.turnoclinica.dto.TurnoResponseDTO;
import com.ucc.turnoclinica.entity.EstadoTurno;
import com.ucc.turnoclinica.entity.Paciente;
import com.ucc.turnoclinica.entity.Profesional;
import com.ucc.turnoclinica.entity.Turno;
import org.springframework.stereotype.Component;

@Component
public class TurnoMapper {

    private final PacienteMapper pacienteMapper;
    private final ProfesionalMapper profesionalMapper;

    public TurnoMapper(PacienteMapper pacienteMapper, ProfesionalMapper profesionalMapper) {
        this.pacienteMapper = pacienteMapper;
        this.profesionalMapper = profesionalMapper;
    }

    public TurnoResponseDTO entityToDTO(Turno turno) {
        if (turno == null) {
            return null;
        }
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getFechaHora(),
                turno.getMotivo(),
                turno.getEstado(),
                pacienteMapper.entityToDTO(turno.getPaciente()),
                profesionalMapper.entityToDTO(turno.getProfesional())
        );
    }

    public Turno dtoToEntity(TurnoRequestDTO dto, Paciente paciente, Profesional profesional) {
        if (dto == null) {
            return null;
        }
        Turno turno = new Turno();
        turno.setFechaHora(dto.fechaHora());
        turno.setMotivo(dto.motivo() != null ? dto.motivo().trim() : null);
        turno.setEstado(dto.estado() != null ? dto.estado() : EstadoTurno.PENDIENTE);
        turno.setPaciente(paciente);
        turno.setProfesional(profesional);
        return turno;
    }
}

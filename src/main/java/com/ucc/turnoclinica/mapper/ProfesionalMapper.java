package com.ucc.turnoclinica.mapper;

import com.ucc.turnoclinica.dto.ProfesionalRequestDTO;
import com.ucc.turnoclinica.dto.ProfesionalResponseDTO;
import com.ucc.turnoclinica.entity.Profesional;
import org.springframework.stereotype.Component;

@Component
public class ProfesionalMapper {

    public ProfesionalResponseDTO entityToDTO(Profesional profesional) {
        if (profesional == null) {
            return null;
        }
        return new ProfesionalResponseDTO(
                profesional.getId(),
                profesional.getNombre(),
                profesional.getApellido(),
                profesional.getMatricula(),
                profesional.getEspecialidad()
        );
    }

    public Profesional dtoToEntity(ProfesionalRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Profesional profesional = new Profesional();
        profesional.setNombre(dto.nombre().trim());
        profesional.setApellido(dto.apellido().trim());
        profesional.setMatricula(dto.matricula().trim());
        profesional.setEspecialidad(dto.especialidad().trim());
        return profesional;
    }
}

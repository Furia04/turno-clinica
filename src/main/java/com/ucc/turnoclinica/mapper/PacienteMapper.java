package com.ucc.turnoclinica.mapper;

import com.ucc.turnoclinica.dto.PacienteRequestDTO;
import com.ucc.turnoclinica.dto.PacienteResponseDTO;
import com.ucc.turnoclinica.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    public PacienteResponseDTO entityToDTO(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getDni(),
                paciente.getTelefono(),
                paciente.getEmail(),
                paciente.getObraSocial()
        );
    }

    public Paciente dtoToEntity(PacienteRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Paciente paciente = new Paciente();
        paciente.setNombre(dto.nombre().trim());
        paciente.setApellido(dto.apellido().trim());
        paciente.setDni(dto.dni().trim());
        paciente.setTelefono(dto.telefono() != null ? dto.telefono().trim() : null);
        paciente.setEmail(dto.email().trim().toLowerCase());
        paciente.setObraSocial(dto.obraSocial() != null ? dto.obraSocial().trim() : null);
        return paciente;
    }
}

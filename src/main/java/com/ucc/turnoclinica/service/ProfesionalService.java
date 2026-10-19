package com.ucc.turnoclinica.service;

import com.ucc.turnoclinica.dto.ProfesionalRequestDTO;
import com.ucc.turnoclinica.dto.ProfesionalResponseDTO;
import com.ucc.turnoclinica.entity.Profesional;
import com.ucc.turnoclinica.exception.ProfesionalNotFoundException;
import com.ucc.turnoclinica.exception.ReglaDeNegocioException;
import com.ucc.turnoclinica.mapper.ProfesionalMapper;
import com.ucc.turnoclinica.repository.ProfesionalRepository;
import com.ucc.turnoclinica.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;
    private final TurnoRepository turnoRepository;
    private final ProfesionalMapper profesionalMapper;

    public ProfesionalService(ProfesionalRepository profesionalRepository,
                              TurnoRepository turnoRepository,
                              ProfesionalMapper profesionalMapper) {
        this.profesionalRepository = profesionalRepository;
        this.turnoRepository = turnoRepository;
        this.profesionalMapper = profesionalMapper;
    }

    public List<ProfesionalResponseDTO> obtenerTodos() {
        return profesionalRepository.findAll().stream()
                .map(profesionalMapper::entityToDTO)
                .toList();
    }

    public ProfesionalResponseDTO obtenerPorId(Long id) {
        Profesional profesional = buscarEntidadPorId(id);
        return profesionalMapper.entityToDTO(profesional);
    }

    public List<ProfesionalResponseDTO> buscarPorEspecialidad(String especialidad) {
        return profesionalRepository.findByEspecialidadContainingIgnoreCase(especialidad).stream()
                .map(profesionalMapper::entityToDTO)
                .toList();
    }

    public ProfesionalResponseDTO crear(ProfesionalRequestDTO dto) {
        if (profesionalRepository.existsByMatricula(dto.matricula().trim())) {
            throw new ReglaDeNegocioException("Ya existe un profesional registrado con esa matrícula.");
        }
        Profesional profesional = profesionalMapper.dtoToEntity(dto);
        Profesional guardado = profesionalRepository.save(profesional);
        return profesionalMapper.entityToDTO(guardado);
    }

    public ProfesionalResponseDTO modificar(Long id, ProfesionalRequestDTO dto) {
        Profesional profesional = buscarEntidadPorId(id);

        String nuevaMatricula = dto.matricula().trim();
        if (!profesional.getMatricula().equalsIgnoreCase(nuevaMatricula) && profesionalRepository.existsByMatricula(nuevaMatricula)) {
            throw new ReglaDeNegocioException("Ya existe un profesional registrado con esa matrícula.");
        }

        profesional.setNombre(dto.nombre().trim());
        profesional.setApellido(dto.apellido().trim());
        profesional.setMatricula(nuevaMatricula);
        profesional.setEspecialidad(dto.especialidad().trim());

        Profesional guardado = profesionalRepository.save(profesional);
        return profesionalMapper.entityToDTO(guardado);
    }

    public void borrar(Long id) {
        if (!profesionalRepository.existsById(id)) {
            throw new ProfesionalNotFoundException(id);
        }
        if (turnoRepository.existsByProfesional_Id(id)) {
            throw new ReglaDeNegocioException("No se puede eliminar porque tiene turnos asociados.");
        }
        profesionalRepository.deleteById(id);
    }

    public Profesional buscarEntidadPorId(Long id) {
        return profesionalRepository.findById(id)
                .orElseThrow(() -> new ProfesionalNotFoundException(id));
    }
}

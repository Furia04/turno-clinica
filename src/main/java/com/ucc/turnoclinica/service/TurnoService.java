package com.ucc.turnoclinica.service;

import com.ucc.turnoclinica.dto.TurnoRequestDTO;
import com.ucc.turnoclinica.dto.TurnoResponseDTO;
import com.ucc.turnoclinica.entity.EstadoTurno;
import com.ucc.turnoclinica.entity.Paciente;
import com.ucc.turnoclinica.entity.Profesional;
import com.ucc.turnoclinica.entity.Turno;
import com.ucc.turnoclinica.exception.PacienteNotFoundException;
import com.ucc.turnoclinica.exception.ProfesionalNotFoundException;
import com.ucc.turnoclinica.exception.ReglaDeNegocioException;
import com.ucc.turnoclinica.exception.TurnoNotFoundException;
import com.ucc.turnoclinica.mapper.TurnoMapper;
import com.ucc.turnoclinica.repository.PacienteRepository;
import com.ucc.turnoclinica.repository.ProfesionalRepository;
import com.ucc.turnoclinica.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final TurnoMapper turnoMapper;

    public TurnoService(TurnoRepository turnoRepository,
                        PacienteRepository pacienteRepository,
                        ProfesionalRepository profesionalRepository,
                        TurnoMapper turnoMapper) {
        this.turnoRepository = turnoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profesionalRepository = profesionalRepository;
        this.turnoMapper = turnoMapper;
    }

    public List<TurnoResponseDTO> obtenerTodos() {
        return turnoRepository.findAll().stream()
                .map(turnoMapper::entityToDTO)
                .toList();
    }

    public TurnoResponseDTO obtenerPorId(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));
        return turnoMapper.entityToDTO(turno);
    }

    public List<TurnoResponseDTO> obtenerPorPaciente(Long idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new PacienteNotFoundException(idPaciente);
        }
        return turnoRepository.findByPaciente_Id(idPaciente).stream()
                .map(turnoMapper::entityToDTO)
                .toList();
    }

    public List<TurnoResponseDTO> obtenerPorProfesional(Long idProfesional) {
        if (!profesionalRepository.existsById(idProfesional)) {
            throw new ProfesionalNotFoundException(idProfesional);
        }
        return turnoRepository.findByProfesional_Id(idProfesional).stream()
                .map(turnoMapper::entityToDTO)
                .toList();
    }

    public TurnoResponseDTO crear(TurnoRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.idPaciente())
                .orElseThrow(() -> new PacienteNotFoundException(dto.idPaciente()));

        Profesional profesional = profesionalRepository.findById(dto.idProfesional())
                .orElseThrow(() -> new ProfesionalNotFoundException(dto.idProfesional()));

        if (turnoRepository.existsByProfesional_IdAndFechaHora(profesional.getId(), dto.fechaHora())) {
            throw new ReglaDeNegocioException("Ese horario ya está reservado, elegí otro.");
        }

        if (turnoRepository.existsByPaciente_IdAndFechaHora(paciente.getId(), dto.fechaHora())) {
            throw new ReglaDeNegocioException("El paciente ya cuenta con un turno registrado en ese mismo horario.");
        }

        Turno turno = turnoMapper.dtoToEntity(dto, paciente, profesional);
        Turno guardado = turnoRepository.save(turno);
        return turnoMapper.entityToDTO(guardado);
    }

    public TurnoResponseDTO modificar(Long id, TurnoRequestDTO dto) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));

        Paciente paciente = pacienteRepository.findById(dto.idPaciente())
                .orElseThrow(() -> new PacienteNotFoundException(dto.idPaciente()));

        Profesional profesional = profesionalRepository.findById(dto.idProfesional())
                .orElseThrow(() -> new ProfesionalNotFoundException(dto.idProfesional()));

        boolean cambioHorarioOProfesional = !turno.getFechaHora().isEqual(dto.fechaHora())
                || !turno.getProfesional().getId().equals(profesional.getId());

        if (cambioHorarioOProfesional && turnoRepository.existsByProfesional_IdAndFechaHora(profesional.getId(), dto.fechaHora())) {
            throw new ReglaDeNegocioException("Ese horario ya está reservado, elegí otro.");
        }

        turno.setFechaHora(dto.fechaHora());
        turno.setMotivo(dto.motivo() != null ? dto.motivo().trim() : null);
        if (dto.estado() != null) {
            turno.setEstado(dto.estado());
        }
        turno.setPaciente(paciente);
        turno.setProfesional(profesional);

        Turno guardado = turnoRepository.save(turno);
        return turnoMapper.entityToDTO(guardado);
    }

    public void borrar(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new TurnoNotFoundException(id);
        }
        turnoRepository.deleteById(id);
    }
}

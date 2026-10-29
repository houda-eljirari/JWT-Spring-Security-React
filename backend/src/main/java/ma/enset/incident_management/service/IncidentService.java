package ma.enset.incident_management.service;

import ma.enset.incident_management.dto.IncidentRequest;
import ma.enset.incident_management.dto.IncidentResponse;
import ma.enset.incident_management.entity.Incident;
import ma.enset.incident_management.entity.User;
import ma.enset.incident_management.repository.IncidentRepository;
import ma.enset.incident_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import ma.enset.incident_management.entity.IncidentStatus;
import ma.enset.incident_management.dto.AssignTechnicianRequest;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public IncidentService(
            IncidentRepository incidentRepository,
            UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public IncidentResponse createIncident(
            IncidentRequest request, Authentication authentication) {

        User employee = getConnectedUser(authentication);

        Incident incident = new Incident();
        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setEmployee(employee);
        return toResponse(incidentRepository.save(incident));
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getMyIncidents(Authentication authentication) {
        User employee = getConnectedUser(authentication);

        return incidentRepository.findByEmployeeId(employee.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IncidentResponse getIncidentById(
            Long id, Authentication authentication) {

        User connectedUser = getConnectedUser(authentication);

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Incident introuvable"));

        boolean isAdmin = connectedUser.getRole().name().equals("ADMIN");
        boolean isEmployeeOwner = incident.getEmployee().getId()
                .equals(connectedUser.getId());
        boolean isAssignedTechnician = incident.getTechnician() != null
                && incident.getTechnician().getId().equals(connectedUser.getId());

        if (!isAdmin && !isEmployeeOwner && !isAssignedTechnician) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Vous n'avez pas le droit de consulter cet incident");
        }

        return toResponse(incident);
    }

    private User getConnectedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentification requise");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getAssignedIncidents(Authentication authentication) {
        User technician = getConnectedUser(authentication);

        if (!technician.getRole().name().equals("TECHNICIAN")
                && !technician.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Accès réservé aux techniciens et administrateurs");
        }

        return incidentRepository.findByTechnicianId(technician.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public IncidentResponse updateIncidentStatus(
            Long id, IncidentStatus status, Authentication authentication) {
        User user = getConnectedUser(authentication);

        if (!user.getRole().name().equals("TECHNICIAN")
                && !user.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Modification réservée aux techniciens et administrateurs");
        }

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Incident introuvable"));

        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isAssignedTechnician = incident.getTechnician() != null
                && incident.getTechnician().getId().equals(user.getId());

        if (!isAdmin && !isAssignedTechnician) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Vous ne pouvez modifier que les incidents qui vous sont affectés");
        }

        incident.setStatus(status);
        return toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentResponse assignTechnician(
            Long incidentId,
            AssignTechnicianRequest request,
            Authentication authentication) {

        User admin = getConnectedUser(authentication);

        if (!admin.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Accès réservé aux administrateurs");
        }

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident introuvable"));

        User technician = userRepository
                .findByUsername(request.technicianUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Technicien introuvable"));

        if (!technician.getRole().name().equals("TECHNICIAN")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'utilisateur indiqué n'est pas un technicien");
        }

        incident.setTechnician(technician);

        return toResponse(incidentRepository.save(incident));
    }

    private IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getStatus().name(),
                incident.getEmployee().getUsername(),
                incident.getTechnician() == null
                        ? null
                        : incident.getTechnician().getUsername(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}

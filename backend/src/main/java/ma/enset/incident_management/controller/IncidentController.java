package ma.enset.incident_management.controller;

import jakarta.validation.Valid;
import ma.enset.incident_management.dto.IncidentRequest;
import ma.enset.incident_management.dto.IncidentResponse;
import ma.enset.incident_management.service.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import ma.enset.incident_management.dto.UpdateIncidentStatusRequest;
import ma.enset.incident_management.dto.AssignTechnicianRequest;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody IncidentRequest request,
            Authentication authentication) {

        IncidentResponse response =
                incidentService.createIncident(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<IncidentResponse>> getMyIncidents(
            Authentication authentication) {

        return ResponseEntity.ok(
                incidentService.getMyIncidents(authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                incidentService.getIncidentById(id, authentication));
    }
    @GetMapping("/assigned")
    public List<IncidentResponse> getAssignedIncidents(
            Authentication authentication) {
        return incidentService.getAssignedIncidents(authentication);
    }

    @PutMapping("/{id}/status")
    public IncidentResponse updateIncidentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentStatusRequest request,
            Authentication authentication) {
        return incidentService.updateIncidentStatus(
                id, request.status(), authentication);
    }

    @PutMapping("/{id}/assign")
    public IncidentResponse assignTechnician(
            @PathVariable Long id,
            @Valid @RequestBody AssignTechnicianRequest request,
            Authentication authentication) {

        return incidentService.assignTechnician(
                id, request, authentication);
    }

}

package ma.enset.incident_management.dto;

import jakarta.validation.constraints.NotNull;
import ma.enset.incident_management.entity.IncidentStatus;

public record UpdateIncidentStatusRequest(
        @NotNull(message = "Le statut est obligatoire")
        IncidentStatus status
) {}

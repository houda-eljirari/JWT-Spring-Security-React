package ma.enset.incident_management.dto;

import java.time.LocalDateTime;

public record IncidentResponse(
        Long id,
        String title,
        String description,
        String status,
        String employeeUsername,
        String technicianUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}


package ma.enset.incident_management.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignTechnicianRequest(
        @NotBlank(message = "Le nom du technicien est obligatoire")
        String technicianUsername
) {}

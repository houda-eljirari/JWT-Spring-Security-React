package ma.enset.incident_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncidentRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères")
        String title,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
        String description
) {}

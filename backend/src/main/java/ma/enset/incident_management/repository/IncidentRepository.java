package ma.enset.incident_management.repository;

import ma.enset.incident_management.entity.Incident;
import ma.enset.incident_management.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByEmployeeId(Long employeeId);

    List<Incident> findByTechnicianId(Long technicianId);

    List<Incident> findByStatus(IncidentStatus status);
}

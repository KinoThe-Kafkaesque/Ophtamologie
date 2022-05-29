package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PatientRepositoryWithBagRelationships {
    Optional<Patient> fetchBagRelationships(Optional<Patient> patient);

    List<Patient> fetchBagRelationships(List<Patient> patients);

    Page<Patient> fetchBagRelationships(Page<Patient> patients);
}

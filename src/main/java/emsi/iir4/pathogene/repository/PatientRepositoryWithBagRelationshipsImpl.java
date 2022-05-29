package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Patient;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PatientRepositoryWithBagRelationshipsImpl implements PatientRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Patient> fetchBagRelationships(Optional<Patient> patient) {
        return patient.map(this::fetchDetections);
    }

    @Override
    public Page<Patient> fetchBagRelationships(Page<Patient> patients) {
        return new PageImpl<>(fetchBagRelationships(patients.getContent()), patients.getPageable(), patients.getTotalElements());
    }

    @Override
    public List<Patient> fetchBagRelationships(List<Patient> patients) {
        return Optional.of(patients).map(this::fetchDetections).orElse(Collections.emptyList());
    }

    Patient fetchDetections(Patient result) {
        return entityManager
            .createQuery("select patient from Patient patient left join fetch patient.detections where patient is :patient", Patient.class)
            .setParameter("patient", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Patient> fetchDetections(List<Patient> patients) {
        return entityManager
            .createQuery(
                "select distinct patient from Patient patient left join fetch patient.detections where patient in :patients",
                Patient.class
            )
            .setParameter("patients", patients)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}

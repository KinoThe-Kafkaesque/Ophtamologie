package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Patient;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Patient entity.
 */
@Repository
public interface PatientRepository extends PatientRepositoryWithBagRelationships, JpaRepository<Patient, Long> {
    default Optional<Patient> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Patient> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Patient> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
    Optional<Patient> findByUserId(Long id);
    Optional<Patient> findByUserLogin(String login);
    Set<Patient> findBySecretaireId(Long id);

}

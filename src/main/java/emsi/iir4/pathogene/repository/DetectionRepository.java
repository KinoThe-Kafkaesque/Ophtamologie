package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Detection;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Detection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DetectionRepository extends JpaRepository<Detection, Long> {}

package emsi.iir4.pathogene.repository;

import emsi.iir4.pathogene.domain.Detection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface DetectionRepositoryWithBagRelationships {
    Optional<Detection> fetchBagRelationships(Optional<Detection> detection);

    List<Detection> fetchBagRelationships(List<Detection> detections);

    Page<Detection> fetchBagRelationships(Page<Detection> detections);
}

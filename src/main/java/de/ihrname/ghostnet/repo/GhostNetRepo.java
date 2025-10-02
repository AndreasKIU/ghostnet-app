package de.ihrname.ghostnet.repo;

import de.ihrname.ghostnet.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface GhostNetRepo extends JpaRepository<GhostNet, Long> {
  List<GhostNet> findByStatusInOrderByCreatedAtAsc(Set<NetStatus> statuses);
}

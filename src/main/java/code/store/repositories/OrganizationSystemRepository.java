package code.store.repositories;

import code.store.entities.OrganizationSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationSystemRepository extends JpaRepository<OrganizationSystemEntity, Long> {
}

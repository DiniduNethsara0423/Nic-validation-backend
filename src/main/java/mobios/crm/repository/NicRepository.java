package mobios.crm.repository;

import mobios.crm.entity.Nic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NicRepository extends JpaRepository<Nic,Long> {
    long count ();
    long countByGender (String male);
}

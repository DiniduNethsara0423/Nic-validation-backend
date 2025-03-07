package mobios.crm.repository;

import mobios.crm.entity.Nic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NicRepository extends JpaRepository<Nic,Long> {
    long count ();
    long countByGender (String male);



}

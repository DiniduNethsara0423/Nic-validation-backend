package mobios.crm.repository;

import mobios.crm.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {
//    File findByFileName(String fileName);
    long count ();

    Optional<File> findByFileName(String fileName);

}

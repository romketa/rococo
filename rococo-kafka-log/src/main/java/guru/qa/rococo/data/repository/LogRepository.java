package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.LogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity, UUID> {

}

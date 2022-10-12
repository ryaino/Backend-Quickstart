package field.ryan.backendquickstart.db.repositories;

import field.ryan.backendquickstart.db.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
}

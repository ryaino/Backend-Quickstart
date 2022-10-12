package field.ryan.backendquickstart.db.repositories;

import field.ryan.backendquickstart.db.entities.AssignedUserRole;
import field.ryan.backendquickstart.db.entities.AssignedUserRolesKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignedUserRolesRepository extends JpaRepository<AssignedUserRole, AssignedUserRolesKey> {
}

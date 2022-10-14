package field.ryan.backendquickstart.db.entities;

import java.io.Serializable;
import java.util.UUID;

public class AssignedUserRolesKey implements Serializable {
    private UUID userId;
    private String userRoleName;

}

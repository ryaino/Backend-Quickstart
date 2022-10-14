package field.ryan.backendquickstart.db.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "assigned_user_roles")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AssignedUserRolesKey.class)
@Getter
@Setter
public class AssignedUserRole {

    @Id
    private UUID userId;
    @Id
    private String userRoleName;
    
}


package field.ryan.backendquickstart.db.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "assigned_user_roles")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AssignedUserRolesKey.class)
@Getter
@Setter
public class AssignedUserRole {

    @Id
    @ManyToOne
    private User user;

    @Id
    @ManyToOne
    private UserRole userRole;


}


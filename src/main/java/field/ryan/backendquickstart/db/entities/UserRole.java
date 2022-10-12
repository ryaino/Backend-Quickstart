package field.ryan.backendquickstart.db.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRole {

    @Id
    private String name;

    @OneToMany(mappedBy = "userRole")
    List<AssignedUserRole> assignedUserRoles = new ArrayList<>();

}

package field.ryan.backendquickstart.db.entities;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;
    private String email;
    private String password;
    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER)
    private Set<AssignedUserRole> roles = new HashSet<>();

}

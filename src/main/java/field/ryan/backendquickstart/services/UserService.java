package field.ryan.backendquickstart.services;

import field.ryan.backendquickstart.db.dtos.UserDto;
import field.ryan.backendquickstart.db.entities.AssignedUserRole;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.db.entities.UserRole;
import field.ryan.backendquickstart.db.repositories.AssignedUserRolesRepository;
import field.ryan.backendquickstart.db.repositories.UserRepository;
import field.ryan.backendquickstart.db.repositories.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private AssignedUserRolesRepository assignedUserRolesRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        AssignedUserRole assignedUserRole = new AssignedUserRole();
        UserRole role = userRoleRepository.findById("ROLE_USER").orElseThrow();
        assignedUserRole.setUserId(newUser.getId());
        assignedUserRole.setUserRoleName(role.getName());
        assignedUserRolesRepository.save(assignedUserRole);
        return newUser;
    }

    public UserDto mapToDto(User user) {
        UserDto dto = modelMapper.map(user, UserDto.class);
        dto.setRoles(flattenUserRoles(user.getRoles()));
        return dto;
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public List<String> flattenUserRoles(Set<AssignedUserRole> assignedUserRoleSet) {
        return assignedUserRoleSet
                .stream()
                .map(assignedUserRole -> assignedUserRole.getUserRoleName())
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        flattenUserRoles(user.getRoles()).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public org.springframework.security.core.userdetails.User convertToSecurityUser(User user) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        flattenUserRoles(user.getRoles()).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

}

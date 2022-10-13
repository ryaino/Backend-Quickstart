package field.ryan.backendquickstart.services;

import field.ryan.backendquickstart.db.dtos.UserDto;
import field.ryan.backendquickstart.db.entities.AssignedUserRole;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.db.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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
                .map(assignedUserRole -> assignedUserRole.getUserRole().getName())
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("");
        }
        return userRepository.findByEmail(email);
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


}

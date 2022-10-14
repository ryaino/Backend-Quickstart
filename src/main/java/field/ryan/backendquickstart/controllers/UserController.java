package field.ryan.backendquickstart.controllers;

import field.ryan.backendquickstart.db.dtos.UserDto;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.dto.RegisterUserInput;
import field.ryan.backendquickstart.services.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@ResponseBody
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;
    private ModelMapper modelMapper;

    @GetMapping("/api")
    public List<UserDto> getAllUsers() {
        return userService.findAll().stream().map( user -> userService.mapToDto(user)).collect(Collectors.toList());
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody RegisterUserInput input) {
        User user = modelMapper.map(input, User.class);
        userService.saveUser(user);
    }

}

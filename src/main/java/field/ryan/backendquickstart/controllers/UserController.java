package field.ryan.backendquickstart.controllers;

import field.ryan.backendquickstart.db.dtos.UserDto;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.services.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@ResponseBody
public class UserController {

    private UserService userService;
    private ModelMapper modelMapper;

    @GetMapping("/api")
    public List<UserDto> getAllUsers() {
        return userService.findAll().stream().map( user -> userService.mapToDto(user)).collect(Collectors.toList());
    }

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

}

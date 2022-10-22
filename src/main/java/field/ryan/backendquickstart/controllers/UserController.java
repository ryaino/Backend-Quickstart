package field.ryan.backendquickstart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import field.ryan.backendquickstart.db.dtos.UserDto;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.dto.RegisterUserInput;
import field.ryan.backendquickstart.services.JwtService;
import field.ryan.backendquickstart.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    private final JwtService jwtService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/register")
    public void registerUser(@RequestBody RegisterUserInput input) {
        User user = modelMapper.map(input, User.class);
        userService.saveUser(user);
    }

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                Map<String, String> tokens = jwtService.refreshAccessToken(request);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Unable to refresh token");
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

}

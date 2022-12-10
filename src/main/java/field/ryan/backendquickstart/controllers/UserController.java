package field.ryan.backendquickstart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.dto.ActionPayload;
import field.ryan.backendquickstart.dto.RefreshTokenOutput;
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
    public Boolean registerUser(@RequestBody ActionPayload<RegisterUserInput> body) {
        User user = modelMapper.map(body.getInput(), User.class);
        userService.saveUser(user);
        return true;
    }

    @PostMapping("/refresh")
    public RefreshTokenOutput refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        RefreshTokenOutput output = new RefreshTokenOutput();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                Map<String, String> tokens = jwtService.refreshAccessToken(request);
                output.setAccess_token(tokens.get("access_token"));
            } catch (Exception e) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Unable to refresh token");
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
        return output;
    }

}

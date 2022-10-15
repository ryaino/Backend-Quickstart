package field.ryan.backendquickstart.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import field.ryan.backendquickstart.db.dtos.UserDto;
import field.ryan.backendquickstart.db.entities.User;
import field.ryan.backendquickstart.dto.RegisterUserInput;
import field.ryan.backendquickstart.services.UserService;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/api")
    public List<UserDto> getAllUsers() {
        return userService.findAll().stream().map( user -> userService.mapToDto(user)).collect(Collectors.toList());
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody RegisterUserInput input) {
        User user = modelMapper.map(input, User.class);
        userService.saveUser(user);
    }

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String email = decodedJWT.getSubject();
                User user = userService.getUserByEmail(email);

                String access_token = createJwtAccessToken(userService.convertToSecurityUser(user), algorithm, user.getId() , request);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token",access_token);
                tokens.put("refresh_token",refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                log.error(e.toString());
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    private String createJwtAccessToken(org.springframework.security.core.userdetails.User user, Algorithm algorithm, UUID userId, HttpServletRequest request) {
        List<String> roles = new ArrayList<>(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        roles.add("anonymous");
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("x-hasura-allowed-roles",roles)
                .withClaim("x-hasura-default-role", "ROLE_USER")
                .withClaim("x-hasura-user-id", userId.toString())
                .sign(algorithm);
    }

}

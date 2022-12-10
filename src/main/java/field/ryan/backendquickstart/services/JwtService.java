package field.ryan.backendquickstart.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String createAccessToken(User user, UUID userId, HttpServletRequest request) {
        List<String> roles = new ArrayList<>(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        roles.add("anonymous");
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("x-hasura-allowed-roles",roles)
                .withClaim("x-hasura-default-role", "ROLE_USER")
                .withClaim("x-hasura-user-id", userId.toString())
                .sign(getAlgorithm());
    }

    public String createRefreshToken(User user, HttpServletRequest request) {
        List<String> roles = new ArrayList<>(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        roles.add("anonymous");
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("x-hasura-allowed-roles", roles)
                .withClaim("x-hasura-default-role", "ROLE_USER")
                .sign(getAlgorithm());
    }

    public Map<String, String> refreshAccessToken(HttpServletRequest request) {
        String refresh_token = request.getHeader(AUTHORIZATION).substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refresh_token);
        String email = decodedJWT.getSubject();
        field.ryan.backendquickstart.db.entities.User user = userService.getUserByEmail(email);

        String access_token = createAccessToken(userService.convertToSecurityUser(user), user.getId() , request);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);
        return tokens;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret.getBytes());
    }
}

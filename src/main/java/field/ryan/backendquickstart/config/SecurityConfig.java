package field.ryan.backendquickstart.config;

import field.ryan.backendquickstart.filters.CustomAuthenticationFilter;
import field.ryan.backendquickstart.filters.CustomAuthorizationFilter;
import field.ryan.backendquickstart.services.JwtService;
import field.ryan.backendquickstart.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationConfiguration configuration;
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/api/user/**").permitAll();
        http.authorizeRequests().antMatchers("/test").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new CustomAuthenticationFilter(authenticationManager(), userService, jwtService));
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    CustomAuthorizationFilter customAuthorizationFilter() {
        return new CustomAuthorizationFilter(jwtSecret);
    }
}

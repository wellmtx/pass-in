package rocketseat.passin.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rocketseat.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import rocketseat.passin.repositories.AttendeeRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final AttendeeRepository attendeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.attendeeRepository.findByEmail(username).orElseThrow(
                () -> new AttendeeNotFoundException("Attendee not found")
        );
    }

    public String generateJwtToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(this.getExpirationTime())
                .sign(algorithm);
    }

    private Instant getExpirationTime() {
        return LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("-03:00"));
    }
}

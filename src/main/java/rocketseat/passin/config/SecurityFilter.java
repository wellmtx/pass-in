package rocketseat.passin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rocketseat.passin.domain.attendee.Attendee;
import rocketseat.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import rocketseat.passin.repositories.AttendeeRepository;
import rocketseat.passin.services.AuthService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final AuthService authService;
    private final AttendeeRepository attendeeRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = this.extractToken(request);

        if (token != null) {
            String email = this.authService.validateJwtToken(token);

            Attendee attendee = this.attendeeRepository.findByEmail(email).orElseThrow(
                    () -> new AttendeeNotFoundException("Attendee not found")
            );

            var authentication = new UsernamePasswordAuthenticationToken(attendee, null, attendee.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return null;
        }

        if (!token.startsWith("Bearer ")) {
            return null;
        }

        return token.split(" ")[1];
    }
}

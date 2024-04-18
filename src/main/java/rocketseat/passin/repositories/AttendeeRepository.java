package rocketseat.passin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rocketseat.passin.domain.attendee.Attendee;

import java.util.List;
import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<Attendee, String>{
    List<Attendee> findByEventId(String eventId);
    Optional<Attendee> findByEmailAndEventId(String email, String eventId);
    Optional<Attendee> findByEmail(String email);
}

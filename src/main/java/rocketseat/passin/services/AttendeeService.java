package rocketseat.passin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import rocketseat.passin.domain.attendee.Attendee;
import rocketseat.passin.domain.attendee.exceptions.AttendeeAlreadyExistsException;
import rocketseat.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import rocketseat.passin.domain.checkin.CheckIn;
import rocketseat.passin.dto.attendee.*;
import rocketseat.passin.repositories.AttendeeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final CheckInService checkInService;
    private final AuthService authService;

    public AttendeeAuthResponseDTO authenticateAttendee(AttendeeAuthenticateDTO dto) {
        Attendee attendee = this.attendeeRepository.findByEmail(dto.email()).orElseThrow(
                () -> new AttendeeNotFoundException("Attendee not found with email: " + dto.email())
        );

        String token = this.authService.generateJwtToken(attendee.getEmail());

        return new AttendeeAuthResponseDTO(token);
    }

    public List<Attendee> getAllAttendeesFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId) {
        List<Attendee> attendees = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetails> attendeesDetails = attendees.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkInService.getCheckIn(attendee.getId());

            LocalDateTime checkedInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);

            return new AttendeeDetails(
                    attendee.getId(),
                    attendee.getName(),
                    attendee.getEmail(),
                    attendee.getCreatedAt(),
                    checkedInAt
            );
        }).toList();

        return new AttendeesListResponseDTO(attendeesDetails);
    }

    public void verifyAttendeeSubscription(String email, String eventId) {
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEmailAndEventId(email, eventId);

        if (isAttendeeRegistered.isPresent()) throw new AttendeeAlreadyExistsException("Attendee already registered");
    }

    public Attendee registerAttendee(Attendee newAttendee) {
        this.attendeeRepository.save(newAttendee);
        return newAttendee;
    }

    public void checkInAttendee(String attendeeId) {
        Attendee attendee = this.getAttendee(attendeeId);
        this.checkInService.registerCheckIn(attendee);
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriBuilder) {
        Attendee attendee = this.getAttendee(attendeeId);

        var uri = uriBuilder.path("/attendees/{attendeeId}/check-in").buildAndExpand(attendeeId).toUri();

        AttendeeBadgeDTO badge = new AttendeeBadgeDTO(
                attendee.getName(),
                attendee.getEmail(),
                uri.toString(),
                attendee.getEvent().getId()
        );

        return new AttendeeBadgeResponseDTO(badge);
    }

    private Attendee getAttendee(String attendeeId) {
        return this.attendeeRepository.findById(attendeeId).orElseThrow(
                () -> new AttendeeNotFoundException("Attendee not found with id: " + attendeeId)
        );
    }
}

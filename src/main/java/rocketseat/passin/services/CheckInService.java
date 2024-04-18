package rocketseat.passin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rocketseat.passin.domain.attendee.Attendee;
import rocketseat.passin.domain.checkin.CheckIn;
import rocketseat.passin.domain.checkin.exceptions.CheckInAlreadyExistsException;
import rocketseat.passin.repositories.CheckInRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final CheckInRepository checkInRepository;

    public void registerCheckIn(Attendee attendee) {
        this.verifyCheckInExists(attendee.getId());

        CheckIn checkIn = new CheckIn();
        checkIn.setAttendee(attendee);
        checkIn.setCreatedAt(LocalDateTime.now());
        this.checkInRepository.save(checkIn);
    }

    private void verifyCheckInExists(String attendeeId) {
        Optional<CheckIn> isCheckedIn = this.checkInRepository.findByAttendeeId(attendeeId);
        if (isCheckedIn.isPresent()) {
            throw new CheckInAlreadyExistsException("Attendee is already checked in");
        }
    }

    public Optional<CheckIn> getCheckIn(String attendeeId) {
        return this.checkInRepository.findByAttendeeId(attendeeId);
    }
}

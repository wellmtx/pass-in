package rocketseat.passin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rocketseat.passin.domain.attendee.Attendee;
import rocketseat.passin.domain.event.Event;
import rocketseat.passin.domain.event.exceptions.EventFullException;
import rocketseat.passin.domain.event.exceptions.EventNotFoundException;
import rocketseat.passin.dto.attendee.AttendeeIdDTO;
import rocketseat.passin.dto.attendee.AttendeeRequestDTO;
import rocketseat.passin.dto.event.EventIdDTO;
import rocketseat.passin.dto.event.EventRequestDTO;
import rocketseat.passin.dto.event.EventResponseDTO;
import rocketseat.passin.repositories.EventRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId) {
        Event event = this.getEventById(eventId);

        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        return new EventResponseDTO(event, attendeeList.size());
    }

    public EventIdDTO createEvent(EventRequestDTO dto) {
        Event event = new Event();
        event.setTitle(dto.title());
        event.setDetails(dto.details());
        event.setMaximumAttendees(dto.maximumAttendees());
        event.setSlug(this.createSlug(dto.title()));

        this.eventRepository.save(event);

        return new EventIdDTO(event.getId());
    }

    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO dto) {
        this.attendeeService.verifyAttendeeSubscription(dto.email(), eventId);

        Event event = this.getEventById(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);

        if (event.getMaximumAttendees() <= attendeeList.size()) throw new EventFullException("Event is full");

        Attendee attendee = new Attendee();
        attendee.setName(dto.name());
        attendee.setEmail(dto.email());
        attendee.setEvent(event);
        attendee.setCreatedAt(LocalDateTime.now());

        this.attendeeService.registerAttendee(attendee);

        return new AttendeeIdDTO(attendee.getId());
    }

    private Event getEventById(String id) {
        return this.eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
    }

    private String createSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD).replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}", "");
        return normalized.replaceAll("[^\\w\\s]", "").replaceAll(" ", "-").toLowerCase();
    }
}

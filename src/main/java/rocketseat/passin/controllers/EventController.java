package rocketseat.passin.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import rocketseat.passin.dto.attendee.AttendeeIdDTO;
import rocketseat.passin.dto.attendee.AttendeeRequestDTO;
import rocketseat.passin.dto.attendee.AttendeesListResponseDTO;
import rocketseat.passin.dto.event.EventIdDTO;
import rocketseat.passin.dto.event.EventRequestDTO;
import rocketseat.passin.dto.event.EventResponseDTO;
import rocketseat.passin.services.AttendeeService;
import rocketseat.passin.services.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final AttendeeService attendeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String id) {
        EventResponseDTO event = this.eventService.getEventDetail(id);
        return ResponseEntity.ok().body(event);
    }

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO dto, UriComponentsBuilder uriBuilder) {
        EventIdDTO eventId = this.eventService.createEvent(dto);

        var uri = uriBuilder.path("/events/{id}").buildAndExpand(eventId.eventId()).toUri();

        return ResponseEntity.created(uri).body(eventId);
    }

    @PostMapping("/{id}/attendees")
    public ResponseEntity<AttendeeIdDTO> registerParticipant(@PathVariable String id, @RequestBody AttendeeRequestDTO dto, UriComponentsBuilder uriBuilder) {
        AttendeeIdDTO attendeeId = this.eventService.registerAttendeeOnEvent(id, dto);

        var uri = uriBuilder.path("/attendees/{attendeeId}/badge").buildAndExpand(attendeeId.attendeeId()).toUri();

        return ResponseEntity.created(uri).body(attendeeId);
    }

    @GetMapping("/{id}/attendees")
    public ResponseEntity<AttendeesListResponseDTO> getEventAttendees(@PathVariable String id) {
        AttendeesListResponseDTO attendees = this.attendeeService.getEventsAttendee(id);
        return ResponseEntity.ok().body(attendees);
    }
}

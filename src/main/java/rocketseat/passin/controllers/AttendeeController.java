package rocketseat.passin.controllers;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import rocketseat.passin.dto.attendee.AttendeeAuthResponseDTO;
import rocketseat.passin.dto.attendee.AttendeeAuthenticateDTO;
import rocketseat.passin.dto.attendee.AttendeeBadgeResponseDTO;
import rocketseat.passin.services.AttendeeService;
import rocketseat.passin.services.CheckInService;

@RestController
@RequestMapping("/attendees")
@RequiredArgsConstructor
public class AttendeeController {
    private final AttendeeService attendeeService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AttendeeAuthenticateDTO dto, UriComponentsBuilder uriBuilder) {
        AttendeeAuthResponseDTO token = this.attendeeService.authenticateAttendee(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{attendeeId}/badge")
    public ResponseEntity<AttendeeBadgeResponseDTO> getAttendeeBadge(@PathVariable String attendeeId, UriComponentsBuilder uriBuilder) {
        AttendeeBadgeResponseDTO response = this.attendeeService.getAttendeeBadge(attendeeId, uriBuilder);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attendeeId}/check-in")
    public ResponseEntity<Object> registerCheckIn(@PathVariable String attendeeId, UriComponentsBuilder uriBuilder) {
        this.attendeeService.checkInAttendee(attendeeId);

        var uri = uriBuilder.path("/attendees/{attendeeId}/badge").buildAndExpand(attendeeId).toUri();

        return ResponseEntity.created(uri).build();
    }
}

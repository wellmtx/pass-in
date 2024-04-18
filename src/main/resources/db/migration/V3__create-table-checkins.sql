CREATE TABLE check_ins (
    id INT NOT NULL PRIMARY KEY IDENTITY,
    attendee_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_ins_attendee_id_fk FOREIGN KEY (attendee_id) REFERENCES attendees(id) ON DELETE RESTRICT ON UPDATE CASCADE
);
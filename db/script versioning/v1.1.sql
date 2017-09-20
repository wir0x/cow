-- CREATE NEW TABLE FOR SAVING AND SENDING NOTIFICATIONS
CREATE TABLE notifications
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    from_name VARCHAR(255),
    from_address VARCHAR(255),
    to_address VARCHAR(255),
    subject VARCHAR(255),
    content LONGTEXT,
    status VARCHAR(255),
    type VARCHAR(255),
    created_date DATETIME,
    sent_date DATETIME,
    error_description LONGTEXT,
    company_id BIGINT,
    position_id BIGINT
);

-- ADD NEW COLUMNS FOR SENDING ALERTS
ALTER TABLE device ADD sos_mails VARCHAR (255);

ALTER TABLE device ADD sos_cellphones VARCHAR (255);

ALTER TABLE device ADD battery_mails VARCHAR (255);

ALTER TABLE device ADD battery_cellphones VARCHAR (255);

-- ADD NEW COLUMN TO POSITION POINTER
ALTER TABLE position_pointer ADD alert_type VARCHAR (255);

UPDATE position_pointer SET alert_type = 'FENCE' WHERE id = 1;

INSERT INTO position_pointer (id, pointer, alert_type) VALUES (2, (SELECT id FROM positions ORDER BY id DESC LIMIT 1), 'SOS');


ALTER TABLE users DROP generate_password;
ALTER TABLE devices ADD generated_id VARCHAR(255) NULL;
ALTER TABLE subscription DROP invitation_mails;
ALTER TABLE subscription DROP invitation_numbers;

-- 2016-02-17 16:08
ALTER TABLE  service_plan ADD COLUMN duration_months INT(11) NOT NULL;

CREATE TABLE alarm
(
    id BIGINT(20) PRIMARY KEY NOT NULL,
    date_received DATETIME,
    date_sent DATETIME,
    device_id BIGINT(20),
    latitude DOUBLE,
    is_sent BIT(1),
    longitude DOUBLE,
    speed DOUBLE,
    type VARCHAR(255)
);
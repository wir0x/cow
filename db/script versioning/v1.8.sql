ALTER TABLE users ADD COLUMN is_without_expiration BIT;

INSERT INTO configurations (description, key_, value) VALUES ('Default expired time session', 'default.expired.time', '2');
INSERT INTO configurations (description, key_, value) VALUES ('Additional expired time session when navigated', 'additional.expired.time', '2');
INSERT INTO configurations (description, key_, value) VALUES ('Identify key for mobiles apps', 'mobile.os.key', '[androidViewer];[iosViewer]');
INSERT INTO configurations (description, key_, value) VALUES ('Url when not add additional time', 'urls.no.extend.session.time', '/authenticate/check-session-time;/position/list/last');
INSERT INTO configurations (description, key_, value) VALUES ('GCM URL ', 'gcm.url', 'https://android.googleapis.com/gcm/send');
INSERT INTO configurations (description, key_, value) VALUES ('Authorization token', 'auth.token', 'AIzaSyA9Lp7n388WwDl8xnB7TmbwSUDeVVCSvw4');
INSERT INTO configurations (description, key_, value) VALUES ('limit speed for device in positions (km)', 'limit.position.speed', '250');
INSERT INTO configurations (description, key_, value) VALUES ('limit distance between positions (in meteres)', 'limit.distance', '40');
INSERT INTO configurations (description, key_, value) VALUES ('Limit speed of device', 'limit.speed', '200');


ALTER TABLE device_type ADD report_limit BIGINT;

UPDATE device_type SET alarm_battery = true, alarm_sos = false, battery = false, description = 'Queclink GV300', report_limit = 50 WHERE id = 1;
UPDATE device_type SET alarm_battery = true, alarm_sos = true, battery = true, description = 'Queclink GL200', report_limit = 15 WHERE id = 2;
UPDATE device_type SET alarm_battery = false, alarm_sos = true, battery = false, description = 'Chino RFV8', report_limit = 10 WHERE id = 3;
UPDATE device_type SET alarm_battery = false, alarm_sos = false, battery = false, description = 'Chino LK 106', report_limit = 10 WHERE id = 4;
UPDATE device_type SET alarm_battery = false, alarm_sos = false, battery = false, description = 'Smartphone', report_limit = 80 WHERE id = 5;



CREATE TABLE user_viewer
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  smartphone_id BIGINT,
  user_id BIGINT,
  FOREIGN KEY (smartphone_id) REFERENCES smartphone (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX idx_user_viewer ON user_viewer (user_id, smartphone_id);
CREATE INDEX fk_user_smartphone_smartphone_id ON user_viewer (smartphone_id);


CREATE TABLE device_notification
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    datetime VARCHAR(255),
    fence_id VARCHAR(255),
    fence_name VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    message VARCHAR(255),
    title VARCHAR(255),
    device_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (device_id) REFERENCES device (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX fk_device_notification_device_id ON device_notification (device_id);
CREATE INDEX fk_device_notification_user_id ON device_notification (user_id);


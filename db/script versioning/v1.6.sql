-- add new column to table positions
ALTER TABLE positions ADD sent_date DATETIME;

-- create new table to sort buffer position sent
CREATE TABLE positions_buffer
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  altitude DOUBLE,
  battery VARCHAR(255),
  command VARCHAR(255),
  course DOUBLE,
  extended_info LONGTEXT,
  latitude DOUBLE,
  longitude DOUBLE,
  speed DOUBLE,
  time DATETIME,
  valid BIT,
  device_id BIGINT,
  sent_date DATETIME,
  FOREIGN KEY (device_id) REFERENCES device (id)
);


-- 2015 06 22 add new property configuration (SMS LIMIT, )
INSERT INTO configurations (id, description, key_, value) VALUES (26, 'configuration for idle time (time in sec.) (default 10 min= 600 sec)', 'default.idle.time', '600');
INSERT INTO configurations (id, description, key_, value) VALUES (27, 'Configuration to SMS LIMIT (default 90 %)', 'default.sms.limit', '90');
INSERT INTO configurations (id, description, key_, value) VALUES (28, 'Default group name to Super admin', 'default.super.admin.group.id', '1');

-- add table device Type
CREATE TABLE device_type
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    alarm_battery BIT,
    alarm_sos BIT,
    battery BIT,
    description VARCHAR(255)
);

-- insert data to Device type
INSERT INTO device_type (id, alarm_battery, alarm_sos, battery, description) VALUES (1, true, false, true, 'Queclink GV300');
INSERT INTO device_type (id, alarm_battery, alarm_sos, battery, description) VALUES (2, true, true, false, 'Queclink GL200');
INSERT INTO device_type (id, alarm_battery, alarm_sos, battery, description) VALUES (3, false, true, false, 'Chino RFV8');
INSERT INTO device_type (id, alarm_battery, alarm_sos, battery, description) VALUES (4, false, false, false, 'Chino LK 106');

-- drop unnecessary columns to table Device
ALTER TABLE device
DROP COLUMN battery;

ALTER TABLE device
DROP COLUMN sos;

ALTER TABLE device
DROP COLUMN type;





-- ADD new column to table device
ALTER TABLE device
ADD COLUMN device_type_id BIGINT;
ALTER TABLE device
ADD FOREIGN KEY (device_type_id)
REFERENCES device_type (id);
CREATE INDEX fk_device_device_type_id ON device (device_type_id);

-- DETAILED REPORT (add new role, update configuration, insert group role)
INSERT INTO roles (id, name) VALUES (14, 'detailed-report');
UPDATE configurations SET description = '', key_ = 'default.admin.group.roles', value = '1,2,3,4,5,6,9,10,11,13,14' WHERE id = 3;
INSERT INTO group_role (group_id, role_id) SELECT id, 14 FROM groups WHERE name = 'Administrador';

-- UPDATE device_type ON DEVICE
-- Note!! this script set device_type_id for default 1, Change device_type from Application
UPDATE device SET device_type_id = (SELECT id FROM device_type WHERE id = 1);




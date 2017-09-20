-- CREATE NEW TABLE FOR CONTROL LIMIT OF SMS
CREATE TABLE sms_control
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  device_id BIGINT,
  month_year DATE,
  max_sms INT,
  used_sms INT,
  FOREIGN KEY (device_id) REFERENCES device (id)
);

-- Subscription for device
CREATE TABLE subscription
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  end_date DATE,
  max_sms INT,
  start_date DATE,
  device_id BIGINT,
  comment VARCHAR(512),
  name_package VARCHAR(255),
  FOREIGN KEY (device_id) REFERENCES device (id)
);
CREATE INDEX fk_subscription_device_id ON subscription (device_id);


-- insert new role for buho admin
INSERT INTO roles (id, name) VALUES (12, 'subscription');
-- insert new role for group admin
INSERT INTO group_role (group_id, role_id) VALUES (1, 12);
-- update new role of the key

-- update table subscription (2015-05-28)
UPDATE configurations SET description = '', key_ = 'default.admin.group.roles', value = '1,2,3,4,5,6,9,10,11,13' WHERE id = 3;
INSERT INTO roles (id, name) VALUES (13, 'state-account');
INSERT INTO group_role (group_id, role_id) SELECT id, 13 FROM groups WHERE name = 'Administrador';

-- add new column to table sms_control
ALTER TABLE sms_control ADD sent_mail BOOLEAN;


-- update table subscription (2015-05-28)
ALTER TABLE subscription ADD comment VARCHAR(512);
ALTER TABLE subscription ADD name_package VARCHAR(255);

UPDATE configurations SET description = '', key_ = 'default.admin.group.roles', value = '1,2,3,4,5,6,9,10,11,13' WHERE id = 3;
INSERT INTO roles (id, name) VALUES (13, 'state-account');
INSERT INTO group_role (group_id, role_id) SELECT id, 13 FROM groups WHERE name = 'Administrador';

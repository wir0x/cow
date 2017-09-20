-- CONFIGURATION FOR VERSION 1.7
INSERT INTO configurations (description, key_, value) VALUES ('default group id for app users', 'default.app.user.group.id', '31');
INSERT INTO configurations (description, key_, value) VALUES ('free service plan id', 'free.service.plan.id', '1');
INSERT INTO configurations (description, key_, value) VALUES ('tigo money encryption key', 'tm.encrypt.key', '2XYIO4AO80NDAGC0LXX3O5DM');
INSERT INTO configurations (description, key_, value) VALUES ('tigo money customer id key', 'tm.id.key', 'ba7f80f5081708f676bf1cd28a28de238b27271abd8613a494465df880798e42efcb314172f2cbfc277dca1a6494a6225359354ecee0b9e167ed6aea3a8c655b');
INSERT INTO configurations (description, key_, value) VALUES ('smartphone device type id', 'smartphone.device.type.id', '5');
INSERT INTO configurations (description, key_, value) VALUES ('prefix for tigo money order id', 'tm.order.prefix', 'buho-dev-');
INSERT INTO configurations (description, key_, value) VALUES ('Low battery level 1 for GPS GL200 (20%)', 'low.battery.level.1', '20');
INSERT INTO configurations (description, key_, value) VALUES ('Low battery Level 2 for GPS GL200 (10%)', 'low.battery.level.2', '10');
INSERT INTO configurations (description, key_, value) VALUES ('Time low battery limit for gl200 in(Seconds - 30min)', 'time.low.battery.limit', '1800');
INSERT INTO configurations (description, key_, value) VALUES ('Device type id GPS Gl200', 'gl200.device.type.id', '2');
INSERT INTO configurations (description, key_, value) VALUES ('Number of months to show for status account in user', 'month.number.to.show', '3');
INSERT INTO configurations (description, key_, value) VALUES ('Limit days remaining of subscription(Day)', 'remaining.days.subscription.1', '15');
INSERT INTO configurations (description, key_, value) VALUES ('Limit days remaining of subscription(day)   ', 'remaining.days.subscription.2', '5');
INSERT INTO configurations (description, key_, value) VALUES ('When time out exception repeat times', 'tm.timeout.times', '3');
INSERT INTO configurations (description, key_, value) VALUES ('Waiting time for next request to TigoMoney in seconds', 'tm.wait.sleep', '10');
INSERT INTO configurations (description, key_, value) VALUES ('Business name of SwissBytes', 'swissbytes.business.name', 'SwissBytes Ltda.');
INSERT INTO configurations (description, key_, value) VALUES ('Business nit of SwissBytes', 'swissbytes.business.nit', '149386022');


-- ALTER POSITION POINTER
INSERT INTO position_pointer (id, pointer, alert_type) VALUES (4, 2953504, 'LOW_BATTERY');


-- INSERT CLIENT DEVICE TO BUHO SUPPORT ACCOUNT
INSERT INTO user_device (device_id, user_id) VALUES (7, 21); -- alexander castro
INSERT INTO user_device (device_id, user_id) VALUES (26, 21); --
INSERT INTO user_device (device_id, user_id) VALUES (27, 21);
INSERT INTO user_device (device_id, user_id) VALUES (28, 21);
INSERT INTO user_device (device_id, user_id) VALUES (33, 21);


-- TABLE ALARM BATTERY FOR GL200
CREATE TABLE alarm_battery
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    level_battery BIGINT,
    register_date DATETIME,
    device_id BIGINT,
    FOREIGN KEY (device_id) REFERENCES device (id)
);
CREATE INDEX fk_alarm_battery_device_id ON alarm_battery (device_id);


-- TABLE SERVICE PLAN
CREATE TABLE service_plan
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  duration_days BIGINT NOT NULL,
  price DOUBLE NOT NULL
);

-- INSERT SERVICE PLANS
INSERT INTO service_plan VALUES (0, 'Tu primer mes gratis', 'Te damos la bienvenida a Buho con 30 días de regalo', '30', 0);
INSERT INTO service_plan VALUES (0, '3 meses', 'Servicio de monitoreo por 90 días', '90', 390);
INSERT INTO service_plan VALUES (0, '6 meses', 'Servicio de monitoreo por 180 días', '180', 660);
INSERT INTO service_plan VALUES (0, '12 meses', 'Servicio de monitoreo por 360 días', '360', 1200);


-- ADD COLUMN AND RELATION AND SET ON TABLE SUBSCRIPTION
ALTER TABLE subscription ADD service_plan_id BIGINT;
ALTER TABLE subscription ADD FOREIGN KEY (service_plan_id) REFERENCES service_plan(id);
UPDATE subscription SET service_plan_id = 1;


-- INSERT DEVICE TYPE
INSERT INTO device_type VALUES (0,false,false,false,'Smartphone');


-- TABLE PAYMENT_REQUEST
CREATE TABLE payment_request
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  creation_date DATETIME NOT NULL,
  status VARCHAR(50) NOT NULL,
  status_detail VARCHAR(255),
  document_number VARCHAR(10),
  message VARCHAR(100),
  amount DOUBLE NOT NULL,
  line BIGINT(8) NOT NULL,
  name VARCHAR(100),
  correct_url VARCHAR(100) NOT NULL,
  error_url VARCHAR(200) NOT NULL,
  confirmation VARCHAR(20),
  notification VARCHAR(20),
  business_name VARCHAR(40),
  nit VARCHAR(20),
  user_id BIGINT NOT NULL,
  device_id BIGINT NOT NULL,
  service_plan_id BIGINT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (device_id) REFERENCES device(id),
  FOREIGN KEY (service_plan_id) REFERENCES service_plan(id)
);


-- TABLE PAYMENT REQUEST ITEM
CREATE TABLE payment_request_item
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  serial INT(3) NOT NULL,
  quantity INT(3) NOT NULL,
  concept VARCHAR(255) NOT NULL,
  unit_price DOUBLE NOT NULL,
  total_price DOUBLE NOT NULL,
  payment_request_id BIGINT NOT NULL,
  FOREIGN KEY (payment_request_id) REFERENCES payment_request(id)
);

ALTER TABLE subscription ADD payment_request_id BIGINT;
ALTER TABLE subscription ADD FOREIGN KEY (payment_request_id) REFERENCES payment_request(id);


-- TABLE SMARTPHONE
CREATE TABLE smartphone
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  imei VARCHAR(255) NOT NULL,
  gcm_token TEXT NOT NULL,
  creation_date DATETIME NOT NULL,
  modification_date DATETIME NOT NULL,
  application_type VARCHAR(30) NOT NULL,
  INDEX app_imei (application_type,imei)
);

ALTER TABLE device
ADD COLUMN smartphone_id BIGINT,
ADD CONSTRAINT fk_device_smartphone_id FOREIGN KEY (smartphone_id) REFERENCES smartphone (id);


DROP PROCEDURE IF EXISTS delete_smartphone;

DELIMITER //
CREATE PROCEDURE delete_smartphone_with_imei (IN p_imei VARCHAR(255))
  BEGIN
    delete from user_device where device_id in (select id from device where smartphone_id in (select id from smartphone where imei = p_imei));
    delete from subscription where device_id in (select id from device where smartphone_id in (select id from smartphone where imei = p_imei));
    delete from payment_request_item where payment_request_id in (select id from payment_request where device_id in (select id from device where smartphone_id in (select id from smartphone where imei = p_imei)));
    delete from payment_request where device_id in (select id from device where smartphone_id in (select id from smartphone where imei = p_imei));
    delete from device where smartphone_id in (select id from smartphone where imei = p_imei);
    delete from smartphone where imei = p_imei;
  END //
DELIMITER ;
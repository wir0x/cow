
-- DELETE TABLE SMARTPHONE
DROP TABLE smartphone;

-- DELETE TABLE DEVICE-NOTIFICATION
DROP TABLE device_notification;

-- ACCOUNT
ALTER TABLE company RENAME TO account;

-- DEVICE
ALTER TABLE device DROP FOREIGN KEY fk_device_company_id;
DROP INDEX fk_device_company_id ON device;
ALTER TABLE device CHANGE company_id account_id BIGINT;
ALTER TABLE device ADD COLUMN gcm_token VARCHAR(255);
ALTER TABLE device ADD COLUMN modification_date DATETIME;

ALTER TABLE device DROP FOREIGN KEY fk_device_smartphone_id;
DROP INDEX fk_device_smartphone_id ON device;
ALTER TABLE device DROP smartphone_id;

ALTER TABLE device
ADD CONSTRAINT fk_device_account_id FOREIGN KEY (account_id) REFERENCES account (id);


-- NOTIFICATION
ALTER TABLE notifications CHANGE company_id account_id BIGINT;

-- PAYMENT REQUEST
ALTER TABLE payment_request DROP FOREIGN KEY payment_request_ibfk_1;
ALTER TABLE payment_request DROP user_id;
ALTER TABLE payment_request DROP FOREIGN KEY payment_request_ibfk_2;
ALTER TABLE payment_request DROP device_id;
ALTER TABLE payment_request DROP FOREIGN KEY payment_request_ibfk_3;
ALTER TABLE payment_request DROP service_plan_id;

-- SMS CONTROL
ALTER TABLE sms_control DROP sent_date;

-- VIEWER
DROP TABLE user_viewer;
CREATE TABLE viewer
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    creation_date DATETIME NOT NULL,
    gcm_token VARCHAR(255),
    imei VARCHAR(255) NOT NULL,
    modification_date DATETIME NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX fk_viewer_user_id ON viewer (user_id);


INSERT INTO buhodb.viewer (id, creation_date, gcm_token, imei, modification_date, user_id) VALUES (2, '2015-09-01 22:12:28', 'fAgGAwZQBlM:APA91bH3L2FIP5Vo1FhdL6i_siuQnDtp17xkCaA3ZcM13VoD0BRRvcgq-_n4FUWVaoszwqsNYpZksgs05KLM_CsDMbKKJI-CzukHlxxvB8de3SW2CVeBC20_gz-IStBkGWaSo6f9AW56', '357962059919373', '2015-09-10 17:27:48', 26);
INSERT INTO buhodb.viewer (id, creation_date, gcm_token, imei, modification_date, user_id) VALUES (3, '2015-09-11 18:52:22', 'cp32UArx-e4:APA91bGgUxNw1dNxJY1Byt02Kc4fMeUn9eUqVKRAd_uROyMLqW8ZApeQVfZd2P5ZEjtX3FMczVMgV4tC31Fk9CumUNr-o0puEGUz9-7UXl5J9okQufZ1U_8vmasCY954Q6WWuZ3wUy0E', '354989068392295', '2015-09-11 18:52:22', 43);
INSERT INTO buhodb.viewer (id, creation_date, gcm_token, imei, modification_date, user_id) VALUES (4, '2015-09-11 19:02:27', 'cwY9_mPfrxw:APA91bHWtq30eNDGwEoQ085ou50k3iCgVlet99o8899tXKh7-XOParLkyvt3tOtjfun469HLo3ekL5SK5MxLNlNeEsE66UdFib3VXK1mwS7Bup52u7SnIJzm6AItXkPcvvYjJUq9l_M1', '354989068400775', '2015-09-11 19:02:27', 43);
INSERT INTO buhodb.viewer (id, creation_date, gcm_token, imei, modification_date, user_id) VALUES (5, '2015-08-31 15:59:51', 'coj1FOTVqSE:APA91bGzKIKzap1XQRwbgKUpiEGo9IV06gHrBkl0sttDfTHFIMxBYQl6A4SComlcxgRWX5LGCdibDnX61kvAcm9HdHwg5rdIjWUtFEVqAS_LGEXPUsmaRG5cOrUYPQT7csA9aqRF19tQ', '358377060662095', '2015-08-31 15:59:51', 55);
INSERT INTO buhodb.viewer (id, creation_date, gcm_token, imei, modification_date, user_id) VALUES (6, '2015-09-11 20:22:59', 'ccc5ui1o20I:APA91bEysQJM6f17UIY30ari-eFkQsa0NBZjKWX7gDMJCk8buA_N8dt4qrAnbtUXGII-4mwtMR-ot1FKX0lx07L5HyVF9R7pMbb7fc_fGPJdqRsx6B0uhlVWPVtIbXkufBUkt5m4sssN', '354989068391016', '2015-09-11 20:22:59', 58);



-- USER
ALTER TABLE users DROP FOREIGN KEY fk_user_group_id;
DROP INDEX fk_user_group_id ON users;
ALTER TABLE users DROP group_id;

ALTER TABLE users ADD COLUMN account_id BIGINT;
ALTER TABLE users
ADD CONSTRAINT fk_user_account_id FOREIGN KEY (account_id) REFERENCES account (id);


-- ADD NEW TABLES

DROP TABLE group_role;
DROP TABLE groups;
DROP TABLE roles;

CREATE TABLE roles
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    visible BIT,
    description VARCHAR(255)
);
CREATE UNIQUE INDEX idx_role_name ON roles (name);

CREATE TABLE permissions
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL
);

CREATE TABLE role_permission
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    role_id BIGINT,
    permission_id INT,
    FOREIGN KEY (role_id) REFERENCES roles (id),
    FOREIGN KEY (permission_id) REFERENCES permissions (id)
);
CREATE UNIQUE INDEX idx_role_permission ON role_permission (role_id, permission_id);
CREATE INDEX fk_role_permission_permission_id ON role_permission (permission_id);
CREATE INDEX fk_role_permission_role_id ON role_permission (role_id);

CREATE TABLE user_role
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);
CREATE UNIQUE INDEX idx_user_role ON user_role (user_id, role_id);
CREATE INDEX fk_user_role_user_id ON user_role (user_id);
CREATE INDEX fk_user_role_role_id ON user_role (role_id);



-- TABLE PERMISSIONS
INSERT INTO permissions (id, name) VALUES (1, 'current-position');
INSERT INTO permissions (id, name) VALUES (2, 'history');
INSERT INTO permissions (id, name) VALUES (3, 'reports');
INSERT INTO permissions (id, name) VALUES (4, 'control');
INSERT INTO permissions (id, name) VALUES (5, 'device-config');
INSERT INTO permissions (id, name) VALUES (6, 'user-management');
INSERT INTO permissions (id, name) VALUES (7, 'device-management');
INSERT INTO permissions (id, name) VALUES (8, 'account-management');
INSERT INTO permissions (id, name) VALUES (9, 'frequently-question');
INSERT INTO permissions (id, name) VALUES (10, 'help');
INSERT INTO permissions (id, name) VALUES (11, 'contact');
INSERT INTO permissions (id, name) VALUES (12, 'subscription');
INSERT INTO permissions (id, name) VALUES (13, 'state-account');
INSERT INTO permissions (id, name) VALUES (14, 'detailed-report');

-- TABLE ROLES
INSERT INTO roles (id, name, visible, description) VALUES (1, 'Administrador de sistema', false, 'System admin');
INSERT INTO roles (id, name, visible, description) VALUES (2, 'Administrador de la cuenta', false, 'account admin');
INSERT INTO roles (id, name, visible, description) VALUES (3, 'Administrador', true, 'UI Admin');
INSERT INTO roles (id, name, visible, description) VALUES (4, 'Usuario', true, 'UI normal user');

-- TABLE ROLE PERMISSION
INSERT INTO role_permission (id, role_id, permission_id) VALUES (1, 1, 7);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (2, 1, 8);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (3, 1, 12);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (4, 3, 1);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (5, 3, 2);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (6, 3, 3);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (7, 3, 4);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (8, 3, 5);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (9, 3, 6);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (10, 3, 9);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (11, 3, 10);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (12, 3, 11);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (13, 3, 13);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (14, 3, 14);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (15, 4, 1);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (16, 4, 2);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (17, 4, 3);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (18, 4, 9);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (19, 4, 10);
INSERT INTO role_permission (id, role_id, permission_id) VALUES (20, 4, 11);



-- REMOVE fields USERS
ALTER TABLE users DROP COLUMN is_system_admin;
ALTER TABLE users DROP COLUMN is_company_admin;

-- USERS
UPDATE users SET account_id = 1 WHERE id = 2;
UPDATE users SET account_id = 1 WHERE id = 10;
UPDATE users SET account_id = 2 WHERE id = 3;
UPDATE users SET account_id = 3 WHERE id = 4;
UPDATE users SET account_id = 3 WHERE id = 8;
UPDATE users SET account_id = 4 WHERE id = 5;
UPDATE users SET account_id = 5 WHERE id = 6;
UPDATE users SET account_id = 6 WHERE id = 11;
UPDATE users SET account_id = 6 WHERE id = 13;
UPDATE users SET account_id = 7 WHERE id = 14;
UPDATE users SET account_id = 8 WHERE id = 15;
UPDATE users SET account_id = 9 WHERE id = 18;
UPDATE users SET account_id = 9 WHERE id = 19;
UPDATE users SET account_id = 9 WHERE id = 20;
UPDATE users SET account_id = 11 WHERE id = 24;
UPDATE users SET account_id = 11 WHERE id = 26;
UPDATE users SET account_id = 11 WHERE id = 51;
UPDATE users SET account_id = 11 WHERE id = 52;
UPDATE users SET account_id = 11 WHERE id = 54;
UPDATE users SET account_id = 12 WHERE id = 25;
UPDATE users SET account_id = 13 WHERE id = 27;
UPDATE users SET account_id = 13 WHERE id = 29;
UPDATE users SET account_id = 14 WHERE id = 30;
UPDATE users SET account_id = 15 WHERE id = 31;
UPDATE users SET account_id = 15 WHERE id = 41;
UPDATE users SET account_id = 15 WHERE id = 49;
UPDATE users SET account_id = 16 WHERE id = 32;
UPDATE users SET account_id = 17 WHERE id = 34;
UPDATE users SET account_id = 18 WHERE id = 35;
UPDATE users SET account_id = 18 WHERE id = 40;
UPDATE users SET account_id = 19 WHERE id = 36;
UPDATE users SET account_id = 20 WHERE id = 37;
UPDATE users SET account_id = 21 WHERE id = 38;
UPDATE users SET account_id = 22 WHERE id = 39;
UPDATE users SET account_id = 23 WHERE id = 42;
UPDATE users SET account_id = 24 WHERE id = 43;
UPDATE users SET account_id = 24 WHERE id = 58;
UPDATE users SET account_id = 24 WHERE id = 59;
UPDATE users SET account_id = 24 WHERE id = 60;
UPDATE users SET account_id = 25 WHERE id = 44;
UPDATE users SET account_id = 26 WHERE id = 45;
UPDATE users SET account_id = 26 WHERE id = 48;
UPDATE users SET account_id = 27 WHERE id = 46;
UPDATE users SET account_id = 28 WHERE id = 55;

-- TABLE USER ROLE
INSERT INTO user_role (user_id, role_id) VALUES (1,1);
INSERT INTO user_role (user_id, role_id) VALUES (2,3);
INSERT INTO user_role (user_id, role_id) VALUES (10,3);
INSERT INTO user_role (user_id, role_id) VALUES (3,3);
INSERT INTO user_role (user_id, role_id) VALUES (4,3);
INSERT INTO user_role (user_id, role_id) VALUES (8,3);
INSERT INTO user_role (user_id, role_id) VALUES (5,3);
INSERT INTO user_role (user_id, role_id) VALUES (6,3);
INSERT INTO user_role (user_id, role_id) VALUES (11,3);
INSERT INTO user_role (user_id, role_id) VALUES (13,4);
INSERT INTO user_role (user_id, role_id) VALUES (14,3);
INSERT INTO user_role (user_id, role_id) VALUES (15,3);
INSERT INTO user_role (user_id, role_id) VALUES (18,3);
INSERT INTO user_role (user_id, role_id) VALUES (19,4);
INSERT INTO user_role (user_id, role_id) VALUES (20,4);
INSERT INTO user_role (user_id, role_id) VALUES (24,3);
INSERT INTO user_role (user_id, role_id) VALUES (26,3);
INSERT INTO user_role (user_id, role_id) VALUES (51,3);
INSERT INTO user_role (user_id, role_id) VALUES (52,4);
INSERT INTO user_role (user_id, role_id) VALUES (54,4);
INSERT INTO user_role (user_id, role_id) VALUES (25,3);
INSERT INTO user_role (user_id, role_id) VALUES (27,3);
INSERT INTO user_role (user_id, role_id) VALUES (29,4);
INSERT INTO user_role (user_id, role_id) VALUES (30,3);
INSERT INTO user_role (user_id, role_id) VALUES (31,3);
INSERT INTO user_role (user_id, role_id) VALUES (41,4);
INSERT INTO user_role (user_id, role_id) VALUES (49,4);
INSERT INTO user_role (user_id, role_id) VALUES (32,3);
INSERT INTO user_role (user_id, role_id) VALUES (34,3);
INSERT INTO user_role (user_id, role_id) VALUES (35,3);
INSERT INTO user_role (user_id, role_id) VALUES (40,4);
INSERT INTO user_role (user_id, role_id) VALUES (36,3);
INSERT INTO user_role (user_id, role_id) VALUES (37,3);
INSERT INTO user_role (user_id, role_id) VALUES (38,3);
INSERT INTO user_role (user_id, role_id) VALUES (39,3);
INSERT INTO user_role (user_id, role_id) VALUES (42,3);
INSERT INTO user_role (user_id, role_id) VALUES (43,3);
INSERT INTO user_role (user_id, role_id) VALUES (58,4);
INSERT INTO user_role (user_id, role_id) VALUES (59,4);
INSERT INTO user_role (user_id, role_id) VALUES (60,4);
INSERT INTO user_role (user_id, role_id) VALUES (44,3);
INSERT INTO user_role (user_id, role_id) VALUES (45,3);
INSERT INTO user_role (user_id, role_id) VALUES (48,4);
INSERT INTO user_role (user_id, role_id) VALUES (46,3);
INSERT INTO user_role (user_id, role_id) VALUES (55,3);

INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO user_role (user_id, role_id) VALUES (3, 2);
INSERT INTO user_role (user_id, role_id) VALUES (4, 2);
INSERT INTO user_role (user_id, role_id) VALUES (5, 2);
INSERT INTO user_role (user_id, role_id) VALUES (6, 2);
INSERT INTO user_role (user_id, role_id) VALUES (11, 2);
INSERT INTO user_role (user_id, role_id) VALUES (14, 2);
INSERT INTO user_role (user_id, role_id) VALUES (15, 2);
INSERT INTO user_role (user_id, role_id) VALUES (18, 2);
INSERT INTO user_role (user_id, role_id) VALUES (24, 2);
INSERT INTO user_role (user_id, role_id) VALUES (25, 2);
INSERT INTO user_role (user_id, role_id) VALUES (27, 2);
INSERT INTO user_role (user_id, role_id) VALUES (30, 2);
INSERT INTO user_role (user_id, role_id) VALUES (31, 2);
INSERT INTO user_role (user_id, role_id) VALUES (32, 2);
INSERT INTO user_role (user_id, role_id) VALUES (34, 2);
INSERT INTO user_role (user_id, role_id) VALUES (35, 2);
INSERT INTO user_role (user_id, role_id) VALUES (36, 2);
INSERT INTO user_role (user_id, role_id) VALUES (37, 2);
INSERT INTO user_role (user_id, role_id) VALUES (38, 2);
INSERT INTO user_role (user_id, role_id) VALUES (39, 2);
INSERT INTO user_role (user_id, role_id) VALUES (42, 2);
INSERT INTO user_role (user_id, role_id) VALUES (43, 2);
INSERT INTO user_role (user_id, role_id) VALUES (44, 2);
INSERT INTO user_role (user_id, role_id) VALUES (45, 2);
INSERT INTO user_role (user_id, role_id) VALUES (46, 2);
INSERT INTO user_role (user_id, role_id) VALUES (55, 2);

INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-08-31 15:24:36', 'ecw0aTt73FA:APA91bHOTtlI299jevVH8v6_M0_ShiE6rYkOyI9BqIx87iKpf-VM1zOPSNBJogSASvz1yH-Cca3kXjuXow8furOW7nScJ0YHSDrQpdW4YcqO5gZA5_Od8enZmdnP0EcubRdxYaaOvELx', '355896053048601', '2015-09-11 08:11:31', 26);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-01 22:12:28', 'fAgGAwZQBlM:APA91bH3L2FIP5Vo1FhdL6i_siuQnDtp17xkCaA3ZcM13VoD0BRRvcgq-_n4FUWVaoszwqsNYpZksgs05KLM_CsDMbKKJI-CzukHlxxvB8de3SW2CVeBC20_gz-IStBkGWaSo6f9AW56', '357962059919373', '2015-09-10 17:27:48', 26);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-11 18:52:22', 'cp32UArx-e4:APA91bGgUxNw1dNxJY1Byt02Kc4fMeUn9eUqVKRAd_uROyMLqW8ZApeQVfZd2P5ZEjtX3FMczVMgV4tC31Fk9CumUNr-o0puEGUz9-7UXl5J9okQufZ1U_8vmasCY954Q6WWuZ3wUy0E', '354989068392295', '2015-09-11 18:52:22', 43);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-11 19:02:27', 'cwY9_mPfrxw:APA91bHWtq30eNDGwEoQ085ou50k3iCgVlet99o8899tXKh7-XOParLkyvt3tOtjfun469HLo3ekL5SK5MxLNlNeEsE66UdFib3VXK1mwS7Bup52u7SnIJzm6AItXkPcvvYjJUq9l_M1', '354989068400775', '2015-09-11 19:02:27', 43);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-08-31 15:59:51', 'coj1FOTVqSE:APA91bGzKIKzap1XQRwbgKUpiEGo9IV06gHrBkl0sttDfTHFIMxBYQl6A4SComlcxgRWX5LGCdibDnX61kvAcm9HdHwg5rdIjWUtFEVqAS_LGEXPUsmaRG5cOrUYPQT7csA9aqRF19tQ', '358377060662095', '2015-08-31 15:59:51', 55);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-11 20:22:59', 'ccc5ui1o20I:APA91bEysQJM6f17UIY30ari-eFkQsa0NBZjKWX7gDMJCk8buA_N8dt4qrAnbtUXGII-4mwtMR-ot1FKX0lx07L5HyVF9R7pMbb7fc_fGPJdqRsx6B0uhlVWPVtIbXkufBUkt5m4sssN', '354989068391016', '2015-09-11 20:22:59', 58);
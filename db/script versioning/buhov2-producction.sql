-- DELETE TABLE SMARTPHONE
-- DROP TABLE smartphone;


-- DELETE TABLE DEVICE-NOTIFICATION
-- DROP TABLE device_notification;


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


UPDATE configurations SET description = '', key_ = 'role.user', value = '4' WHERE id = 4;

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


-- UPDATE USERS
UPDATE users SET account_id = 1 WHERE id = 2;
UPDATE users SET account_id = 1 WHERE id = 15;
UPDATE users SET account_id = 1 WHERE id = 12;
UPDATE users SET account_id = 2 WHERE id = 3;
UPDATE users SET account_id = 2 WHERE id = 9;
UPDATE users SET account_id = 2 WHERE id = 11;
UPDATE users SET account_id = 3 WHERE id = 4;
UPDATE users SET account_id = 3 WHERE id = 41;
UPDATE users SET account_id = 3 WHERE id = 42;
UPDATE users SET account_id = 3 WHERE id = 43;
UPDATE users SET account_id = 4 WHERE id = 5;
UPDATE users SET account_id = 5 WHERE id = 6;
UPDATE users SET account_id = 6 WHERE id = 13;
UPDATE users SET account_id = 6 WHERE id = 14;
UPDATE users SET account_id = 7 WHERE id = 16;
UPDATE users SET account_id = 8 WHERE id = 17;
UPDATE users SET account_id = 9 WHERE id = 18;
UPDATE users SET account_id = 9 WHERE id = 20;
UPDATE users SET account_id = 9 WHERE id = 23;
UPDATE users SET account_id = 10 WHERE id = 19;
UPDATE users SET account_id = 11 WHERE id = 21;
UPDATE users SET account_id = 12 WHERE id = 22;
UPDATE users SET account_id = 13 WHERE id = 24;
UPDATE users SET account_id = 14 WHERE id = 25;
UPDATE users SET account_id = 15 WHERE id = 26;
UPDATE users SET account_id = 15 WHERE id = 33;
UPDATE users SET account_id = 15 WHERE id = 34;
UPDATE users SET account_id = 15 WHERE id = 35;
UPDATE users SET account_id = 15 WHERE id = 36;
UPDATE users SET account_id = 15 WHERE id = 37;
UPDATE users SET account_id = 15 WHERE id = 38;
UPDATE users SET account_id = 15 WHERE id = 39;
UPDATE users SET account_id = 15 WHERE id = 40;
UPDATE users SET account_id = 15 WHERE id = 44;
UPDATE users SET account_id = 15 WHERE id = 45;
UPDATE users SET account_id = 15 WHERE id = 46;
UPDATE users SET account_id = 16 WHERE id = 27;
UPDATE users SET account_id = 16 WHERE id = 31;
UPDATE users SET account_id = 16 WHERE id = 32;


-- UPDATE USER ROLE
INSERT INTO user_role (user_id, role_id) VALUES (2,3);
INSERT INTO user_role (user_id, role_id) VALUES (15,3);
INSERT INTO user_role (user_id, role_id) VALUES (3,3);
INSERT INTO user_role (user_id, role_id) VALUES (4,3);
INSERT INTO user_role (user_id, role_id) VALUES (5,3);
INSERT INTO user_role (user_id, role_id) VALUES (6,3);
INSERT INTO user_role (user_id, role_id) VALUES (13,3);
INSERT INTO user_role (user_id, role_id) VALUES (16,3);
INSERT INTO user_role (user_id, role_id) VALUES (17,3);
INSERT INTO user_role (user_id, role_id) VALUES (18,3);
INSERT INTO user_role (user_id, role_id) VALUES (19,3);
INSERT INTO user_role (user_id, role_id) VALUES (21,3);
INSERT INTO user_role (user_id, role_id) VALUES (22,3);
INSERT INTO user_role (user_id, role_id) VALUES (24,3);
INSERT INTO user_role (user_id, role_id) VALUES (25,3);
INSERT INTO user_role (user_id, role_id) VALUES (26,3);
INSERT INTO user_role (user_id, role_id) VALUES (27,3);
INSERT INTO user_role (user_id, role_id) VALUES (1,1);
INSERT INTO user_role (user_id, role_id) VALUES (12,4);
INSERT INTO user_role (user_id, role_id) VALUES (9,4);
INSERT INTO user_role (user_id, role_id) VALUES (11,4);
INSERT INTO user_role (user_id, role_id) VALUES (41,4);
INSERT INTO user_role (user_id, role_id) VALUES (42,4);
INSERT INTO user_role (user_id, role_id) VALUES (43,4);
INSERT INTO user_role (user_id, role_id) VALUES (14,4);
INSERT INTO user_role (user_id, role_id) VALUES (20,4);
INSERT INTO user_role (user_id, role_id) VALUES (23,4);
INSERT INTO user_role (user_id, role_id) VALUES (33,4);
INSERT INTO user_role (user_id, role_id) VALUES (34,4);
INSERT INTO user_role (user_id, role_id) VALUES (35,4);
INSERT INTO user_role (user_id, role_id) VALUES (36,4);
INSERT INTO user_role (user_id, role_id) VALUES (37,4);
INSERT INTO user_role (user_id, role_id) VALUES (38,4);
INSERT INTO user_role (user_id, role_id) VALUES (39,4);
INSERT INTO user_role (user_id, role_id) VALUES (40,4);
INSERT INTO user_role (user_id, role_id) VALUES (44,4);
INSERT INTO user_role (user_id, role_id) VALUES (45,4);
INSERT INTO user_role (user_id, role_id) VALUES (46,4);
INSERT INTO user_role (user_id, role_id) VALUES (31,4);
INSERT INTO user_role (user_id, role_id) VALUES (32,4);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO user_role (user_id, role_id) VALUES (3, 2);
INSERT INTO user_role (user_id, role_id) VALUES (4, 2);
INSERT INTO user_role (user_id, role_id) VALUES (5, 2);
INSERT INTO user_role (user_id, role_id) VALUES (6, 2);
INSERT INTO user_role (user_id, role_id) VALUES (13, 2);
INSERT INTO user_role (user_id, role_id) VALUES (16, 2);
INSERT INTO user_role (user_id, role_id) VALUES (17, 2);
INSERT INTO user_role (user_id, role_id) VALUES (18, 2);
INSERT INTO user_role (user_id, role_id) VALUES (19, 2);
INSERT INTO user_role (user_id, role_id) VALUES (21, 2);
INSERT INTO user_role (user_id, role_id) VALUES (22, 2);
INSERT INTO user_role (user_id, role_id) VALUES (24, 2);
INSERT INTO user_role (user_id, role_id) VALUES (25, 2);
INSERT INTO user_role (user_id, role_id) VALUES (26, 2);
INSERT INTO user_role (user_id, role_id) VALUES (27, 2);

# UPDATE DEVICE
UPDATE device SET gcm_token = 'cFKng2FRL7w:APA91bFTZFi8Fw_RdUJHeCQBpyMYIjv2pPGPbZH42gVKPNasxZROqOjBgtVqePd1dzrwVvwM87AJMLuh5CwW11AXOtStGsOjXsDnVZVltAr41b242MqcIiL-RWXSl3yFAY6hC52q2DtM' WHERE id = 35;
UPDATE device SET gcm_token = 'fs5NuYJgvmE:APA91bEXdCOYMRZEP5cDqdAe70TZIGtux_msmB9g8_smZ2dhdS0VaFr6wl5Vz9OCXpm65WeX5YwyJQ2QlS5yHRwQVWQWJ27Pxu8xB8DBZtUAA_zTyoaEr0OeacoMp4dc62TZ8_bKtwwS' WHERE id = 36;
UPDATE device SET gcm_token = 'dEW0WyQ0AQs:APA91bHyyi4jmsmgIjsVw9MG3DrWyKsj3oeiuXFzW6ow62Q5_mzoTJ9h2t08vZL1DGMbowJHGlcrGeqYJTl5b-MC4mEVyJQCkTFeLX-XLIJF5ssytQcTYSxUB-_lJMvQjigrtGlQOn78' WHERE id = 40;
UPDATE device SET gcm_token = 'e9vyd1WEU10:APA91bEmiUfPRpBpmOPAEqYY4xPvk7w8xoyeaMipXGEmxzfDxi-Dyj3YyVBdlpgqX1awnA8kHjswJmXfPp1bfAFLAjyQqsIFcelOHUohhjhRaIWsW5YaVUUQwyZyJv1Mra2wSO0LxmA2' WHERE id = 37;
UPDATE device SET gcm_token = 'cotQQM2CYRk:APA91bFKpnUmAPwmehSdNmFBz3CN_Se_giUjncxasAnE6r8SN9oCvheB7fBzDjxrHeytO88NMKbK7-Hm8bIUIirdYSMd-MdJVXyGf3m3b_dStZeOxuQenuGVgy3pUTbZ4wUG3Tmc6MaK' WHERE id = 38;
UPDATE device SET gcm_token = 'cEyeTT8O6QQ:APA91bHMO39Wsh-O1fRKxiOKIzWAEypTfMMBYSznhWCnvJtHhmjhE1FMHd2EayjH_NLmo8DXHa2HuoD8Poz-iGJiQ_6FGPG7Hj_CutE8OJF7-qZ72ra2bSnJG0TiMx9w_-KamMhqFrv-' WHERE id = 39;
UPDATE device SET gcm_token = 'f41FHId591g:APA91bGPdwC-8mysPxCiO1EXeDcE2Wr8eBHFnllkPlkEV1wUuSlGAEDxs2S6jwDZN-ry1VmJcyoaeId5--_pZXMyF9Xr_Sxg0V4Ib0jMU5iVLu8Waqecu1BLSJqazRhrAZdqkbX8yGz-' WHERE id = 42;
UPDATE device SET gcm_token = 'c9Qq8atbhSQ:APA91bErctXCJAQkQnSwox5JOc2ocFlbAqyghP07Y7VCecEKxp80W7WBO_tKWJMvfFmOhjVG6hJFKVRYQl3LwuwlrvyZBMvsWaA3wpqS8lzKFf9Rko71HOUprLQQj9Axm8i2gYIGLOIE' WHERE id = 41;
UPDATE device SET gcm_token = 'cothTlHaRrE:APA91bFxQ-T-hs9krEUmaNECLJGRTwVGlAhhIXodCy1qPI45aoXrEXE4IcuMSPdQoOVeHOecVcqZU87iRIgbYcNeVdgeFkO61b-io_bYShBXjSgynRRTUYy52jv2aoWEw2QVAliG_EYc' WHERE id = 43;
UPDATE device SET gcm_token = 'eOxvI7HerbM:APA91bHSsrFfHZXAI133icSyvez-ptjl3zpyEJhuxzapmSFlDMtbN-CPCYV-nyWsiLvUGSxznORCt0gzeM-chPIwPYIEhQ5ElXUfwpjB9pvaltmWDpKONRYNqNTnjXIUnKOVOtH4qiyC' WHERE id = 44;
UPDATE device SET gcm_token = 'ciT0jrORzaE:APA91bHtsWb--DsZjsJHGN6mmH6mx9BB5YXMwsDaYz_rIpmfmnIGWG9DOmFmrHnm46Wu-DjzMfLRWA7mQUWJtQ37Lk15qG_46BNJlH_JYTkEs0Xx7-u7h5z0w0BR0z4ahy2fszLmPpuP' WHERE id = 45;

# INSERT VIEWER
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-11 22:34:24', 'cEOmDszOP6U:APA91bHzB8MmoST4uE7I1HUvFtKZhYDXLYOAWqbF-DpdKRI2tJk5pt46jqEiOhwRZ_PeLQGX_YiC9Bvv645WnK7xkjBXGRX66tR9UCqqLXQkHuC6MDpGL7_-fH1iYjU4GA4q8FpNxiEa', '358377060662095', '2015-09-12 06:49:07', 3);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-03 12:16:50', 'dEW0WyQ0AQs:APA91bHyyi4jmsmgIjsVw9MG3DrWyKsj3oeiuXFzW6ow62Q5_mzoTJ9h2t08vZL1DGMbowJHGlcrGeqYJTl5b-MC4mEVyJQCkTFeLX-XLIJF5ssytQcTYSxUB-_lJMvQjigrtGlQOn78', '358944063053315', '2015-09-17 16:51:44', 4);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-04 01:51:33', 'fMjqKDoWpAE:APA91bH2SUAa_YGmX2JdNVaT8gT_NLGlAemipy-Kc4jzF44OyJhd1DdXSCmynJiIQvAL-LJCsy9YwlP46dLP4EEPde98a4CAqXGm0jLzT7eYYRXbZzHWWIln_7l_oKAAw86txlG_XEVa', '357962059919373', '2015-09-15 10:52:42', 4);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-04 08:38:09', 'fTgs8dKLRzQ:APA91bHcCait8qWjxuGialPFzFJIGdyINtVkn9QTCcN2b_qAx3BRRWddjRjX-NjaKQoJvLH9_Xk4xreMucIpVg81HvdTFkLvBHRJiy2EBoHXqAlgA6G_UvqbXdPOas8MvwVx6AvrRurw', '354150064677672', '2015-09-14 15:40:31', 4);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-10 16:58:48', 'f41FHId591g:APA91bGPdwC-8mysPxCiO1EXeDcE2Wr8eBHFnllkPlkEV1wUuSlGAEDxs2S6jwDZN-ry1VmJcyoaeId5--_pZXMyF9Xr_Sxg0V4Ib0jMU5iVLu8Waqecu1BLSJqazRhrAZdqkbX8yGz-', '355896053048601', '2015-09-14 15:04:36', 4);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-07 14:30:11', 'cI_S0ktAWhA:APA91bFx1ReX9uIjySUQ9stTXZov2-pUrjHNz31G8eV-8f3hNW2hzKBaB6LygP9V0RYQiOE6L1H0S4WQNb4TV2CG1OVQF0h8kwMZ5lrDvcWEBvZEJX3qRYWO8KBAMgYlhofJppZ4oSjO', '353296061342108', '2015-09-11 20:54:05', 13);
INSERT INTO viewer (creation_date, gcm_token, imei, modification_date, user_id) VALUES ('2015-09-14 21:40:08', 'cuD7dJym8iU:APA91bENp7g4KgrcfArIikypR4c50ixIhFjlBPU5VndC_OaoNW6zLoRsDk-aLCrZViyokQA4SuzALBmiA1v8iMqQNFNZ3XiE9Y9P0oQuQIsdvaGjYGHRE7d8Rrtqyz9MGnUNUwoAdibt', '357512058822233', '2015-09-14 21:40:08', 14);


# INSERT CONFIGURATION
INSERT INTO configurations (id, description, key_, value) VALUES (60, null, 'role.admin.account', '2');
INSERT INTO configurations (id, description, key_, value) VALUES (61, null, 'app.account.id', '20');

# UPDATE CONFIGURATION
UPDATE configurations SET description = '', key_ = 'role.admin', value = '3' WHERE id = 3;

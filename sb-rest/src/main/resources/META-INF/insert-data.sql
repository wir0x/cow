-- INSERT COMPANY
INSERT INTO company (id, address, name, phone_number, status, email) VALUES (1, 'Calle Buen Retiro Nro 120', 'Swissbytes LTDA', '72001654', 'ENABLED', 'rodrigo.guzman@swissbytes.ch');

-- INSERT GROUP
INSERT INTO groups (id, name, company_id) VALUES (1, 'Super Admin', NULL);
INSERT INTO groups (id, name, company_id) VALUES (2, 'Administrador', 1);
INSERT INTO groups (id, name, company_id) VALUES (3, 'Usuario', 1);

-- INSERT USER
INSERT INTO users (id, email, name, password, status, user_name, group_id, is_company_admin, is_system_admin) VALUES (1, 'system.admin@buho.bo', 'Buho Tracking Solution', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'ENABLED', 'buho-admin', 1, FALSE, TRUE);
INSERT INTO users (id, email, name, password, status, user_name, group_id, is_company_admin, is_system_admin) VALUES (2, 'admin@buho.bo', 'Buho Tracking Solution', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'ENABLED', 'buho', 2, TRUE, FALSE);

-- INSERT DEVICE
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (1, '862894020221332', '+(591) 71343785', 'Francisco Sancheza sd', FALSE, 'ff6600', 'fa-automobile', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (2, '862894020188895', '+(591)67706896', 'ANA', FALSE, '2432ee', 'fa-eraser', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (3, '359710041258675', '+(591)67710382', 'GPS DLUX 102', FALSE, 'ff00ee', 'fa-male', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (4, '867844002173337', '+(591)71343779', 'GPS BLANCO GL200', TRUE, 'f500f5', 'fa-child', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (5, '4106012971', '+(591)00000', 'LK 106 A', FALSE, 'aa1042', 'fa-male', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (7, '862894020189687', '+5911234567', 'New-GV300 ', FALSE, '00f000', 'fa-male', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (8, '6410023182', '+591123456', 'RFV8S - 82sdfsf', TRUE, '7b1fa2', 'fa-female', 1, 'ENABLED');
INSERT INTO device (id, imei, phone_number, name, battery, color, icon, account_id, status) VALUES (9, '6410023181', '+59177355967', 'RFV8S - 81', TRUE, '7b1fa2', 'fa-female', 1, 'ENABLED');

-- INSERT ROLES
INSERT INTO roles (id, name) VALUES (1, 'current-position');
INSERT INTO roles (id, name) VALUES (2, 'history');
INSERT INTO roles (id, name) VALUES (3, 'reports');
INSERT INTO roles (id, name) VALUES (4, 'control');
INSERT INTO roles (id, name) VALUES (5, 'device-config');
INSERT INTO roles (id, name) VALUES (6, 'user-management');
INSERT INTO roles (id, name) VALUES (7, 'device-management');
INSERT INTO roles (id, name) VALUES (8, 'company-management');

-- INSERT GROUP ROLE
INSERT INTO group_role (id, group_id, role_id) VALUES (1, 1, 7);
INSERT INTO group_role (id, group_id, role_id) VALUES (2, 1, 8);
INSERT INTO group_role (id, group_id, role_id) VALUES (3, 2, 1);
INSERT INTO group_role (id, group_id, role_id) VALUES (4, 2, 2);
INSERT INTO group_role (id, group_id, role_id) VALUES (5, 2, 3);
INSERT INTO group_role (id, group_id, role_id) VALUES (6, 2, 4);
INSERT INTO group_role (id, group_id, role_id) VALUES (7, 2, 5);
INSERT INTO group_role (id, group_id, role_id) VALUES (8, 2, 6);
INSERT INTO group_role (id, group_id, role_id) VALUES (9, 3, 1);
INSERT INTO group_role (id, group_id, role_id) VALUES (10, 3, 2);
INSERT INTO group_role (id, group_id, role_id) VALUES (11, 3, 3);

-- INSERT INITIAL CONFIGURATION
INSERT INTO configurations (key_, value, description) VALUES ('default.admin.group.name', 'Administrador', 'this is the company administrator group, that can see all transactions');
INSERT INTO configurations (key_, value, description) VALUES ('default.user.group.name', 'Usuario', '');
INSERT INTO configurations (key_, value, description) VALUES ('default.admin.group.roles', '1,2,3,4,5,6', '');
INSERT INTO configurations (key_, value, description) VALUES ('default.user.group.roles', '1,2,3', '');
INSERT INTO configurations (key_, value, description) VALUES ('url.domain', 'https://app.buho.bo', '');
INSERT INTO configurations (key_, value, description) VALUES ('url.positionserver', 'gps.buho.bo', '');
INSERT INTO configurations (key_, value, description) VALUES ('url.frontend', '/#/', '');
INSERT INTO configurations (key_, value, description) VALUES ('url.rest', '/rest/', '');
INSERT INTO configurations (key_, value, description) VALUES ('page.reset.password', 'reset-password/', '');
INSERT INTO configurations (key_, value, description) VALUES ('company.test.id', '2', '');
INSERT INTO configurations (key_, value, description) VALUES ('system.administrator.group.id', '1', '');
INSERT INTO configurations (key_, value, description) VALUES ('date.format.short', 'dd-MM-yyyy', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.server', 'email-smtp.us-east-1.amazonaws.com', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.port', '2587', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.username', 'AKIAIM5PGRMCB5JB4ANQ', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.password', 'AvCM2wR2Ga8myS8FPeQxbn6dPBVUvkuH3pr9InDjVQIp', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.sender.address', 'no-reply@buho.bo', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.sender.name', 'no-reply@buho.bo', '');
INSERT INTO configurations (key_, value, description) VALUES ('smtp.ssl', 'true', '');
INSERT INTO configurations (key_, value, description) VALUES ('sms.url', 'https://secure.cm.nl/smssgateway/cm/gateway.ashx', '');
INSERT INTO configurations (key_, value, description) VALUES ('sms.token', '0bcad9dd-be6e-44cc-9490-2b0f70118b65', '');
INSERT INTO configurations (key_, value, description) VALUES ('sms.sender', '33575050', '');

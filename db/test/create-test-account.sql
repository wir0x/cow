-- company
INSERT INTO company (id, address, name, phone_number, status, email) VALUES (2, 'Calle Buen Retiro Nro 120', 'TESTER LTDA', '123456789', 'ENABLED', 'tester@swissbytes.ch');

-- group
INSERT INTO groups (id, name, company_id) VALUES (3, 'Administrador', 2);
INSERT INTO groups (id, name, company_id) VALUES (4, 'Usuario', 2);

-- group role
INSERT INTO group_role (group_id, role_id) VALUES (3, 1);
INSERT INTO group_role (group_id, role_id) VALUES (3, 2);
INSERT INTO group_role (group_id, role_id) VALUES (3, 3);
INSERT INTO group_role (group_id, role_id) VALUES (3, 4);
INSERT INTO group_role (group_id, role_id) VALUES (3, 5);
INSERT INTO group_role (group_id, role_id) VALUES (3, 6);
INSERT INTO group_role (group_id, role_id) VALUES (4, 1);
INSERT INTO group_role (group_id, role_id) VALUES (4, 2);
INSERT INTO group_role (group_id, role_id) VALUES (4, 3);

-- user
INSERT INTO users (id, is_company_admin, email, name, password, status, is_system_admin, user_name, group_id, phone_number) VALUES (10, TRUE, 'test@yopmail.com', 'Test Buho Tracking', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', 'ENABLED', FALSE, 'test', 3, '1234567');

-- devices
INSERT INTO device (battery, color, icon, imei, name, phone_number, status, account_id) VALUES (TRUE , '2432ee', 'fa-male', '111111111111111', 'PERSONA', '+(591)67706896', 'ENABLED', 2);
INSERT INTO device (battery, color, icon, imei, name, phone_number, status, account_id) VALUES (FALSE, 'ff00ee', 'fa-automobile', '222222222222222', 'AUTO', '+(591)67710382', 'ENABLED', 2);
INSERT INTO device (battery, color, icon, imei, name, phone_number, status, account_id) VALUES (FALSE, 'f06000', 'fa-taxi', '3333333333', 'TAXI', '+59177355967', 'ENABLED', 2);
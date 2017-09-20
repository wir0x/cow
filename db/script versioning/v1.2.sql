-- ADD NEW COLUMN TO DEVICE
ALTER TABLE device ADD sos BIT;

-- UPDATE DEVICES WITH NEW COLUMN
UPDATE device SET battery = true, sos = false WHERE id = 1;
UPDATE device SET battery = true, sos = false WHERE id = 2;
UPDATE device SET battery = false, sos = false WHERE id = 3;
UPDATE device SET battery = true, sos = true WHERE id = 4;
UPDATE device SET battery = false,sos = false WHERE id = 5;
UPDATE device SET battery = false, sos = false WHERE id = 6;
UPDATE device SET battery = true, sos = false WHERE id = 7;
UPDATE device SET battery = true, sos = true WHERE id = 8;
UPDATE device SET battery = true, sos = true WHERE id = 9;
UPDATE device SET battery = true, sos = false WHERE id = 10;
UPDATE device SET battery = true, sos = false WHERE id = 11;
UPDATE device SET battery = true, sos = false WHERE id = 12;
UPDATE device SET battery = true, sos = true WHERE id = 13;
UPDATE device SET battery = true, sos = true WHERE id = 14;
UPDATE device SET battery = true, sos = false WHERE id = 15;
UPDATE device SET battery = true, sos = true WHERE id = 16;
UPDATE device SET battery = true, sos = true WHERE id = 17;
UPDATE device SET battery = true, sos = true WHERE id = 18;
UPDATE device SET battery = true, sos = true WHERE id = 19;
UPDATE device SET battery = true, sos = true WHERE id = 20;
UPDATE device SET battery = true, sos = true WHERE id = 21;
UPDATE device SET battery = true, sos = true WHERE id = 22;
UPDATE device SET battery = true, sos = true WHERE id = 23;

-- [2015-05-06] INSERT NEW ROLES
INSERT INTO roles (id, name) VALUES (9, 'frequently-question');
INSERT INTO roles (id, name) VALUES (10, 'help');
INSERT INTO roles (id, name) VALUES (11, 'contact');

--             INSERT NEW GROUP ROLES
INSERT INTO group_role (group_id, role_id) SELECT id, 9 FROM groups WHERE name IN ('Administrador', 'Usuario');
INSERT INTO group_role (group_id, role_id) SELECT id, 10 FROM groups WHERE name IN ('Administrador', 'Usuario');
INSERT INTO group_role (group_id, role_id) SELECT id, 11 FROM groups WHERE name IN ('Administrador', 'Usuario');


--              INSERT NEW CONFIGURATION
/*
Add in table configuration in this fields:
default.admin.group.roles [1,2,3,4,5,6,9,10,11]
default.user.group.roles  [1,2,3,9,10,11]
*/
INSERT INTO configurations (description, key_, value)VALUES ('this is configuration from for support, that',
                                                                    'smtp.sender.address.support', 'support@buho.bo');
UPDATE configurations SET description = '', key_ = 'default.admin.group.roles', value = '1,2,3,4,5,6,9,10,11,13,14,15,16,17' WHERE id = 3;
INSERT INTO permissions (id, name) VALUES (15, 'add-device');
INSERT INTO permissions (id, name) VALUES (16, 'subscriptions-management');
INSERT INTO permissions (id, name) VALUES (17, 'shopping-cart');

-- insert new fields to account
ALTER TABLE account ADD line INT NULL;
ALTER TABLE account ADD document_number VARCHAR(10)NULL;
ALTER TABLE account ADD social_reason VARCHAR (40)NULL;
ALTER TABLE account ADD nit VARCHAR (20) NULL;

-- insert new configuration
INSERT INTO configurations (description, key_, value) VALUES ('Roles admin id', 'role.admin.id', '2;3');
INSERT INTO configurations (description, key_, value) VALUES ('Tracker Role id ', 'role.tracker.id', '5');
INSERT INTO configurations (description, key_, value) VALUES ('Role viewer id', 'role.user.id', '4');
INSERT INTO configurations (description, key_, value) VALUES ('Digit number random password for user subscription ', 'digit.number.password', '6');

-- remove unnecessary attributes on table SUBSCRIPTION
ALTER TABLE subscription DROP comment;
ALTER TABLE subscription DROP name_package;

-- add columns to subscription
ALTER TABLE subscription ADD invitation_mails VARCHAR(1000) NULL;
ALTER TABLE subscription ADD invitation_numbers VARCHAR(1000) NULL;
ALTER TABLE subscription ADD user_pay BIT NULL;
ALTER TABLE subscription ADD status VARCHAR(255) NULL;
ALTER TABLE subscription ADD user_id BIGINT NULL;
ALTER TABLE subscription ADD CONSTRAINT fk_subscription_user_id FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE subscription ADD shopping_cart BIT NULL;
UPDATE subscription SET user_pay = FALSE;

-- add new role (tracker)
INSERT INTO roles (id, name, visible, description) VALUES (5, 'Tracker', FALSE , 'user tracker');

ALTER TABLE users ADD change_password BIT NULL;
ALTER TABLE account ADD generate_password VARCHAR(255)NULL;
ALTER TABLE account ADD social_reason VARCHAR (40)NULL;
ALTER TABLE account ADD nit VARCHAR (20) NULL;



INSERT INTO role_permission (role_id, permission_id) VALUES (3, 15);
INSERT INTO role_permission (role_id, permission_id) VALUES (3, 16);
INSERT INTO role_permission (role_id, permission_id) VALUES (3, 17);



-- 2015-11-10
INSERT INTO configurations (description, key_, value) VALUES ('permissions for users without devices', 'default.permissions.without.device', 'add-device;contact');
INSERT INTO configurations (description, key_, value) VALUES ('Prefix phone numbers  TIGO', 'prefix.phone.numbers.tigo', '75;76;77;78;69');







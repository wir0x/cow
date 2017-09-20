

INSERT INTO buhodb.permissions (id, name) VALUES (19, 'devices-status');
INSERT INTO buhodb.role_permission (role_id, permission_id) VALUES (1, 19);
ALTER TABLE device_type ADD COLUMN drop_watch_alert BIT;
UPDATE device_type SET drop_watch_alert = FALSE ;
UPDATE device_type SET drop_watch_alert = TRUE WHERE id=6;


-- INSERT
-- date: 2016/06/15 13:44
-- by: gonzalo
-- description: configuration to generate tracker ID
INSERT INTO configurations (description, key_, value)
VALUES ('max number to generate tracker id', 'max.digit.tracker.id', 999999);



-- INSERT
-- date: 2016/06/16 10:44
-- by: gonzalo
-- description: configuration to generate tracker ID
INSERT INTO configurations (description, key_, value)
VALUES ('min number to generate tracker id', 'min.digit.tracker.id', 100000);


-- INSERT
-- Date: 2016/06/20 11:15
-- By: gonzalo
-- Description: Add new gps device type (wondex spt10 personal)
INSERT INTO device_type (id, alarm_battery, alarm_sos, battery, description, report_limit, drop_watch_alert)
VALUES (7, false, true, true, 'Wondex SPT10 - personal', 50, false);


-- INSERT
-- Date: 2016/06/20 17:30
-- By: gonzalo
-- Description: Add interval time for SOS when need validate position with current date
INSERT INTO configurations(description, key_, value)
VALUES ('Time interval for SOS (on minutes).', 'time.sos.interval', 5);


-- INSERT
-- Date: 2016/06/24 09:15
-- By: Gonzalo
-- Description: Add Admin user for ubidata
INSERT INTO users (email, name, password, phone_number, status, user_name, account_id, is_without_expiration, change_password, generated_password)
VALUES ('ubidata@ubidata.bo', 'Ubidata ', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', null, 'ENABLED', 'ubidata-admin', null, false, false, null);


-- INSERT
-- Date: 2016/06/24 09:15
-- By: Gonzalo
-- Description: Add Roles to UBIDATA user
INSERT INTO user_role (user_id, role_id) VALUES ((SELECT id FROM users WHERE user_name='ubidata-admin'), 1);


-- ALTER
-- Date: 2016/06/24 09:15
-- By: Gonzalo
-- Description: Add seller column to table
ALTER TABLE account ADD COLUMN seller VARCHAR (255);
UPDATE account SET seller = 'SWISSBYTES';


-- INSERT
-- Date: 2016/06/27 11:30
-- By: Gonzalo
-- Description: Add username seller (buho admin).
INSERT INTO configurations(description, key_, value)
VALUES ('User name buho admin', 'username.buho.sys.admin', 'buho-admin');

-- INSERT
-- Date: 2016/06/27 11:30
-- By: Gonzalo
-- Description: Add username seller (ubidata admin).
INSERT INTO configurations(description, key_, value)
VALUES ('User name ubidata admin', 'username.ubidata.sys.admin', 'ubidata-admin');





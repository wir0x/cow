-- DROP columns: start_date | end_date
ALTER TABLE fence
	DROP COLUMN start_date;
ALTER TABLE fence
	DROP COLUMN end_date;

-- add column battery
INSERT INTO buhodb.position_pointer (id, pointer, alert_type) VALUES (3,860771, 'BATTERY');
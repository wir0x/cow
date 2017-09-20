-- GL200 FOR +RESP:GTFRI, +BUFF:GTFRI, +RESP:GTGEO, +RESP:GTSOS, +RESP:GTPNL, +BUFF:GTSOS, +BUFF:GTGEO, +BUFF:GTPNL
SELECT device_id, battery, speed, extended_info,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',20),',',-1) new_battery,
  IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1),''),0) new_speed
FROM positions
WHERE extended_info LIKE '+%GL200%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+BUFF:GTFRI')
  AND device_id != 4
ORDER BY new_speed;

UPDATE positions
SET
  battery = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',20),',',-1),''),0),
  speed = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1),''),0)
WHERE extended_info LIKE '+%GL200%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTFRI', '+BUFF:GTFRI', '+RESP:GTGEO', '+RESP:GTSOS', '+RESP:GTPNL', '+BUFF:GTSOS', '+BUFF:GTGEO', '+BUFF:GTPNL')
      AND device_id != 4;

-- GL200 FOR +RESP:GTBPL, +BUFF:GTBPL
SELECT device_id, battery, speed, extended_info,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',17),',',-1) new_battery,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',7),',',-1) new_speed
FROM positions
WHERE extended_info LIKE '+%GL200%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTBPL', '+BUFF:GTBPL')
      AND device_id != 4
ORDER BY new_speed DESC;

UPDATE positions
SET
  battery = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',17),',',-1),''),0),
  speed = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',7),',',-1),''),0)
WHERE extended_info LIKE '+%GL200%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTBPL', '+BUFF:GTBPL')
      AND device_id != 4;

-- GV300 FOR +RESP:GTFRI, +RESP:GTGEO
SELECT device_id, battery, speed, extended_info,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',24),',',-1) new_battery,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1) new_speed
FROM positions
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTFRI')
#   AND device_id NOT IN (10,11,12)
ORDER BY speed DESC ;

UPDATE positions
SET
  battery = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',24),',',-1),''),0),
  speed = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1),''),0),
  valid = TRUE
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTFRI');

-- GV300 FOR +RESP:GTGSS, +RESP:GTERI
SELECT device_id, battery, speed, extended_info,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',24),',',-1) new_battery,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',10),',',-1) new_speed
FROM positions
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTGSS', '+RESP:GTERI')
#       AND device_id NOT IN (10,11,12)
ORDER BY speed;

UPDATE positions
SET
  battery = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',20),',',-1),''),0),
  speed = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',10),',',-1),''),0)
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTGSS', '+RESP:GTERI');

-- GV300 FOR +RESP:GTRTL, +RESP:GTEPS
SELECT device_id, battery, speed, extended_info,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',19),',',-1) new_battery,
  SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1) new_speed
FROM positions
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTRTL', '+RESP:GTEPS')
#       AND device_id NOT IN (10,11,12)
ORDER BY speed;

UPDATE positions
SET
  battery = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',19),',',-1),''),0),
  speed = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1),''),0)
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTRTL', '+RESP:GTEPS');

SELECT DISTINCT SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) new_battery
FROM positions
WHERE extended_info LIKE '+%GV300%';

-- UPDATE FOR FILTER VALID POSITIONS
UPDATE positions
SET
  valid = TRUE
WHERE extended_info LIKE '+%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTFRI', '+BUFF:GTFRI');

UPDATE positions
SET
  valid = FALSE
WHERE extended_info LIKE '+%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) NOT IN ('+RESP:GTFRI', '+BUFF:GTFRI');
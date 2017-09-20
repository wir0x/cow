-- create new table plots for saving all plots sent
CREATE TABLE plots
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    port INT,
    sent_date DATETIME,
    command LONGTEXT
);

UPDATE positions
SET
    battery = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',24),',',-1),''),0),
    speed = IFNULL(NULLIF(SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',9),',',-1),''),0)
WHERE extended_info LIKE '+%GV300%'
      AND SUBSTRING_INDEX(SUBSTRING_INDEX(extended_info,',',1),',',-1) IN ('+RESP:GTFRI');

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
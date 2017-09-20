  ------------------------------------------
-- This script is for fix when server is:
-- Down or collapse
------------------------------------------

-- Update status on notification for not send notification to clients.
UPDATE notifications SET status = 'PROCESSED' WHERE status='PENDING';

-- Update position pointer for not collapse server and consume sinews on server

-- Update pointer FENCE (critical).
UPDATE position_pointer SET pointer = (SELECT MAX(id) FROM positions) WHERE alert_type = 'FENCE';

-- Update pointer SOS.
UPDATE position_pointer SET pointer = (SELECT MAX(id) FROM positions WHERE extended_info LIKE '%GTSOS%') WHERE alert_type = 'SOS';

-- Update pointer LOW BATTERY.
UPDATE position_pointer SET pointer = (SELECT MAX(id) FROM positions WHERE extended_info LIKE '%GTMPN%' OR extended_info LIKE '%GTMPF%') WHERE alert_type = 'LOW_BATTERY';

-- Update pointer CONNECT OR DISCONNECT BATTERY.
UPDATE position_pointer SET pointer = (SELECT MAX(id) FROM positions WHERE extended_info LIKE '%SOS%') WHERE alert_type = 'BATTERY';



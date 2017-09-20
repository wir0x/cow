-- CREATE NEW TABLE CONFIGURATIONS
CREATE TABLE configurations
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    key_ VARCHAR(255),
    value VARCHAR(255)
);
CREATE UNIQUE INDEX idx_configuration_key ON configurations (key_);

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
INSERT INTO configurations (key_, value, description) VALUES ('sms.sender', '33575054', '');
INSERT INTO configurations (key_, value, description) VALUES ('port.gv300', '5002', '');

-- ADD NEW COLUMN TYPE TO DEVICE TABLE
ALTER TABLE device
ADD type VARCHAR (255);
CREATE TABLE company
(
  id BIGINT PRIMARY KEY NOT NULL,
  address VARCHAR(50) NOT NULL,
  devices TINYBLOB,
  name VARCHAR(50) NOT NULL,
  nit VARCHAR(50) NOT NULL,
  telf VARCHAR(50) NOT NULL,
  status INT NOT NULL,
  user TINYBLOB
);
CREATE TABLE device
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  imei VARCHAR(255),
  phone_number VARCHAR(255),
  unique_id VARCHAR(50) NOT NULL
);
CREATE TABLE positions
(
  id BIGINT PRIMARY KEY NOT NULL,
  altitude DOUBLE,
  command VARCHAR(255),
  course DOUBLE,
  device_id BIGINT,
  extended_info LONGTEXT NOT NULL,
  latitude DOUBLE,
  longitude DOUBLE,
  speed DOUBLE,
  time DATETIME,
  valid BIT
);
CREATE TABLE role
(
  id INT PRIMARY KEY NOT NULL,
  name VARCHAR(50) NOT NULL
);
CREATE TABLE users
(
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  email VARCHAR(50) NOT NULL,
  name VARCHAR(50),
  password VARCHAR(100) NOT NULL,
  status INT NOT NULL,
  user_name VARCHAR(50) NOT NULL
);
INSERT INTO users(id, email, name, password, status, user_name)
VALUES(1, 'admin@admin.com', 'admin', 'admin', 0, 'admin')


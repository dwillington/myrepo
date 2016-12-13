CREATE DATABASE hybrisdbuser;
CREATE USER 'hybrisdbuser'@'%' IDENTIFIED BY 'jasp3r91';
GRANT ALL PRIVILEGES ON *.* TO 'hybrisdbuser'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
SHOW VARIABLES LIKE 'innodb_flush_log_at_trx_commit';
SET GLOBAL innodb_flush_log_at_trx_commit  = 0;

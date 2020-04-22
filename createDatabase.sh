mysql -u root -p -e "DROP DATABASE IF EXISTS bd2_grupo8;
                  CREATE DATABASE bd2_grupo8;
                  CREATE USER IF NOT EXISTS 'grupo8'@'localhost' IDENTIFIED BY 'grupo8';
                  GRANT ALL PRIVILEGES ON bd2_grupo8.* TO 'grupo8'@'localhost';
                  FLUSH PRIVILEGES;
                  "

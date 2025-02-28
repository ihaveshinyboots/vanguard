# Setup

1. Download MySql version 9.2.0 from https://dev.mysql.com/downloads/mysql/
2. Enter 'password' for the root user
3. Run the following scripts
   ```
   CREATE TABLE sys.game_sales (
   id MEDIUMINT NOT NULL,
   game_no TINYINT NOT NULL,
   game_name VARCHAR(20) NOT NULL,
   game_code VARCHAR(5) NOT NULL,
   type TINYINT NOT NULL,
   cost_price DECIMAL(10,2) NOT NULL,
   tax DECIMAL(3,2) NOT NULL,
   sale_price DECIMAL(10,2) NOT NULL,
   date_of_sale DATETIME NOT NULL,
   PRIMARY KEY (id));
   
   CREATE INDEX idx_date_of_sale ON sys.game_sales (date_of_sale);
   CREATE INDEX idx_sale_price ON sys.game_sales (sale_price);
   
   CREATE TABLE sys.sales_summary (
   id INT PRIMARY KEY AUTO_INCREMENT,
   period_start DATE NOT NULL,
   period_end DATE NOT NULL,
   total_games_sold INT DEFAULT 0,
   total_sales DECIMAL(10, 2) DEFAULT 0.00,
   game_no TINYINT DEFAULT NULL,
   total_sales_for_game DECIMAL(10, 2) DEFAULT 0.00
   );
   
   CREATE TABLE sys.import_record (
   id INT PRIMARY KEY AUTO_INCREMENT,
   file_name VARCHAR(255) NOT NULL,
   status VARCHAR(10) NOT NULL
   );
   
   SET GLOBAL local_infile = 1;
4. Download IntelliJ Community Edition https://www.jetbrains.com/idea/download/?section=mac
5. Ensure that the project structure SDK level is Java 21
6. Run TestApplication
7. Download Postman https://www.postman.com/downloads/
8. Import postman collection (postman/vanguard.postman_collection.json)
9. For the post /import api, select the file from src/test/games_sales.csv or generate a new one with the generate_file.py

# Limitations:

1. The response time is slow might be due to a slow laptop
2. Running second /import api does not update the db values

-- golang_dbという名前のデータベースを作成
CREATE DATABASE hkk_project;
-- golang_dbをアクティブ
use hkk_project;
CREATE USER 'HKK'
IDENTIFIED BY 'hkk_it_kaihatu';
GRANT ALL PRIVILEGES On * . * TO 'HKK';
-- usersテーブルを作成。名前とパスワード
CREATE TABLE test_table (
    id INT(11) AUTO_INCREMENT NOT NULL,
    name VARCHAR(64) NOT NULL,
    PRIMARY KEY (id)
);
-- test_tableにテーブルに２つレコードを追加
INSERT INTO test_table (name) VALUES ("YOSHII");
INSERT INTO test_table (name) VALUES ("OTA");
INSERT INTO test_table (name) VALUES ("SANO");

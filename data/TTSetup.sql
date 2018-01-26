/*  Setup file which creates the tables required for Transaction Tracker to 
    run on SQLite.
*/
-- Turning on foreign keys.
PRAGMA foreign_keys = ON;

-- Drop tables if they exist
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Users;

-- Holds all the user information
CREATE TABLE Users (
username            VARCHAR(30) PRIMARY KEY,
name                VARCHAR(50) NOT NULL,
balance_in_cents    INT,
password            VARCHAR(60) NOT NULL
);

-- Contains valid category names in this program
CREATE TABLE Categories (
catName VARCHAR(30) PRIMARY KEY
);

-- Holds all of the transactions for all users
DROP TABLE IF EXISTS Transactions;
CREATE TABLE Transactions (
description     VARCHAR(30)     NOT NULL,
price_in_cents  INT,
day             DATE,
memo            VARCHAR(20),
category        VARCHAR(30)     REFERENCES Categories(catName),
belongsTo       VARCHAR(30)     REFERENCES Users(username)
                                ON UPDATE CASCADE 
                                ON DELETE CASCADE,
PRIMARY KEY (description, price_in_cents, day, belongsTo)
);

-- Adds basic categories
INSERT INTO Categories VALUES ("Deposit");
INSERT INTO Categories VALUES ("Food");
INSERT INTO Categories VALUES ("Housing");
INSERT INTO Categories VALUES ("Transportation");
INSERT INTO Categories VALUES ("Misc.");
INSERT INTO Categories VALUES ("N/A");
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
user_cat		VARCHAR(60) PRIMARY KEY,
belongsTo 		VARCHAR(30) REFERENCES Users(username)
							ON UPDATE CASCADE
							ON DELETE CASCADE,
catName 		VARCHAR(30)
);

-- Holds all of the transactions for all users
CREATE TABLE Transactions (
description     VARCHAR(30)     NOT NULL,
price_in_cents  INT,
day             DATE,
memo            VARCHAR(20),
user_cat		VARCHAR(60) REFERENCES Categories(user_cat),
PRIMARY KEY (description, price_in_cents, day, user_cat)
);

-- Add basic categories.
--INSERT INTO Categories VALUES ("N/A");
--INSERT INTO Categories VALUES ("Deposit");
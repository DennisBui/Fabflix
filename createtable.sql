drop database if exists moviedb;
create database moviedb;
use moviedb;

CREATE TABLE movies (
	id varchar(10) not null,
	title varchar(100) not null,
	year integer  not null,
	director varchar(100) not null,
	primary key (id)
);

CREATE TABLe stars (
	id varchar(10) not null,
    name varchar(100) not null,
    birthYear integer,
    primary key (id)
);

CREATE TABLE stars_in_movies(
	starId varchar(10) not null,
    movieId varchar(10) not null,
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE genres (
	id integer not null AUTO_INCREMENT,
    name varchar(32) not null,
    primary key (id)
);

CREATE TABLE genres_in_movies (
	genreId integer not null,
    movieId varchar(10) not null,
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards (
	id varchar(20) not null,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    expiration date not null,
    primary key (id)
);

CREATE TABLE customers (
	id integer not null AUTO_INCREMENT,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    ccId varchar(20) not null,
    address varchar(200) not null,
    email varchar(50) not null,
    password varchar(20) not null,
    primary key (id),
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales (
	id integer not null AUTO_INCREMENT,
    customerId integer not null,
    movieId varchar(10) not null,
    saleDate date not null,
    primary key (id),
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE ratings (
	movieId varchar(10) not null,
    rating float not null,
    numVotes integer not null,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);
    



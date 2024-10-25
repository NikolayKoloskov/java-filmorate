CREATE TABLE IF NOT EXISTS users(
  id int generated by default as identity primary key,
  name varchar(255),
  login varchar(255) not null,
  birthday date not null,
  email varchar(255) not null
);

CREATE TABLE IF NOT EXISTS mpa (
  id int generated by default as identity primary key,
  name varchar(255) not NULL unique
);

CREATE TABLE IF NOT EXISTS films (
  id int generated by default as identity primary key,
  name varchar(255) not null,
  description varchar(255) not null,
  release_date date not null,
  duration int not null,
  mpa_rating int not NULL,
  rate int,
  FOREIGN KEY (mpa_rating) REFERENCES mpa (id)
);


CREATE TABLE IF NOT EXISTS friend_requests
(
  user_id int,
  friend_id int,
  foreign key (user_id) references users (id) on delete cascade,
  foreign key (friend_id) references users (id) on delete cascade,
  primary key (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS genres (
  id int generated by default as identity primary key,
  name varchar(255) not null unique
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id int,
  genre_id int,
  foreign key (film_id) references films (id) on delete cascade,
  foreign key (genre_id) references genres (id) on delete cascade,
  primary key (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
 film_id int,
 user_id int,
 foreign key (film_id) references films (id) on delete cascade,
 foreign key (user_id) references users (id) on delete CASCADE,
 primary key (film_id, user_id)
);



insert into mpa (name)
values ('G');
insert into mpa (name)
values ('PG');
insert into mpa (name)
values ('PG-13');
insert into mpa (name)
values ('R');
insert into mpa (name)
values ('NC-17');

insert into genres (name)
values ('Комедия');
insert into genres (name)
values ('Драма');
insert into genres (name)
values ('Мультфильм');
insert into genres (name)
values ('Триллер');
insert into genres (name)
values ('Документальный');
insert into genres (name)
values ('Боевик');

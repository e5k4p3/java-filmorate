DELETE FROM FILMS_GENRES;
DELETE FROM FILMS_LIKES;
DELETE FROM FILMS_MODEL;
DELETE FROM GENRE_DICTIONARY;
DELETE FROM MPA_DICTIONARY;
DELETE FROM USERS_FRIENDS;
DELETE FROM USERS_MODEL;

ALTER TABLE FILMS_GENRES ALTER COLUMN FILM_GENRE_ID RESTART WITH 1;
ALTER TABLE FILMS_LIKES ALTER COLUMN LIKE_ID RESTART WITH 1;
ALTER TABLE FILMS_MODEL ALTER COLUMN FILM_ID RESTART WITH 1;
ALTER TABLE GENRE_DICTIONARY ALTER COLUMN GENRE_ID RESTART WITH 1;
ALTER TABLE MPA_DICTIONARY ALTER COLUMN MPA_ID RESTART WITH 1;
ALTER TABLE USERS_FRIENDS ALTER COLUMN FRIENDS_ID RESTART WITH 1;
ALTER TABLE USERS_MODEL ALTER COLUMN USER_ID RESTART WITH 1;


INSERT INTO GENRE_DICTIONARY (GENRE_NAME) VALUES ('Комедия');
INSERT INTO GENRE_DICTIONARY (GENRE_NAME) VALUES ('Драма');
INSERT INTO GENRE_DICTIONARY (GENRE_NAME) VALUES ('Мультфильм');
INSERT INTO GENRE_DICTIONARY (GENRE_NAME) VALUES ('Триллер');
INSERT INTO GENRE_DICTIONARY (GENRE_NAME) VALUES ('Документальный');
INSERT INTO GENRE_DICTIONARY (GENRE_NAME) VALUES ('Боевик');
INSERT INTO MPA_DICTIONARY (RATING, RATING_DESCRIPTION) VALUES ('G', 'У фильма нет возрастных ограничений');
INSERT INTO MPA_DICTIONARY (RATING, RATING_DESCRIPTION) VALUES ('PG', 'Детям рекомендуется смотреть фильм с родителями');
INSERT INTO MPA_DICTIONARY (RATING, RATING_DESCRIPTION) VALUES ('PG-13', 'Детям до 13 лет просмотр не желателен');
INSERT INTO MPA_DICTIONARY (RATING, RATING_DESCRIPTION) VALUES ('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
INSERT INTO MPA_DICTIONARY (RATING, RATING_DESCRIPTION) VALUES ('NC-17', 'Лицам до 18 лет просмотр запрещён');

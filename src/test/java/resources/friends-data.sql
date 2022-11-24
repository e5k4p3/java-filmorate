DELETE FROM USERS_MODEL;
DELETE FROM USERS_FRIENDS;

ALTER TABLE USERS_MODEL ALTER COLUMN USER_ID RESTART WITH 1;
ALTER TABLE USERS_FRIENDS ALTER COLUMN FRIENDS_ID RESTART WITH 1;

INSERT INTO USERS_MODEL(email, login, name, birthday) VALUES ('e5k4p3@gmail.com', 'e5k4p3', 'e5k4p3', DATE '1995-07-11');
INSERT INTO USERS_MODEL(email, login, name, birthday) VALUES ('mulenas@gmail.com', 'Mulenas', 'Mulenas', DATE '1995-07-11');
INSERT INTO USERS_MODEL(email, login, name, birthday) VALUES ('thius@gmail.com', 'thius', 'thius', DATE '1995-07-11');
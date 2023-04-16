CREATE TABLE IF NOT EXISTS PUBLIC.FILMS (
                                            FILM_ID int NOT NULL AUTO_INCREMENT,
                                            NAME varchar(60) NOT NULL,
    DESCRIPRION varchar(255),
    RELEASEDATA date NOT NULL,
    DURATION int NOT NULL,
    RATING varchar(10) NOT NULL,
    CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID)
    );

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
                                            GENRE_ID int NOT NULL AUTO_INCREMENT,
                                            NAME varchar(50) NOT NULL,
    CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
    );

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE_FILM (
                                                 FILM_ID int NOT NULL,
                                                 GENRE_ID int,
                                                 CONSTRAINT GENRE_FILM_PK PRIMARY KEY (FILM_ID,GENRE_ID),
    CONSTRAINT GENRE_FILM_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID),
    CONSTRAINT GENRE_FILM_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRE(GENRE_ID)
    );

CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
                                            USER_ID int NOT NULL AUTO_INCREMENT,
                                            EMAIL varchar NOT NULL,
                                            LOGIN varchar NOT NULL,
                                            NAME varchar DEFAULT 'username',
                                            BIRTHDAY date NOT NULL,
                                            CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
    );

CREATE TABLE IF NOT EXISTS PUBLIC.LIKES (
                                            FILM_ID int NOT NULL,
                                            USER_ID int NOT NULL,
                                            CONSTRAINT LIKES_PK PRIMARY KEY (FILM_ID,USER_ID),
    CONSTRAINT LIKES_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID),
    CONSTRAINT LIKES_FK_1 FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID)
    );


CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDS (
                                              USERFIRST_ID int NOT NULL,
                                              USERSECOND_ID int NOT NULL,
                                              IS_CONFIRM boolean DEFAULT false NOT NULL,
                                              CONSTRAINT FRIENDS_PK PRIMARY KEY (USERFIRST_ID,USERSECOND_ID),
    CONSTRAINT USERFIRST_FK FOREIGN KEY (USERFIRST_ID) REFERENCES PUBLIC.USERS(USER_ID),
    CONSTRAINT USERSECOND_FK FOREIGN KEY (USERSECOND_ID) REFERENCES PUBLIC.USERS(USER_ID)
    );
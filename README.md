# java-filmorate
Проект Filmorate

![Блок схема](/diagram.png)

### films
```
Хранит в данные о фильмах
 
film_id (PK) – id фильма
name – наименование фильма
description -  описание фильма
releaseDate – дата релиза фильма
duration – продолжительность фильма
rating –  типа рейтинга: 
    G — у фильма нет возрастных ограничений;
    PG — детям рекомендуется смотреть фильм с родителями;
    PG-13— детям до 13 лет просмотр нежелателен;
    R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого;
    NC-17 — лицам до 18 лет просмотр запрещён.
```

### genre_film
```
Формирует таблицу отношений фильмов и жанров
    
film_id  (PK, FK)  – идентификатор фильма
genre_id (PK, FK) - идентификатор жанра
```

### genre
```
Хранит жанры фильмов:
    Комедия (comedy);
    Драма (drama);
    Мультфильм (cartoon);
    Триллер (thriller);
    Документальный (documentary);
    Боевик (action).
    
genre_id  (PK) – идентификатор жанра
name – наименование жанра
```

### users
```
Хранит в данные о пользователях

user_id  (PK) – идентификатор пользователя
email – электронная почта пользователя
login -  логин пользователя
name – имя пользователя
birthday – день рождения пользователя
```

### friends
```
Хранит в данные о дружбе пользователей

userFirst_id (PK, FK)  – идентификатор первого пользователя
userSecond_id (PK, FK)  – идентификатор второго пользователя
is_confirm – подтверждена ли дружба (true - да, false - нет)
```

### likes
```
Хранит в данные о лайках фильма

film_id  (PK, FK)  – идентификатор фильма
user_id  (PK, FK) – идентификатор пользователя
```

### Примеры построения запросов базы данных:
#### Список всех подтвержденных друзей пользователя с id=1
```
SELECT userFirst_id
FROM friends
WHERE userFirst_id = 1 
    AND is_confirm = true;
```
#### Список всех документальных фильмов 
```
SELECT name
FROM films AS f
WHERE film_id IN (
    SELECT gf.film_id
    FROM genre_film AS gf
    LEFT OUTER JOIN genre AS g ON gf.genre_id = g.genre_id
    WHERE g.name = "documentary"
);
```
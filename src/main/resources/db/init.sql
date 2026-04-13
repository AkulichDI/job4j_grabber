CREATE TABLE IF NOT EXISTS posts (
                                     id SERIAL PRIMARY KEY,
                                     title TEXT,
                                     link  TEXT UNIQUE,
                                     description TEXT,
                                     created date

);
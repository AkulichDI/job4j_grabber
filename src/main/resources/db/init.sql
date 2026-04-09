CREATE TABLE IF NOT EXISTS posts (
    id SERIAL PRIMARY KEY,
    title TEXT,
    link TEXT,
    description TEXT,
    created TIMESTAMP

);
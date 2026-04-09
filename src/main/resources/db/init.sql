CREATE TABLE posts IF NOT EXISTS (
    id SERIAL PRIMARY KEY,
    title TEXT,
    link TEXT,
    description TEXT,
    created TIMESTAMP

)
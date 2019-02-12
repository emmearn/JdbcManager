--Setup database
DROP DATABASE IF EXISTS jdbc_db;
CREATE DATABASE jdbc_db;
\c jdbc_db;

DROP TABLE IF EXISTS public.generic_object;

CREATE TABLE IF NOT EXISTS public.generic_object (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(70),
    custom_code INTEGER,
    date TIMESTAMP WITHOUT TIME ZONE,
    enabled BOOLEAN DEFAULT TRUE
);

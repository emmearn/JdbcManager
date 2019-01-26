# JdbcManager
In order to perform this project they are necessary:
- Postgresql (definitely working with version 9.5);
- Jetty (definitely working with version 9.3.14).

#### Postgresql
It is also necessary a DB.
Execute the commands:
- `sudo -i -u postgres`;
- `createuser -P -s -e user_example`;
- choose and enter a password.
- `createdb -O user_example db_name_example`;

After that, create a simple table:
`CREATE TABLE generic_object (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(70),
    custom_code INTEGER,
    date TIMESTAMP WITHOUT TIME ZONE,
    enabled BOOLEAN
);`

#### Execution
- run the comand `mvn clean install` in the main project folder;
- Enter into the folder `web` and run the comand: `mvn jetty:run`;

#### Usage
If there isn't any error, the backend will be avaible at the address `localhost:8080`.
In the body of requests that require a json, fields likes "id" or "enable" are illegal because they are part of the backend logic.
The services are:
- `GET ../genericobject` for listing all the objects generated;
- `GET ../genericobject/{id}` for a specific object;
- `POST ../genericobject` for creation;
- `PUT ../genericobject` for a full update;
- `PATCH ../genericobject` for a partial update;
- `DELETE ../genericobject/{id}` for to cancel a specific object;
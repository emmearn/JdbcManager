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
    name VARCHAR(70)
);`

#### Execution
- run the comand `mvn clean install` in the main project folder;
- Enter into the folder `web` and run the comand: `mvn jetty:run`;
- if there isn't any error, the backend will be avaible at the address `localhost:8080`.
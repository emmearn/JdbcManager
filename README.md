# JdbcManager
JdbcManager is a singleton object who provide methods for to interface with a DB by "JdbcTemplate".
This one is provides by org.springframework.jdbc.core package, but, for to use it, is necessary write query
like "SELECT * FROM table WHERE condition ORDER BY field ASC".
JdbcManager's methods create this query for you using "Generics".
In the project is already present an object named "GenericObject" in the models module.
The GenericObject model has more demostration fields: name(String), customCode(Int), date(LocalDateTime), enabled(Boolean).
The end point for GenericObject use a service layer that use a repo layer that use the JdbcManager methods.
That's it.
Obiviously, it isn't a ORM. It is only an exercise with the Generics and Kotlin.

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
    enabled BOOLEAN DEFAULT TRUE
);`

#### Execution
- run the comand `mvn clean install` in the main project folder;
- Enter into the folder `web` and run the comand: `mvn jetty:run`;

#### Usage
If there isn't any error, the backend will be avaible at the address `localhost:8080`.
In the body of requests that require a json, fields likes "id" or "enable" are illegal because they are part of the backend logic.
The services are:
- `GET ../genericobject` for listing all the objects generated;
- `GET ../genericobject?orderBy=id&orderType=ASC&enabled=true` in optional is also available, or just one of these filters;
- `GET ../genericobject/{id}` for a specific object;
- `POST ../genericobject` with the body like
    {
        "name": "a name",
        "customCode": 42,
        "date": "2019-01-27T15:50"
    }
    for creation;
- `PUT ../genericobject` with the body like
    {
        "id": 1,
        "name": "name changed",
        "customCode": 24,
        "date": "2019-01-27T15:50"
    }
    for a full update. Notice that if the backend misses a field or the id return an error;
- `PATCH ../genericobject` with the body like
    {
        "id": 1,
        "name": "name changed partially"
    }
    for a partial update;
- `DELETE ../genericobject/{id}` for to cancel a specific object;

Every return is wrapped in an object like
{
    "result": true, // or false if there is an error. In that case "message" field contains a message
    "message": null
}
returned after a request like post creation or editing an existing object or like
{
    "result": [
        {
            "id": 1,
            "name": "name",
            "customCode": 42,
            "date": [2019, 1, 26, 17, 18],
            "enabled": true
        },
        {
            "id": 2,
            "name": "another name",
            "customCode": 34,
            "date": [2019, 1, 26, 17, 18],
            "enabled": true
        }
    ],
    "message": null
}
returned after a get request.

### Customizations
If you notice `GET ../genericobject/{id}` is different by `GET ../genericobject` only for a parameter.
Indeed they use the same repo methods. The first one only have `conditionsList.add(Triple("id", "=", id))` more than the second one.
If you want, you can use this conditionsList as you wish.
For example, you can add a filter:
`conditionsList.add(Triple("enabled", "=", true))`
or a specific condition:
`conditionsList.add(Triple("customCode", ">", 10))`

### Possible developments
GenericObject is only for demostration.
You can expand this data model and create custom object defined into models module.
After that, is also necessary create end point, service, and repo layer.
But JdbcManager works with any model.
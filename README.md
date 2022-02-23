Contributors:
Joshua Herrera and Tanner Nielsen


Java-Based ORM for a postgres Connection via JDBC

Features:
Maps a generic Object to a given Database Connection by utilizing Reflection to parse out Column and table information
Handles CRUD operations by dynmaically creating SQL commands based on Inputs
Returns SQL query results after rebuilding object(s) by extracting constructors and populating object fields with query results

XML
Project is exported as Maven package before being imported into a web server

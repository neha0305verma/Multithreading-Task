# network-data-importer

This project is used to import data from an excel sheet to the database Neo4j.

Steps to run the project :-

1. OPEN network-data-importer/src/main/resources/app.properties file.
2. UPDATE the database details(username, password and neo4jUrl) in the file and SAVE it.
3. Then open a terminal within the network-data-importer folder and run the below commands:
--> a. mvn clean compile
--> b. mvn exec:java
  
The data of the excel sheet from the location "network-data-importer/src/main/resources/demoData.xlsx" will be impoerted in the database Neo4j.

## Setup

## Required Software

* Intellij Idea or Eclipse
* A [Neo4j](https://neo4j.com) Database
* A [Redis](https://redis.io/download) Database

## Other Requirments

* A set of songs

## Instructions
1. Create a songs directory inside of the AudioStreaming folder and put your songs that you want to stream inside. It does not matter whether you organize them by album or artist, the program will find them.
2. Download and start the neo4j database. This is where we will store song information, user information, and which users like which songs.
3. Once started, go to http://localhost:7474/browser/ and change your username to `neo4j` and the password to `database`
4. Downlaod and start the redis database. This is where we will store queues of songs, which is attached to a username.
5. Now open up the AudioStreaming project in Eclipse or Intellij. It is prefered that you use Intellij since that is what itwas originally developed in.
6. Make sure the dependencies specified in the pom.xml file are downloaded
7. Run the [population script](./AudioStreaming/src/main/java/population/PopulateSongs.java). It is a separate java application inside of the AudioStreaming application. This will take the songs stored in the songs directory and put them into the Neo4j database.
8. Run the Spring application (the web server) by running the following command in the terminal
```
./mvnw spring-boot:run
```
9. The application should now be running on port 8080.
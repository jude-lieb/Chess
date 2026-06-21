How to get it running:
This project needs Java and Maven installed to function.
To start the backend, run "mvn clean install" and "mvn spring-boot:run" from the backend folder.
To start the frontend, run "npm i" and "npm run dev" from the frontend folder.
Navigate to localhost:5173 in your web browser to use the app.

Developer: Jude Lieb

Start date: 06/2023

This has a bare-bones move response system that chooses a legal move to play based on material totals at a low depth. 
Websockets are used to connect the Java server to the clients. 
Multiplayer mode and Computer mode are both available.

How to get it running:
This project needs Java and Maven installed to function.
To start the backend, run "mvn clean install" and "mvn exec:java" from the backend folder.
To start the frontend, run "npm i" and "npm start" (may need sudo) from the frontend folder.
Navigate to localhost in your web browser to use the app.

Developer: Jude Lieb

Start date: 06/2023

This has a bare-bones move response system that chooses a legal move to play based on material totals at a low depth. 
Websockets are used to connect the java server to the node app and the node app to the client.
A Cloudflare tunnel is used to secure my local server machine.





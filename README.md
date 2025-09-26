How to get it running:
Download and install java version >=17 and add it to your environment variables path.
Download and install maven and add it to your environment variables path.
To start the backend, run "mvn clean install" and "mvn exec:java".
Copy the .env_example file into a .env file in the frontend folder.
To start the frontend, run "npm i" and "npm start" from the frontend folder.
Navigate to the localhost address in your web browser.

Developer: Jude Lieb

Start date: 06/2023

This has a bare-bones move response system is running that chooses a legal move to play based on material totals at a low depth. 
Websockets are used to connect the java server to the node app and from the node app to the client.
A Cloudflare tunnel is used to secure my local server machine.



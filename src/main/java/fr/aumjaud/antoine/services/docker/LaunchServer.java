package fr.aumjaud.antoine.services.docker;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

public class LaunchServer {
	
    public static void main(String... args) {
        port(9080); 

        get("/hi", (request, response) -> "hello");
        
        DockerResource dockerResource = new DockerResource();
        post("/webhook", "application/json", dockerResource::webhook);
    }
}

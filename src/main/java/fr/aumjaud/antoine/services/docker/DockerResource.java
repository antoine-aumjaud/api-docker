package fr.aumjaud.antoine.services.synology.chatbot;

import spark.Request;
import spark.Response;

public class DockerResource {

    public String webhook(Request request, Response response) {
        
        curl -X GET http://192.168.0.6:2375/v1.19/containers/json

        return "ok";
    }
    
    
}

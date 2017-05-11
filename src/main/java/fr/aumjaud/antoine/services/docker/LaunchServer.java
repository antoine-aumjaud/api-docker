package fr.aumjaud.antoine.services.docker;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.port;

public class LaunchServer {

	private static Logger logger = LoggerFactory.getLogger(DockerResource.class);
	private static String CONFIG_FILENAME = "api-docker.properties";

	public static void main(String... args) {
		Properties properties = loadProperties();
		DockerResource dockerResource = new DockerResource(properties);

		port(9080);
		get("/hi", (request, response) -> "hello");

		post("/", "application/json", dockerResource::webhook);
	}

	private static Properties loadProperties() {
		try (InputStream is = LaunchServer.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME)) {
			if (is == null)
				throw new IllegalStateException("No config file in classpath: " + CONFIG_FILENAME);
			Properties p = new Properties();
			p.load(is);
			return p;
		} catch (IOException e) {
			logger.error("Can't load properties", e);
			return null;
		}
	}
}

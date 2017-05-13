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

	private static Logger logger = LoggerFactory.getLogger(LaunchServer.class);

	private static String COMMON_CONFIG_FILENAME = "common.properties";
	private static String APP_CONFIG_FILENAME = "api-docker.properties";

	public static void main(String... args) {
		TechnicalResource commonResource = new TechnicalResource(loadProperties(COMMON_CONFIG_FILENAME));
		DockerResource dockerResource = new DockerResource(loadProperties(APP_CONFIG_FILENAME));

		port(9080);

		get("/hi", commonResource::hi);
		get("/info", commonResource::info);

		post("/", "application/json", dockerResource::webhook);
	}

	private static Properties loadProperties(String configFileName) {
		try (InputStream is = LaunchServer.class.getClassLoader().getResourceAsStream(configFileName)) {
			if (is == null) {
				throw new IllegalStateException("No config file in classpath: " + configFileName);
			}
			Properties p = new Properties();
			p.load(is);
			logger.info(configFileName + " loaded");
			return p;
		} catch (IOException e) {
			logger.error("Can't load properties", e);
			return null;
		}
	}
}

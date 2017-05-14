package fr.aumjaud.antoine.services.docker;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import fr.aumjaud.antoine.services.common.PropertyHelper;
import fr.aumjaud.antoine.services.common.TechnicalResource;

public class LaunchServer {

	//private static Logger logger = LoggerFactory.getLogger(LaunchServer.class);

	private static String COMMON_CONFIG_FILENAME = "common.properties";
	private static String APP_CONFIG_FILENAME = "api-docker.properties";

	public static void main(String... args) {
		PropertyHelper propertyHelper = new PropertyHelper();
		TechnicalResource technicalResource = new TechnicalResource(propertyHelper.loadProperties(COMMON_CONFIG_FILENAME));
		DockerResource dockerResource = new DockerResource(propertyHelper.loadProperties(APP_CONFIG_FILENAME));

		port(9080);

		get("/hi", (request, response) -> technicalResource.hi());
		get("/info", (request, response) -> technicalResource.info());

		post("/", "application/json", dockerResource::webhook);
	}

}

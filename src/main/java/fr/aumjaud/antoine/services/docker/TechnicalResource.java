package fr.aumjaud.antoine.services.docker;

import java.util.Properties;

import spark.Request;
import spark.Response;

public class TechnicalResource {
	//private static Logger logger = LoggerFactory.getLogger(TechnicalResource.class);

	private Properties commonProperties;

	public TechnicalResource(Properties commonProperties) {
		this.commonProperties = commonProperties;
	}

	public String hi(Request request, Response response) {
		return "hello";
	}

	public String info(Request request, Response response) {
		return String.format("{\"name\": \"%s\", \"version\":\"%s\"}", //
				commonProperties.getProperty("application_name"), //
				commonProperties.getProperty("version"));
	}
}

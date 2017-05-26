package fr.aumjaud.antoine.services.docker.requesthandler;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.aumjaud.antoine.services.common.security.NoAccessException;
import fr.aumjaud.antoine.services.common.security.WrongRequestException;
import fr.aumjaud.antoine.services.docker.model.DockerPayload;
import fr.aumjaud.antoine.services.docker.model.DockerRepository;
import fr.aumjaud.antoine.services.docker.service.DockerService;
import spark.Request;
import spark.Response;

public class DockerResource {
	private static final Logger logger = LoggerFactory.getLogger(DockerResource.class);

	private DockerService dockerService = new DockerService();
	private Properties properties;
	private Gson gson;

	public DockerResource() {
		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gson = builder.create();
	}

	/**
	 * Set config 
	 * @param properties the config to set
	 */
	public void setConfig(Properties properties) {
		this.properties = properties;
		dockerService.setConfig(properties);
	}

	/**
	 * Manage webhook from DockerHub
	 */
	public String webhook(Request request, Response response) {
		// Check post data
		DockerPayload dockerPayload = getDockerPayload(request);
		if (dockerPayload == null) {
			throw new WrongRequestException("incorrect POST data", "Try to access to deployment with no parsable POST data");
		}

		DockerRepository dockerRepository = dockerPayload.getRepository();
		String imageId = dockerRepository.getRepoName();
		String containerId = dockerRepository.getName();

		// Check image owner
		if (!imageId.startsWith(properties.getProperty("docker-owner"))) {
			throw new NoAccessException("incorrect acess", "Try to access to deployment with not valid image: " + imageId);
		}

		// Process execution
		try {
			dockerService.executeWebHook(imageId, containerId);
			return "ok";

		} catch (RuntimeException | IOException e) {
			logger.error(e.getMessage(), e);
			response.status(500);
			return "error, see logs";
		}
	}

	/**
	 * Get Docker Payload form request
	 * @param request
	 * @return
	 */
	DockerPayload getDockerPayload(Request request) {
		return gson.fromJson(request.body(), DockerPayload.class);
	}

}

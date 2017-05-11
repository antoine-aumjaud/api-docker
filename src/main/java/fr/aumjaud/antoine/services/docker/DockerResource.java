package fr.aumjaud.antoine.services.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.aumjaud.antoine.services.docker.model.DockerPushData;
import fr.aumjaud.antoine.services.docker.model.DockerRepository;
import spark.Request;
import spark.Response;

public class DockerResource {
	private static Logger logger = LoggerFactory.getLogger(DockerResource.class);

	private Properties properties;
	private Gson gson;

	public DockerResource(Properties properties) {
		this.properties = properties;

		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

		gson = builder.create();
	}

	/**
	 * Manage webhook from dockerhub
	 */
	public String webhook(Request request, Response response) {

		try {
			DockerRepository dockerRepository = getData(request).getRepository();
			String imageId = dockerRepository.getRepoName();
			String containerId = dockerRepository.getName();

			execute(getCommand("pull", imageId, containerId));
			execute(getCommand("stop", imageId, containerId));
			execute(getCommand("remove", imageId, containerId));
			execute(getCommand("start", imageId, containerId));

			return "ok";

		} catch (RuntimeException | IOException e) {
			logger.error(e.getMessage(), e);
			return "error";
		}
	}

	DockerPushData getData(Request request) {
		return gson.fromJson(request.body(), DockerPushData.class);
	}

	/**
	 * Get command from config file, search for containerId command, if not found,
	 * takes the common command.
	 * @param commandType the type to the command to search in properties
	 * @param imageId the command parameter ${imageId} to filter
	 * @param containerId the command parameter ${imageId} to filter
	 * @return the filtered command from the config file
	 */
	String getCommand(String commandType, String imageId, String containerId) {
		String command = properties.getProperty(containerId + ".command." + commandType);
		if (command == null) {
			command = properties.getProperty("common.command." + commandType);
		}
		if (command == null) {
			throw new RuntimeException("Command " + commandType + "not found");
		}
		return command.replaceAll("\\$\\{imageId\\}", imageId).replaceAll("\\$\\{containerId\\}", containerId);
	}

	/**
	 * Execute a command
	 * @param command the command to execute
	 * @throws IOException if there is an error during execution
	 */
	private void execute(String command) throws IOException {
		Process p = new ProcessBuilder().command(command).redirectErrorStream(true).start();

		try (BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
			logger.debug(String.join(System.lineSeparator(), outReader.lines().collect(Collectors.toList())));
			p.wait(Integer.parseInt(properties.getProperty("process.timeout", "30")));

		} catch (IOException | InterruptedException e) {
			throw new IOException("Can't execute process");
		}
	}

}

package fr.aumjaud.antoine.services.docker.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aumjaud.antoine.services.common.http.HttpCode;
import fr.aumjaud.antoine.services.common.http.HttpHelper;
import fr.aumjaud.antoine.services.common.http.HttpResponse;

public class DockerService {

	private static final Logger logger = LoggerFactory.getLogger(DockerService.class);

	private HttpHelper httpHelper = new HttpHelper();

	private Properties properties;

	/**
	 * Set config 
	 * @param properties the config to set
	 */
	public void setConfig(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Manage webhook from DockerHub
	 * @throws IOException if exception occures during execution
	 */
	public void executeWebHook(String imageId, String containerId) throws IOException {
		try {
			execute(getCommand("stop", imageId, containerId));
			execute(getCommand("rm", imageId, containerId));
			execute(getCommand("rmi", imageId, containerId));
			execute(getCommand("pull", imageId, containerId));
			execute(getCommand("start", imageId, containerId));
			sendMessageToChatBot(containerId, "successful");
		} catch (IOException e) {
			sendMessageToChatBot(containerId, "failed");
			throw e;
		}
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
			throw new RuntimeException("Command '" + commandType + "' not found for " + containerId);
		}
		return command.replaceAll("\\$\\{imageId\\}", imageId).replaceAll("\\$\\{containerId\\}", containerId);
	}

	/**
	 * Execute a command
	 * @param command the command to execute
	 * @throws IOException if there is an error during execution
	 */
	boolean execute(String command) throws IOException {
		logger.info("Execute command: {}", command);
		boolean exitValue = false;
		Process p = new ProcessBuilder().command(command.split(" ")).redirectErrorStream(true).start();

		try (BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
			logger.debug(String.join(System.lineSeparator(), outReader.lines().collect(Collectors.toList())));
			exitValue = p.waitFor(Long.parseLong(properties.getProperty("process.timeout", "30")), TimeUnit.SECONDS);
			logger.info("Command executed, result: {}", exitValue);
			return exitValue;
		} catch (IOException | InterruptedException e) {
			throw new IOException("Can't execute process");
		}
	}

	private void sendMessageToChatBot(String containerId, String status) {
		HttpResponse httpResponse = httpHelper.postData( //
				properties.getProperty("api-synology-chatbot.url"), //
				"Deploy " + status + " of " + containerId, //
				properties.getProperty("api-synology-chatbot.secure-key"));
		if (httpResponse == null || httpResponse.getHttpCode() != HttpCode.OK) {
			logger.warn("Can't get response form chat-bot API");
		}
	}
}

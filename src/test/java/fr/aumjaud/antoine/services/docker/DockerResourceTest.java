
package fr.aumjaud.antoine.services.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.aumjaud.antoine.services.docker.model.DockerPayload;

public class DockerResourceTest {

	private Properties properties;
	private DockerResource dockerResource = new DockerResource();

	@Before
	public void init() {
		properties = new Properties();
		dockerResource.setConfig(properties);
	}

	@Test
	public void getData_should_parse_an_docker_webhook_info() throws IOException, URISyntaxException {
		// Given
		String msg = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("docker_webhook.json").toURI())));
		spark.Request request = new spark.Request() {
			public String body() {
				return msg;
			}
		};

		// When
		DockerPayload dp = dockerResource.getData(request);

		// Then
		assertNotNull(dp.getRepository());
		assertEquals("testhook", dp.getRepository().getName());
		assertEquals("svendowideit/testhook", dp.getRepository().getRepoName());
	}

	@Test
	public void getCommand_should_get_container_command_if_exists() {
		// Given
		properties.put("containerId.command.mycommand", "OK");
		properties.put("common.command.mycommand", "KO");

		// When
		String command = dockerResource.getCommand("mycommand", null, "containerId");

		// Then
		assertEquals("OK", command);
	}

	@Test
	public void getCommand_should_get_common_command_if_container_command_does_not_exist() {
		// Given
		properties.put("common.command.mycommand", "OK");

		// When
		String command = dockerResource.getCommand("mycommand", null, "containerId");

		// Then
		assertEquals("OK", command);
	}

	@Test
	public void getCommand_should_filter_the_command_with_parameters() {
		// Given
		properties.put("common.command.mycommand", "${imageId}-${containerId}");

		// When
		String command = dockerResource.getCommand("mycommand", "aa", "bb");

		// Then
		assertEquals("aa-bb", command);
	}

	@Test
	public void execute_should_execute_a_command() throws IOException {
		// Given
		String command = "docker ps -a";

		// When
		boolean res = dockerResource.execute(command);

		// Then
		assertTrue(res);
	}

}

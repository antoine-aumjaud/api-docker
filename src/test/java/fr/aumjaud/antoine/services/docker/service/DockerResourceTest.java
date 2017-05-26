
package fr.aumjaud.antoine.services.docker.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class DockerResourceTest {

	private Properties properties;
	private DockerService dockerService = new DockerService();

	@Before
	public void init() {
		properties = new Properties();
		dockerService.setConfig(properties);
	}

	@Test
	public void getCommand_should_get_container_command_if_exists() {
		// Given
		properties.put("containerId.command.mycommand", "OK");
		properties.put("common.command.mycommand", "KO");

		// When
		String command = dockerService.getCommand("mycommand", null, "containerId");

		// Then
		assertEquals("OK", command);
	}

	@Test
	public void getCommand_should_get_common_command_if_container_command_does_not_exist() {
		// Given
		properties.put("common.command.mycommand", "OK");

		// When
		String command = dockerService.getCommand("mycommand", null, "containerId");

		// Then
		assertEquals("OK", command);
	}

	@Test
	public void getCommand_should_filter_the_command_with_parameters() {
		// Given
		properties.put("common.command.mycommand", "${imageId}-${containerId}");

		// When
		String command = dockerService.getCommand("mycommand", "aa", "bb");

		// Then
		assertEquals("aa-bb", command);
	}

	@Test
	public void execute_should_execute_a_command() throws IOException {
		// Given
		String command = "docker ps -a";

		// When
		boolean res = dockerService.execute(command);

		// Then
		assertTrue(res);
	}

}

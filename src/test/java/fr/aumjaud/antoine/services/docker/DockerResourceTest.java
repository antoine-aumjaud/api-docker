
package fr.aumjaud.antoine.services.docker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.aumjaud.antoine.services.docker.model.DockerPushData;

public class DockerResourceTest {

	private Properties properties;
	private DockerResource dockerResource = new DockerResource();

	@Before
	public void init() {
		properties = new Properties();
		dockerResource.setConfig(properties);
	}

	@Test
	public void getData_should_parse_an_docker_webhook_info() {
		// Given
		String msg = "{  \"callback_url\": \"https://registry.hub.docker.com/u/svendowideit/testhook/hook/2141b5bi5i5b02bec211i4eeih0242eg11000a/\",  \"push_data\": {    \"images\": [        \"27d47432a69bca5f2700e4dff7de0388ed65f9d3fb1ec645e2bc24c223dc1cc3\",        \"51a9c7c1f8bb2fa19bcd09789a34e63f35abb80044bc10196e304f6634cc582c\",        \"...\"    ],    \"pushed_at\": 1.417566161e+09,    \"pusher\": \"trustedbuilder\"  },  \"repository\": {    \"comment_count\": \"0\",    \"date_created\": 1.417494799e+09,    \"description\": \"\",    \"dockerfile\": \"\",    \"full_description\": \"Docker Hub based automated build from a GitHub repo\",    \"is_official\": false,    \"is_private\": true,    \"is_trusted\": true,    \"name\": \"testhook\",    \"namespace\": \"svendowideit\",    \"owner\": \"svendowideit\",    \"repo_name\": \"svendowideit/testhook\",    \"repo_url\": \"https://registry.hub.docker.com/u/svendowideit/testhook/\",    \"star_count\": 0,    \"status\": \"Active\"  }}";
		spark.Request request = new spark.Request() {
			public String body() {
				return msg;
			}
		};

		// When
		DockerPushData dpd = dockerResource.getData(request);

		// Then
		assertNotNull(dpd.getRepository());
		assertEquals("testhook", dpd.getRepository().getName());
		assertEquals("svendowideit/testhook", dpd.getRepository().getRepoName());
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

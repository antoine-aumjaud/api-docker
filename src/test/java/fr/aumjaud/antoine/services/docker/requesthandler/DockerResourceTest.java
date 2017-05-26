
package fr.aumjaud.antoine.services.docker.requesthandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.aumjaud.antoine.services.docker.model.DockerPayload;
import fr.aumjaud.antoine.services.docker.requesthandler.DockerResource;

public class DockerResourceTest {

	private Properties properties;
	private DockerResource dockerResource = new DockerResource();

	@Before
	public void init() {
		properties = new Properties();
		dockerResource.setConfig(properties);
	}

	@Test
	public void getDockerPayload_should_parse_an_docker_webhook_info() throws IOException, URISyntaxException {
		// Given
		String msg = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("docker_webhook.json").toURI())));
		spark.Request request = new spark.Request() {
			public String body() {
				return msg;
			}
		};

		// When
		DockerPayload dp = dockerResource.getDockerPayload(request);

		// Then
		assertNotNull(dp.getRepository());
		assertEquals("testhook", dp.getRepository().getName());
		assertEquals("svendowideit/testhook", dp.getRepository().getRepoName());
	}
}

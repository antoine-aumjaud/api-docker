package fr.aumjaud.antoine.services.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;

import fr.aumjaud.antoine.services.docker.model.DockerPushData;
import fr.aumjaud.antoine.services.docker.model.DockerRepository;
import spark.Request;
import spark.Response;

public class DockerResource {

	private static Logger logger = LoggerFactory.getLogger(DockerResource.class);
	private Gson gson;

	public DockerResource() {
		GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		gson = builder.create();
	}

	public String webhook(Request request, Response response) {

		try (DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock")) {
			DockerRepository dockerRepository = getData(request).getRepository();

			String imageId = dockerRepository.getRepoName();
			String containerId = dockerRepository.getName();
			docker.stopContainer(containerId, 15);
			docker.removeContainer(containerId);
			docker.removeImage(imageId);

			docker.pull(imageId);
			docker.createContainer(ContainerConfig.builder().image(imageId).build(), containerId);
			docker.startContainer(containerId);

			return "ok";

		} catch (DockerException | InterruptedException e) {
			logger.error(e.getMessage(), e);

			return "error";
		}
	}

	DockerPushData getData(Request request) {
		return gson.fromJson(request.body(), DockerPushData.class);
	}



}

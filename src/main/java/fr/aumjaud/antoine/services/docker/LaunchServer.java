package fr.aumjaud.antoine.services.docker;

import static spark.Spark.post;

import java.util.Properties;

import fr.aumjaud.antoine.services.common.server.spark.SparkImplementation;
import fr.aumjaud.antoine.services.common.server.spark.SparkLauncher;
import fr.aumjaud.antoine.services.docker.requesthandler.DockerResource;

public class LaunchServer {

	public static void main(String... args) {

		new SparkLauncher(new SparkImplementation() {

			private DockerResource dockerResource = new DockerResource();

			@Override
			public String getAppConfigName() {
				return "api-docker.properties";
			}

			@Override
			public String getApiName() {
				return "api-docker";
			}

			@Override
			public void setConfig(Properties appProperties) {
				dockerResource.setConfig(appProperties);
			}

			@Override
			public void initSpark(String securePath) {
				post(securePath + "/docker-webhook/", dockerResource::webhook);
			} 
		});

	}
}

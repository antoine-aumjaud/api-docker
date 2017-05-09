
package fr.aumjaud.antoine.services.docker;

import org.junit.Test;

import fr.aumjaud.antoine.services.docker.DockerResource;
import fr.aumjaud.antoine.services.docker.model.DockerPushData;

public class DockerResourceTest {
	private DockerResource DockerResource = new DockerResource();

	@Test
	public void should_parse_an_docker_webhook_info() {
		//Given
		String msg = "{  \"callback_url\": \"https://registry.hub.docker.com/u/svendowideit/testhook/hook/2141b5bi5i5b02bec211i4eeih0242eg11000a/\",  \"push_data\": {    \"images\": [        \"27d47432a69bca5f2700e4dff7de0388ed65f9d3fb1ec645e2bc24c223dc1cc3\",        \"51a9c7c1f8bb2fa19bcd09789a34e63f35abb80044bc10196e304f6634cc582c\",        \"...\"    ],    \"pushed_at\": 1.417566161e+09,    \"pusher\": \"trustedbuilder\"  },  \"repository\": {    \"comment_count\": \"0\",    \"date_created\": 1.417494799e+09,    \"description\": \"\",    \"dockerfile\": \"\",    \"full_description\": \"Docker Hub based automated build from a GitHub repo\",    \"is_official\": false,    \"is_private\": true,    \"is_trusted\": true,    \"name\": \"testhook\",    \"namespace\": \"svendowideit\",    \"owner\": \"svendowideit\",    \"repo_name\": \"svendowideit/testhook\",    \"repo_url\": \"https://registry.hub.docker.com/u/svendowideit/testhook/\",    \"star_count\": 0,    \"status\": \"Active\"  }}";
		spark.Request request = new spark.Request() {
			public String body() {
				return msg;
			}
		};
		
		//When
		DockerPushData dpd = DockerResource.getData(request);
		
		//Then
		System.out.println(dpd.getRepository().getName());
	}
}

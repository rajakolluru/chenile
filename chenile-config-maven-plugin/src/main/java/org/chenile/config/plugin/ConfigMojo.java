package org.chenile.config.plugin;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.chenile.base.response.GenericResponse;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.service.Info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generate a Chenile Service Registry configuration from 
 * ChenileConfiguration
 *
 */
@Mojo(name = "generate", requiresProject = true, defaultPhase = LifecyclePhase.INTEGRATION_TEST, requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class ConfigMojo extends AbstractMojo {
	/**
	 * The name of the info service (this will be excluded)
	 */
	private static final String INFO_SERVICE_NAME= "infoService";

	/**
	 * The URL from where the api doc is retrieved.
	 */
	@Parameter(defaultValue = "http://localhost:8080/info", property = "org.chenile.config.plugin.infoUrl", required = true)
	private String infoUrl;

	/**
	 * The URL from where the api doc is retrieved.
	 */
	@Parameter(defaultValue = "http://localhost:8080/service-info/", property = "org.chenile.config.plugin.serviceInfoUrlPrefix", required = true)
	private String serviceInfoUrlPrefix;

	/**
	 * Output directory for the generated Service registry JSON.
	 */
	@Parameter(defaultValue = "${project.build.directory}/service-registry", property = "org.chenile.config.plugin.outputDir", required = true)
	private File outputDir;
	/**
	 * The Project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	/**
	 * Skip execution if set to true. Default is false.
	 */
	@Parameter(defaultValue = "false", property = "org.chenile.config.plugin.skip")
	private boolean skip;
	
	@Parameter(defaultValue = "true", property = "org.chenile.config.plugin.skipInfoService")
	private boolean skipInfoService;

	/**
	 * The Project helper.
	 */
	@Component
	private MavenProjectHelper projectHelper;

	/**
	 * The constant GET.
	 */
	private static final String GET = "GET";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	public void execute() {
		if (skip) {
			getLog().info("Skip execution as per configuration");
			return;
		}
		outputDir.mkdirs();
		for (Info.ServiceInfo isi : obtainServices()) {
			processService(isi.name);
		}
	}
	
	private List<Info.ServiceInfo> obtainServices(){
		byte[] bytes = makeGetCall(infoUrl);
		if (bytes == null) return null;
		try {
			GenericResponse<Info> allServices = objectMapper.readValue(bytes,
				new TypeReference<GenericResponse<Info>>() {});
			Info info = allServices.getData();
			return  info.services;	
		}catch(Exception e) {
			getLog().error("An error has occured", e);
			return null;
		}
	}

	private byte[] makeGetCall(String url) {
		HttpURLConnection connection;
		try {
			URL urlForGetRequest = new URL(url);
			connection = (HttpURLConnection) urlForGetRequest.openConnection();
			connection.setRequestMethod(GET);
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				getLog().error("An error has occured: Response code " + responseCode);
				return null;
			}
			String result = this.readFullyAsString(connection.getInputStream());
			return result.getBytes(StandardCharsets.UTF_8);
		}catch(Exception e) {
			getLog().error("An error has occured", e);
			return null;
		}
	}

	private void processService(String name) {
		try {
			if (skipInfoService && name.equals(INFO_SERVICE_NAME)) return;
			byte[] bytes = makeGetCall(serviceInfoUrlPrefix + name);
			GenericResponse<Map<String,Object>> serviceInfo = objectMapper.readValue(bytes,
					new TypeReference<GenericResponse<Map<String,Object>>>() {});
			String s = objectMapper.writeValueAsString(serviceInfo.getData());
			Files.write(Paths.get(outputDir.getAbsolutePath() + "/" + name + ".json"), s.getBytes());
		}catch(Exception e) {
			getLog().error("An error has occured", e);
		}
	}


	/**
	 * Read fully as string string.
	 *
	 * @param inputStream the input stream
	 * @return the string
	 * @throws IOException the io exception
	 */
	private String readFullyAsString(InputStream inputStream) throws IOException {
		return readFully(inputStream).toString(StandardCharsets.UTF_8.name());
	}

	/**
	 * Read fully byte array output stream.
	 *
	 * @param inputStream the input stream
	 * @return the byte array output stream
	 * @throws IOException the io exception
	 */
	private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos;
	}

}

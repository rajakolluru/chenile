package org.chenile.filewatch.test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.chenile.filewatch.test.service.FooService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

import com.google.common.jimfs.Jimfs;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@PropertySource("classpath:org/chenile/filewatch/test/TestFileWatch.properties")
@ActiveProfiles("unittest")
public class TestChenileFileWatch extends SpringBootServletInitializer{
	public static final String SRC_DIR = "/tmp/src/foo";
	public static final String DEST_DIR = "/tmp/dest/foo";

	public static void main(String[] args) {
		SpringApplication.run(TestChenileFileWatch.class, args);
	}
	
	@Bean public FooService fooService() {
		return new FooService();
	}
	
	@Bean FileSystem fileWatcherFileSystem() throws Exception{
    	FileSystem fileSystem =  Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
    	Path src = fileSystem.getPath(SRC_DIR);
    	Files.createDirectories(src);
    	
    	Path dest = fileSystem.getPath(DEST_DIR);
    	Files.createDirectories(dest);
    	return fileSystem;
    }

}


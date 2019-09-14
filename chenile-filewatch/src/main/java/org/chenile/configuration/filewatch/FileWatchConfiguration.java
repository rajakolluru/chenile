package org.chenile.configuration.filewatch;


import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chenile.filewatch.handler.FileProcessor;
import org.chenile.filewatch.handler.FileWatcherExecutorService;
import org.chenile.filewatch.init.ChenileFileWatchInitializer;
import org.chenile.filewatch.init.FileWatchBuilder;
import org.chenile.filewatch.init.FileWatchSubscribersInitializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class FileWatchConfiguration {
	@Value("${chenile.file.watch.json.package}")
	private Resource[] chenileFileWatchJsonResources;
	
	@Value("${chenile.file.watch.source.folder}")
	private String srcFolder;
	
	@Value("${chenile.file.watch.dest.folder}")
	private String destFolder;
	
	@Value("${chenile.file.watch.polltime.seconds}")
	private int pollTimeInSeconds;
	
    @Bean
    public ChenileFileWatchInitializer fileWatchInitializer(){
        return new ChenileFileWatchInitializer(chenileFileWatchJsonResources);
    }
    
    @Bean 
    public FileWatchSubscribersInitializer fileWatchSubscribersInitializer() {
    	return new FileWatchSubscribersInitializer();
    }
    
    @Profile("!unittest")
    @Bean FileSystem fileWatcherFileSystem(){
    	return FileSystems.getDefault();
    }
    @Bean FileWatcherExecutorService fileWatcherExecutorService(@Qualifier("fileWatcherFileSystem") FileSystem fileSystem) {
    	return new FileWatcherExecutorService(srcFolder,destFolder,pollTimeInSeconds,fileSystem);
    }
    
    @Bean ExecutorService executorService() {
    	return Executors.newFixedThreadPool(3);
    }
    
    @Bean
    public FileWatchBuilder fileWatchBuilder() {
    	return  new FileWatchBuilder();
    }
    
    @Bean FileProcessor fileProcessor() {
    	return new FileProcessor();
    }
    
    
}

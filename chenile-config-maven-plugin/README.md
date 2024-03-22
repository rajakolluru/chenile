## **Introduction to chenile-config-maven-plugin**

The aim of chenile-config-maven-plugin is to generate json for the Aurora Service Registry during build time. 
The plugin works during integration-tests phase, and generate the JSON description. 
The plugin works in conjunction with spring-boot-maven plugin. 

You can test it during the integration tests phase using the maven command:

```shell
mvn verify
```

In order to use this functionality, you need to add the plugin declaration on the plugins section of your pom.xml:

```xml
<plugins>
  <plugin>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-maven-plugin</artifactId>
   <version>2.3.4.RELEASE</version>
   <configuration>
      <jvmArguments>-Dspring.application.admin.enabled=true</jvmArguments>
   </configuration>
   <executions>
    <execution>
     <id>pre-integration-test</id>
     <goals>
      <goal>start</goal>
     </goals>
    </execution>
    <execution>
     <id>post-integration-test</id>
     <goals>
      <goal>stop</goal>
     </goals>
    </execution>
   </executions>
  </plugin>
  <plugin>
   <groupId>org.chenile</groupId>
   <artifactId>chenile-config-maven-plugin</artifactId>
	 <!-- version can be skipped if using Aurora as the parent -->
   <executions>
    <execution>
     <id>integration-test</id>
     <goals>
      <goal>generate</goal>
     </goals>
    </execution>
   </executions>
  </plugin>
</plugins>
```
			
## **Custom settings of the springdoc-openapi-maven-plugin**

It possible to customise the following plugin properties:
*   org.chenile.config.plugin.skipInfoService: Skip the generation of Info service JSON.
    * The default value is: true
*   org.chenile.config.plugin.infoUrl: The local url of your info service
    * The default value is: http://localhost:8080/info
*   org.chenile.config.plugin.serviceInfoUrlPrefix: The local url of your info service
    * The default value is: http://localhost:8080/service-info/
*  org.chenile.config.plugin.outputDir: The output directory that would contain the JSONs
    * The default value is: ${project.build.directory}/service-registry
*   org.chenile.config.plugin.skip: Skip execution if set to true.
    * The default value is: false

```xml
<plugin>
 <groupId>org.springdoc</groupId>
 <artifactId>springdoc-openapi-maven-plugin</artifactId>
 <version>1.1</version>
 <executions>
  <execution>
   <id>integration-test</id>
   <goals>
    <goal>generate</goal>
   </goals>
  </execution>
 </executions>
 <configuration>
  <apiDocsUrl>http://localhost:8080/v3/api-docs</apiDocsUrl>
  <outputFileName>openapi.json</outputFileName>
  <outputDir>/home/springdoc/maven-output</outputDir>
  <skip>false</skip>
 </configuration>
</plugin>
```


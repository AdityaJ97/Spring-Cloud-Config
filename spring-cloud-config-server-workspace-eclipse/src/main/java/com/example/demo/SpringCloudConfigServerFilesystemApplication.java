package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.JdbcEnvironmentRepository;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@EnableConfigServer
@Import({NativeEnvironmentRepository.class})

public class SpringCloudConfigServerFilesystemApplication {
	
       public static void main(String[] args) {
           SpringApplication.run(SpringCloudConfigServerFilesystemApplication.class, args);
       }
     
}




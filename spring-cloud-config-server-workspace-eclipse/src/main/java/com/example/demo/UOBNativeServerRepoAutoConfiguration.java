package com.example.demo;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.environment.NativeEnvironmentProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Order(1)
@Primary
@Configuration
//@Profile("uob")
public class UOBNativeServerRepoAutoConfiguration extends UOBNativeRepositoryProperties {
    	final Logger log = Logger.getLogger(this.getClass().getName());

		private Boolean failOnError = false;
	  
	    private Boolean addLabelLocations = true;
	    private String defaultLabel = "master";

	    @Value("${spring.cloud.config.server.uob.searchLocations}")
	    private String[] searchLocations;
	  
	    private String version;
	    
	    @Value("${spring.cloud.config.server.uob.order:0x7fffffff}")
	    private int order;
	    
	    //Extension to facilitate multiple apptype property types for property file grab
	    @Value("${spring.cloud.config.server.uob.apptypes:banking}")
	    private String application_types;
	    	   
	    
	    @Bean
	    @ConditionalOnMissingBean(UOBNativeServerRepo.class)
	    UOBNativeServerRepo uobNativeServerRepo(ConfigurableEnvironment environment, NativeEnvironmentProperties properties, UOBNativeServerRepoAutoConfiguration properties1) {
	    	properties.setSearchLocations(searchLocations);
	    	properties.setDefaultLabel(defaultLabel);
	    	properties.setFailOnError(failOnError);
	    	properties.setAddLabelLocations(addLabelLocations);
	    	properties.setOrder(order);
	    	properties.setVersion(version);
	    	properties1.setSearchLocations(searchLocations);
	    	properties1.setDefaultLabel(defaultLabel);
	    	properties1.setFailOnError(failOnError);
	    	properties1.setAddLabelLocations(addLabelLocations);
	    	properties1.setOrder(order);
	    	properties1.setVersion(version);
	    	properties1.setApplication_types(application_types);
	    	UOBNativeServerRepo repo = new UOBNativeServerRepo(environment,properties,properties1);
	    	log.info("#######################" + repo);
	    	return repo;
	    }

	    
	    
}


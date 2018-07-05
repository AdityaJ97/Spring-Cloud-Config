package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.core.Ordered;

@ConfigurationProperties("spring.cloud.config.server.uob")
public class UOBNativeRepositoryProperties implements EnvironmentRepositoryProperties {
    /**
     * Flag to determine how to handle exceptions during decryption (default false).
     */
    private Boolean failOnError = false;
    private String application_types= "banking";
    /**
     * Flag to determine whether label locations should be added.
     */
    private Boolean addLabelLocations = true;
    private String defaultLabel = "master";
    /**
     * Locations to search for configuration files. Defaults to the same as a Spring Boot
     * app so [classpath:/,classpath:/config/,file:./,file:./config/].
     */
    private String[] searchLocations = new String[0];
    /**
     * Version string to be reported for native repository
     */
    private String version;
    private int order = Ordered.LOWEST_PRECEDENCE;

    public Boolean getFailOnError() {
        return failOnError;
    }

    public void setFailOnError(Boolean failOnError) {
        this.failOnError = failOnError;
    }

    public Boolean getAddLabelLocations() {
        return addLabelLocations;
    }

    public void setAddLabelLocations(Boolean addLabelLocations) {
        this.addLabelLocations = addLabelLocations;
    }

    public String getDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(String defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    public String[] getSearchLocations() {
        return searchLocations;
    }

    public void setSearchLocations(String[] searchLocations) {
        this.searchLocations = searchLocations;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

	public String getApplication_types() {
		return application_types;
	}

	public void setApplication_types(String application_types) {
		this.application_types = application_types;
	}
}

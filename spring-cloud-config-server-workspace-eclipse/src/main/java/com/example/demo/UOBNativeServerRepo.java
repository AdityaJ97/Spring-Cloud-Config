package com.example.demo;

import org.springframework.cloud.config.server.environment.NativeEnvironmentProperties;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.PassthruEnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.cloud.config.server.environment.SearchPathLocator.Locations;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.JdbcEnvironmentRepository;

public class UOBNativeServerRepo extends NativeEnvironmentRepository {
	private static Log logger = LogFactory.getLog(UOBNativeServerRepo.class);

	private String defaultLabel;

	private String[] searchLocations;
	
	//Apptypes contains the names of all property files that need to be lifted, including their environments. E.g: apptypes = payments, payments-dev, payments-qa
	private String apptypes;

	private boolean failOnError;

	private boolean addLabelLocations;

	private String version;

	private static final String[] DEFAULT_LOCATIONS = new String[] { "classpath:/",
			"classpath:/config/", "file:./", "file:./config/" };

	private ConfigurableEnvironment environment;

	private int order;
	
	public UOBNativeServerRepo(ConfigurableEnvironment environment, NativeEnvironmentProperties properties, UOBNativeServerRepoAutoConfiguration properties1) {
		super(environment, properties);
		this.environment = environment;
		this.addLabelLocations = properties1.getAddLabelLocations();
		this.defaultLabel = properties1.getDefaultLabel();
		this.failOnError = properties1.getFailOnError();
		this.order = properties1.getOrder();
		this.searchLocations = properties1.getSearchLocations();
		this.version = properties1.getVersion();
		this.apptypes=properties1.getApplication_types();
	}
	
	private ConfigurableEnvironment getEnvironment(String profile) {
		ConfigurableEnvironment environment = new StandardEnvironment();
		Map<String, Object> map = new HashMap<>();
		map.put("spring.profiles.active", profile);
		map.put("spring.main.web-application-type", "none");
		environment.getPropertySources().addFirst(new MapPropertySource("profiles", map));
		return environment;
	}

	private String[] getArgs(String application, String profile, String label) {

		List<String> list = new ArrayList<String>();
		if (!apptypes.startsWith("application")) {
			apptypes = "application," + apptypes;
		}
		list.add("--spring.config.name=" + apptypes);
		list.add("--spring.cloud.bootstrap.enabled=false");
		list.add("--encrypt.failOnError=" + this.failOnError);
		list.add("--spring.config.location=" + StringUtils.arrayToCommaDelimitedString(
				getLocations(application, profile, label).getLocations()));
		return list.toArray(new String[0]);
	}
	
	private boolean isDirectory(String location) {
		return !location.contains("{") && !location.endsWith(".properties")
				&& !location.endsWith(".yml") && !location.endsWith(".yaml");
	}
	
	@Override
	protected Environment clean(Environment value) {
		Environment result = new Environment(value.getName(), value.getProfiles(),
				value.getLabel(), this.version, value.getState());
		for (PropertySource source : value.getPropertySources()) {
			String name = source.getName();
			//System.out.println(name);
			if (this.environment.getPropertySources().contains(name)) {
				continue;
			}
			name = name.replace("applicationConfig: [", "");
			name = name.replace("]", "");
			if (this.searchLocations != null) {
				boolean matches = false;
				String normal = name;
				if (normal.startsWith("file:")) {
					normal = StringUtils
							.cleanPath(new File(normal.substring("file:".length()))
									.getAbsolutePath());
				}
				String profile = result.getProfiles() == null ? null
						: StringUtils.arrayToCommaDelimitedString(result.getProfiles());
				for (String pattern : getLocations(result.getName(), profile,
						result.getLabel()).getLocations()) {
					if (!pattern.contains(":")) {
						pattern = "file:" + pattern;
					}
					if (pattern.startsWith("file:")) {
						pattern = StringUtils
								.cleanPath(new File(pattern.substring("file:".length()))
										.getAbsolutePath())
								+ "/";
					}
					if (logger.isTraceEnabled()) {
						logger.trace("Testing pattern: " + pattern
								+ " with property source: " + name);
					}
					if (normal.startsWith(pattern)
							&& !normal.substring(pattern.length()).contains("/")) {
						matches = true;
						break;
					}
				}
				if (!matches) {
					// Don't include this one: it wasn't matched by our search locations
					if (logger.isDebugEnabled()) {
						logger.debug("Not adding property source: " + name);
					}
					continue;
				}
			}
			logger.info("Adding property source: " + name);
			result.add(new PropertySource(name, source.getSource()));
		}
		return result;
	}



	
	@Override
	public Environment findOne(String config, String profile, String label){
		//System.out.print(config);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(
				PropertyPlaceholderAutoConfiguration.class);
		ConfigurableEnvironment environment = getEnvironment(profile);
		builder.environment(environment);
		builder.web(WebApplicationType.NONE).bannerMode(Mode.OFF);
		if (!logger.isDebugEnabled()) {
			// Make the mini-application startup less verbose
			builder.logStartupInfo(false);
		}
		String[] args = getArgs(config, profile, label);
		System.out.print(Arrays.toString(args));
		// Explicitly set the listeners (to exclude logging listener which would change
		// log levels in the caller)
		builder.application()
				.setListeners(Arrays.asList(new ConfigFileApplicationListener()));
		ConfigurableApplicationContext context = builder.run(args);
		environment.getPropertySources().remove("profiles");
		try {
			//System.out.println(clean(new PassthruEnvironmentRepository(environment).findOne(config,
					//profile, label)));
			return clean(new PassthruEnvironmentRepository(environment).findOne(config,
					profile, label));
		}
		finally {
			context.close();
		}
		
	}
	
	@Override
	public Locations getLocations(String application, String profile, String label) {
		String[] locations = this.searchLocations;
		if (this.searchLocations == null || this.searchLocations.length == 0) {
			locations = DEFAULT_LOCATIONS;
		}
		Collection<String> output = new LinkedHashSet<String>();

		if (label == null) {
			label = defaultLabel;
		}
		for (String location : locations) {
			String[] profiles = new String[] { profile };
			if (profile != null) {
				profiles = StringUtils.commaDelimitedListToStringArray(profile);
			}
			String[] apps = new String[] { application };
			if (application != null) {
				apps = StringUtils.commaDelimitedListToStringArray(application);
			}
			for (String prof : profiles) {
				for (String app : apps) {
					String value = location;
					if (application != null) {
						value = value.replace("{application}", app);
					}
					if (prof != null) {
						value = value.replace("{profile}", prof);

					}
					if (label != null) {
						value = value.replace("{label}", label);
						System.out.println(label);

					}
					if (!value.endsWith("/")) {
						value = value + "/";

					}
					if (isDirectory(value)) {
						output.add(value);
					}
				}
			}
		}
		if (this.addLabelLocations) {
			for (String location : locations) {
				if (StringUtils.hasText(label)) {
					String labelled = location + label.trim() + "/";
					if (isDirectory(labelled)) {
						output.add(labelled);
						
					}
				}
			}
		}
		for ( String opt: output) {
			System.out.println(opt);
		}
		return new Locations(application, profile, label, this.version,
				output.toArray(new String[0]));
	}
	
	
	
	
}

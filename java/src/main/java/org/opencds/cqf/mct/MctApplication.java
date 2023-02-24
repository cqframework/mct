package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import org.opencds.cqf.mct.api.FacilityRegistrationAPI;
import org.opencds.cqf.mct.api.GatherAPI;
import org.opencds.cqf.mct.api.GeneratePatientDataAPI;
import org.opencds.cqf.mct.api.MeasureConfigurationAPI;
import org.opencds.cqf.mct.api.PatientSelectorAPI;
import org.opencds.cqf.mct.api.ReceivingSystemConfigurationAPI;
import org.opencds.cqf.mct.api.SubmitAPI;
import org.opencds.cqf.mct.config.MctProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@ServletComponentScan(basePackageClasses = { RestfulServer.class })
@SpringBootApplication(exclude = { ElasticsearchRestClientAutoConfiguration.class })
@Import({ MctProperties.class })
public class MctApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(MctApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MctApplication.class);
	}

	@Autowired
	AutowireCapableBeanFactory beanFactory;

	@Bean
	public ServletRegistrationBean<RestfulServer> hapiServletRegistration(RestfulServer restfulServer) {
		ServletRegistrationBean<RestfulServer> servletRegistrationBean = new ServletRegistrationBean<>();
		beanFactory.autowireBean(restfulServer);
		servletRegistrationBean.setName("MCT servlet");
		servletRegistrationBean.setServlet(restfulServer);
		servletRegistrationBean.addUrlMappings("/mct/*");
		servletRegistrationBean.setLoadOnStartup(1);

		return servletRegistrationBean;
	}

	@Bean
	public RestfulServer restfulServer(FhirContext fhirContext, SpringContext springContext, CorsConfiguration corsConfiguration) {
		RestfulServer fhirServer = new RestfulServer(fhirContext);
		fhirServer.registerInterceptor(new CorsInterceptor(corsConfiguration));
		fhirServer.registerProvider(new GatherAPI());
		fhirServer.registerProvider(new FacilityRegistrationAPI());
		fhirServer.registerProvider(new MeasureConfigurationAPI());
		fhirServer.registerProvider(new ReceivingSystemConfigurationAPI());
		fhirServer.registerProvider(new GeneratePatientDataAPI());
		fhirServer.registerProvider(new PatientSelectorAPI());
		fhirServer.registerProvider(new SubmitAPI());
		return fhirServer;
	}

	@Bean
	public FhirContext fhirContext(MctProperties properties) {
		return FhirContext.forCached(properties.getFhirVersion());
	}

	@Bean
	public CorsConfiguration corsConfiguration() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedHeader("Origin");
		config.addAllowedHeader("Accept");
		config.addAllowedHeader("X-Requested-With");
		config.addAllowedHeader("Content-Type");
		config.addAllowedOrigin("*");
		config.addExposedHeader("Location");
		config.addExposedHeader("Content-Location");
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		return config;
	}
}


package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.opencds.cqf.mct.api.FacilityRegistrationAPI;
import org.opencds.cqf.mct.api.GatherAPI;
import org.opencds.cqf.mct.api.MeasureConfigurationAPI;
import org.opencds.cqf.mct.api.ReceivingSystemConfigurationAPI;
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
	public ServletRegistrationBean<RestfulServerWithCors> hapiServletRegistration(RestfulServerWithCors restfulServer) {
		ServletRegistrationBean<RestfulServerWithCors> servletRegistrationBean = new ServletRegistrationBean<>();
		beanFactory.autowireBean(restfulServer);
		servletRegistrationBean.setName("MCT servlet");
		servletRegistrationBean.setServlet(restfulServer);
		servletRegistrationBean.addUrlMappings("/mct/*");
		servletRegistrationBean.setLoadOnStartup(1);

		return servletRegistrationBean;
	}

	@Bean
	public RestfulServerWithCors restfulServer(FhirContext fhirContext, SpringContext springContext) {
		RestfulServerWithCors fhirServer = new RestfulServerWithCors(fhirContext);
		fhirServer.registerProvider(new GatherAPI());
		fhirServer.registerProvider(new FacilityRegistrationAPI());
		fhirServer.registerProvider(new MeasureConfigurationAPI());
		fhirServer.registerProvider(new ReceivingSystemConfigurationAPI());
		return fhirServer;
	}

	@Bean
	public FhirContext fhirContext(MctProperties properties) {
		return FhirContext.forCached(properties.getFhirVersion());
	}
}


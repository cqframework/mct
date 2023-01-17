package org.opencds.cqf.mct;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.opencds.cqf.mct.api.GatherAPI;
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
@Import({ MctConfig.class })
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
	public RestfulServer restfulServer(MctConfig config) {
		FhirContext fhirContext = config.fhirContext();
		RestfulServer fhirServer = new RestfulServer(fhirContext);
		fhirServer.registerProvider(new GatherAPI(fhirContext));
		return fhirServer;
	}

}

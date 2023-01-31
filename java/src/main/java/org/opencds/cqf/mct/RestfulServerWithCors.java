package org.opencds.cqf.mct;
import java.util.Arrays;

import javax.servlet.ServletException;

import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;

public class RestfulServerWithCors extends RestfulServer {

		public RestfulServerWithCors(FhirContext fhirContext) {
			super(fhirContext);
		}

		@Override
		protected void initialize() throws ServletException {
				CorsConfiguration config = new CorsConfiguration();
				config.addAllowedHeader("Origin");
				config.addAllowedHeader("Accept");
				config.addAllowedHeader("X-Requested-With");
				config.addAllowedHeader("Content-Type");

				config.addAllowedOrigin("*");

				config.addExposedHeader("Location");
				config.addExposedHeader("Content-Location");
				config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

				// Create the interceptor and register it
				CorsInterceptor interceptor = new CorsInterceptor(config);
				registerInterceptor(interceptor);
		}
	}

package com.marketrow.ecommerce;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Required when packaging as a WAR for deployment into an external servlet
 * container (e.g. a standalone Tomcat). This hooks the Spring Boot app into
 * Tomcat's normal WAR startup lifecycle instead of relying on the embedded
 * server used by "java -jar".
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MarketRowApplication.class);
    }

}

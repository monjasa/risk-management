package org.monjasa.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.util.List;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {

        List<ClassPathResource> classPathResources = List.of(
                new ClassPathResource("data/risk-sources.json"),
                new ClassPathResource("data/risk-events.json"),
                new ClassPathResource("data/risk-arrangements.json"),
                new ClassPathResource("data/experts.json")
        );

        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(classPathResources.toArray(Resource[]::new));

        return factory;
    }
}

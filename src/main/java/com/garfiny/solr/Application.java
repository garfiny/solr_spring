package com.garfiny.solr;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.boot.SpringApplication;

import com.garfiny.solr.datasources.australiancurriculum.AustralianCurriculumRdfDownloader;

@Configuration
@ComponentScan({"com.garfiny.solr.repositories", "com.garfiny.solr.datasources"})
@PropertySource("classpath:application.properties")
public class Application {
	
	public void test(ApplicationContext context) {
		AustralianCurriculumRdfDownloader downloader = 
				context.getBean(AustralianCurriculumRdfDownloader.class);
		downloader.downloadEnglish();
	}

	public static void main(String[] args) {
        AnnotationConfigApplicationContext context = 
        		new AnnotationConfigApplicationContext(Application.class);
        context.register(EmbeddedSolrContext.class);
        context.register(HttpSolrContext.class);
//        SpringApplication.run(context);
        new Application().test(context);
    }
}
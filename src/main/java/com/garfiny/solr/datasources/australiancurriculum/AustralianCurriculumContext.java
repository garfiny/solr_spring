package com.garfiny.solr.datasources.australiancurriculum;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@PropertySource("classpath:australian_curriculum_rdf.properties")
public class AustralianCurriculumContext {

}
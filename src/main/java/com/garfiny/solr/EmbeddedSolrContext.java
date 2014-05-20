package com.garfiny.solr;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactoryBean;

@Configuration
@EnableSolrRepositories(basePackages={"com.garfiny.solr.repositories"})
@Profile("dev")
public class EmbeddedSolrContext {

  static final String SOLR_HOST = "solr.host";

  @Resource
  private Environment environment;

  @Bean
  public SolrServer solrServer() {
    String solrHost = environment.getRequiredProperty(SOLR_HOST);
    return new HttpSolrServer(solrHost);
  }
  
  @Bean
  public EmbeddedSolrServerFactoryBean solrServerFactoryBean() {
      EmbeddedSolrServerFactoryBean factory = new EmbeddedSolrServerFactoryBean();

      factory.setSolrHome(environment.getRequiredProperty("solr.solr.home"));

      return factory;
  }

  @Bean
  public SolrTemplate solrTemplate() throws Exception {
      return new SolrTemplate(solrServerFactoryBean().getObject());
  }
}
package com.garfiny.solr;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class BaseTest extends AbstractJUnit4SpringContextTests {

//	@Autowired
//    private ApplicationContext applicationContext;
//	
//	public ApplicationContext getApplicationContext() {
//		return applicationContext;
//	}
}

package com.parimal.omslite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Main class controlling OMS Lite Application
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class OMSLiteApplication {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(OMSLiteApplication.class, args);
	}

	@PostConstruct
	public void init() {
		logger.trace("OMSLiteApplication - Application started ...");

	}

	@PreDestroy
	public void destroy() {
		logger.trace("OMSLiteApplication - Shutdown initiated ...");
	}

}

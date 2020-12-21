package com.mmt.ddreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import reactor.core.scheduler.Schedulers;
import reactor.util.Metrics;


@SpringBootApplication
@PropertySource(value = "classpath:application.properties")
public class DdReactiveApplication {

	public static void main(String[] args) {
//		BlockHound.install();
		Schedulers.enableMetrics();
		SpringApplication.run(DdReactiveApplication.class, args);
		Metrics.MicrometerConfiguration.getRegistry();
	}

}

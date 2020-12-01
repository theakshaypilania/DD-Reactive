package com.mmt.ddreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
public class DdReactiveApplication {

	public static void main(String[] args) {
//		BlockHound.install();

		SpringApplication.run(DdReactiveApplication.class, args);
	}

}

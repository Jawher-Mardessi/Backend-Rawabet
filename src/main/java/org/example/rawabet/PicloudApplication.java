package org.example.rawabet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PicloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicloudApplication.class, args);
	}

}

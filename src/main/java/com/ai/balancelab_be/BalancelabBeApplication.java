package com.ai.balancelab_be;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BalancelabBeApplication {
	private static final Logger logger = LoggerFactory.getLogger(BalancelabBeApplication.class);

	public static void main(String[] args) {
		// OS 감지
		String osName = System.getProperty("os.name").toLowerCase();
		String activeProfile;

		if (osName.contains("win")) {
			activeProfile = "dev-windows";
		} else if (osName.contains("mac") || osName.contains("nix") || osName.contains("nux")) {
			activeProfile = "dev-mac";
		} else {
			activeProfile = "default";
		}

		logger.info("Detected OS: {}, Setting profile: {}", osName, activeProfile);

		// 프로파일 동적 설정
		SpringApplication app = new SpringApplication(BalancelabBeApplication.class);
		app.setAdditionalProfiles(activeProfile);
		app.run(args);
	}

}

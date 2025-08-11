package hu.pmamico.deszkamatek.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class BuildInfoConfig {

    @Bean
    public BuildInfo buildInfo() {
        return new BuildInfo();
    }

    @Component
    public static class BuildInfo {
        private final String buildTime;

        public BuildInfo() {
            // Capture the current time as the build time when the application starts
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.buildTime = now.format(formatter);
        }

        public String getBuildTime() {
            return buildTime;
        }
    }
}
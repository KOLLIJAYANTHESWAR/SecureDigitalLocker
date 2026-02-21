package com.sdl.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "storage")
@Validated
@Getter
@Setter
public class StorageConfig {

    @NotBlank
    private String type; // local or gcp

    private Local local = new Local();
    private Gcp gcp = new Gcp();

    @Getter
    @Setter
    public static class Local {

        @NotBlank
        private String uploadDir;

        @NotBlank
        private String profileDir;

        @NotBlank
        private String documentDir;
    }

    @Getter
    @Setter
    public static class Gcp {

        private String bucketName;
        private String projectId;
    }
}
package org.evabirds.evabirds.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${cloudinary.url:}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        if (StringUtils.hasText(cloudinaryUrl)) {
            log.info("Cloudinary initialized via CLOUDINARY_URL");
            return new Cloudinary(cloudinaryUrl);
        } else if (StringUtils.hasText(cloudName) && StringUtils.hasText(apiKey) && StringUtils.hasText(apiSecret)) {
            log.info("Cloudinary initialized for cloud: {}", cloudName);
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", cloudName);
            config.put("api_key", apiKey);
            config.put("api_secret", apiSecret);
            config.put("secure", true);
            return new Cloudinary(config);
        } else {
            log.warn("Cloudinary credentials not detected in environment. Image operations will fallback to local storage.");
            Map<String, Object> dummyConfig = new HashMap<>();
            dummyConfig.put("secure", true);
            return new Cloudinary(dummyConfig);
        }
    }
}

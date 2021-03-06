package pandora.clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication
public class AudioStreamingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioStreamingApplication.class, args);
    }
}

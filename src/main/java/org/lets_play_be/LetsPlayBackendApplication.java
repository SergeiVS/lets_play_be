package org.lets_play_be;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEncryptableProperties
@EnableScheduling
@EnableAsync
public class LetsPlayBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LetsPlayBackendApplication.class, args);
    }
}

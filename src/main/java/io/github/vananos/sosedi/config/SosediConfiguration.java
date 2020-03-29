package io.github.vananos.sosedi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@EnableScheduling
public class SosediConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean(name = "taskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}

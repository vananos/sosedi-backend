package io.github.vananos.sosedi.config;

import io.github.vananos.sosedi.SosediApp;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SosediApp.class)
public class SosediConfig {

}

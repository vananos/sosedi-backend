package io.github.vananos.sosedi.config.security;

import io.github.vananos.sosedi.security.CustomPermissionEvaluator;
import io.github.vananos.sosedi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private PasswordEncoder passwordEncoder;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private UserDetailsService userDetailsService;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private UserService userService;

    @Value("${sosedi.cors.dev}")
    private Boolean isCorsDev;

    @Autowired
    public SecurityConfig(
            AuthenticationEntryPoint authenticationEntryPoint,
            UserDetailsService userDetailsService,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        if (isCorsDev) {
            http.cors();
        } else {
            http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        }
        http.csrf().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/register", "/confirmation*", "/feedback")
                .permitAll()
                .antMatchers("/*").authenticated()
                .and()
                .formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(failureHandler)
                .and()
                .httpBasic()
                .and()
                .logout();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator());
        web.expressionHandler(expressionHandler);
    }

    @Bean
    public PermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator(userService);
    }
}

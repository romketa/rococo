package guru.qa.rococo.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import guru.qa.rococo.config.cors.CorsCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.service.DefaultGrpcServiceConfigurer;
import org.springframework.grpc.server.service.DefaultGrpcServiceDiscoverer;
import org.springframework.grpc.server.service.GrpcServiceConfigurer;
import org.springframework.grpc.server.service.GrpcServiceDiscoverer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private final CorsCustomizer corsCustomizer;

  @Autowired
  public SecurityConfig(CorsCustomizer corsCustomizer) {
    this.corsCustomizer = corsCustomizer;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    corsCustomizer.corsCustomizer(http);

    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(customizer ->
        customizer
            .requestMatchers(
                antMatcher(HttpMethod.GET, "/api/session"),
                antMatcher(HttpMethod.GET, "/api/artist/**"),
                antMatcher(HttpMethod.GET, "/api/museum/**"),
                antMatcher(HttpMethod.GET, "/api/country/**"),
                antMatcher(HttpMethod.GET, "/api/painting/**"))
            .permitAll()
            .anyRequest()
            .authenticated()
    ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public GrpcServiceDiscoverer grpcServiceDiscoverer(GrpcServiceConfigurer serviceConfigure, ApplicationContext applicationContext) {
    return new DefaultGrpcServiceDiscoverer(serviceConfigure, applicationContext);
  }

  @Bean
  public GrpcServiceConfigurer grpcServiceConfigurer(ApplicationContext applicationContext) {
    return new DefaultGrpcServiceConfigurer(applicationContext);
  }
}
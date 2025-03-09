package guru.qa.rococo.config;

import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import io.grpc.ManagedChannel;
import java.util.List;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.server.service.DefaultGrpcServiceConfigurer;
import org.springframework.grpc.server.service.DefaultGrpcServiceDiscoverer;
import org.springframework.grpc.server.service.GrpcServiceConfigurer;
import org.springframework.grpc.server.service.GrpcServiceDiscoverer;

@Configuration
@ImportAutoConfiguration({
    org.springframework.grpc.server.security.GrpcSecurity.class,
    org.springframework.grpc.autoconfigure.client.GrpcClientAutoConfiguration.class,
    org.springframework.grpc.autoconfigure.common.codec.GrpcCodecConfiguration.class,
})
public class GrpcConfiguration {
}

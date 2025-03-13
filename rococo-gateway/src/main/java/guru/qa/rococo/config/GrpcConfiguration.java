package guru.qa.rococo.config;

import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.grpc.rococo.RococoCountryServiceGrpc;
import guru.qa.grpc.rococo.RococoMuseumServiceGrpc;
import guru.qa.grpc.rococo.RococoPaintingServiceGrpc;
import io.grpc.ManagedChannel;
import java.util.List;

import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
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

    @Bean
    @Order(100)
    GrpcChannelBuilderCustomizer<NettyChannelBuilder> flowControlCustomizer() {
        return (name, builder) -> builder.flowControlWindow(50 * 1024 * 1024);
    }

    @Bean
    @Order(200)
    <T extends ManagedChannelBuilder<T>> GrpcChannelBuilderCustomizer<T> channelCustomizer() {
        return (name, builder) -> builder
                .intercept(new GrpcConsoleInterceptor())
                .maxRetryAttempts(5);
    }

    @Bean
    RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub(
            GrpcChannelFactory channelFactory) {
        ManagedChannel channel = channelFactory.createChannel("grpcArtistClient");
        return RococoArtistServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryStub(
            GrpcChannelFactory channelFactory) {
        ManagedChannel channel = channelFactory.createChannel("grpcCountryClient");
        return RococoCountryServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub(
            GrpcChannelFactory channelFactory) {
        ManagedChannel channel = channelFactory.createChannel("grpcMuseumClient");
        return RococoMuseumServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingStub(
            GrpcChannelFactory channelFactory) {
        ManagedChannel channel = channelFactory.createChannel("grpcPaintingClient");
        return RococoPaintingServiceGrpc.newBlockingStub(channel);
    }
}

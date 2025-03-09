package guru.qa.rococo.config;

import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.server.service.GrpcServiceDiscoverer;

@Configuration
public class GrpcClientConfig {

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
}
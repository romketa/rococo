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
  @Order(50)
  <T extends ManagedChannelBuilder<T>> GrpcChannelBuilderCustomizer<T> retryChannelCustomizer() {
    return (name, builder) -> builder.intercept(new GrpcConsoleInterceptor()).maxInboundMessageSize(9999999).maxRetryAttempts(5);
  }

  @Bean
  RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub(
      GrpcChannelFactory channelFactory) {
    ManagedChannel channel = channelFactory.createChannel("grpcArtistClient");
    return RococoArtistServiceGrpc.newBlockingStub(channel);
  }
}

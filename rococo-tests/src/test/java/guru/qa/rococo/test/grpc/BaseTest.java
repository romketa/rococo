package guru.qa.rococo.test.grpc;

import static com.google.protobuf.ByteString.copyFromUtf8;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.RococoArtistServiceGrpc;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import java.util.UUID;
import utils.GrpcConsoleInterceptor;

@GrpcTest
public abstract class BaseTest {

  protected static final Config CFG = Config.getInstance();

}

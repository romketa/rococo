package utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcConsoleInterceptor implements  io.grpc.ClientInterceptor {

  private static final JsonFormat.Printer printer = JsonFormat.printer();
  private static final int MAX_MESSAGE_LENGTH = 300;

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall(
        channel.newCall(methodDescriptor, callOptions)
    ) {

      @Override
      public void sendMessage(Object message) {
        try {
          String jsonMessage = printer.print((MessageOrBuilder) message);
          logMessage("REQUEST", jsonMessage);
        } catch (InvalidProtocolBufferException e) {
          throw new RuntimeException(e);
        }
        super.sendMessage(message);
      }

      @Override
      public void start(Listener responseListener, Metadata headers) {
        ForwardingClientCallListener<Object> clientCallListener = new ForwardingClientCallListener<>() {

          @Override
          public void onMessage(Object message) {
            try {
              String jsonMessage = printer.print((MessageOrBuilder) message);
              logMessage("RESPONSE", jsonMessage);
            } catch (InvalidProtocolBufferException e) {
              throw new RuntimeException(e);
            }
            super.onMessage(message);
          }

          @Override
          protected Listener<Object> delegate() {
            return responseListener;
          }
        };
        super.start(clientCallListener, headers);
      }

      private void logMessage(String prefix, String message) {
        if (message.length() > MAX_MESSAGE_LENGTH) {
          System.out.println(prefix + ": " + message.substring(0, MAX_MESSAGE_LENGTH));
        } else {
          System.out.println(prefix + ": " + message);
        }
      }
    };
  }
}

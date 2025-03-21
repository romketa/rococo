package utils;

import static com.google.protobuf.ByteString.copyFromUtf8;

import com.google.protobuf.ByteString;
import java.util.UUID;

public class Utils {

  public static ByteString toByteStringFromUuid(UUID uuid) {
    return copyFromUtf8(uuid.toString());
  }

}

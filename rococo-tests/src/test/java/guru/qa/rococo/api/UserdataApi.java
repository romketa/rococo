package guru.qa.rococo.api;

import java.util.List;
import guru.qa.rococo.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserdataApi {

  @GET("/internal/users/current")
  Call<UserJson> currentUser(@Query("username") String username);

  @GET("/internal/users/all")
  Call<List<UserJson>> allUsers();

  @POST("/internal/users/update")
  Call<UserJson> updateUserInfo(@Body UserJson user);
}

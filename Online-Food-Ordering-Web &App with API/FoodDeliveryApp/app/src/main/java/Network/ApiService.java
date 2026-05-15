package Network;
import Model.Order;
import Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    @FormUrlEncoded
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @POST("register")
    @FormUrlEncoded
    Call<BasicResponse> register(
            @Field("name") String name,
            @Field("username") String username,
            @Field("password") String password
    );



    @GET("orders")
    Call<List<Order>> getOrders();
    @GET("wallet/{id}")
    Call<WalletResponse> getWallet(@Path("id") String userId);
    @PUT("orders/{id}/status")
    Call<BasicResponse> updateStatuss(
            @Path("id") String orderId,
            @Query("status") String status
    );
    @FormUrlEncoded
    @POST("/orders/update-status")
    Call<BasicResponse> updateStatus(
            @Field("order_id") String order_id,
            @Field("status") String status,
            @Field("rider_id") String rider_id
    );


    @GET("rider/orders/{id}")
    Call<List<Order>> getRiderOrders(@Path("id") String riderId);

    @GET("user/{id}")
    Call<User> getUserProfile(@Path("id") String riderId);


}

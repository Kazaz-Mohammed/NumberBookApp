package com.example.telephonetp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface ContactApi {
//    @FormUrlEncoded
//    @POST("insert_contact.php") // adjust if it's in a folder
//    Call<ApiResponse> insertContact(
//            @Field("name") String name,
//            @Field("number") String number
//    );


    @POST("insert_contact.php")
    Call<Void> insertContact(@Body Contact contact); // This is the POST method
}

package com.c5durango.alertalsm.Retrofit;

import com.c5durango.alertalsm.Constantes;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class NetworkTask {
    Retrofit retrofit;
    ApiCalls apiCalls;
    String TAG = "VIDEO_SERVICE";

    public NetworkTask() {
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constantes.URL+"/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            apiCalls = retrofit.create(ApiCalls.class);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /*public method for upload*/
    /*public Call<String> uploadImageToServer(String url, MultipartBody.Part part) {
        return apiCalls.uploadImage(url, part);
    }*/


    public Call<String> uploadVideo(int id_reporte, MultipartBody.Part[] fbody) {
        return apiCalls.uploadVideo(id_reporte, fbody);
    }

    /*Retrofit Interface*/
    public interface ApiCalls {

        @Multipart
        @POST("upload/video/{reporte}")
        Call<String> uploadVideo(@Path(value = "reporte") int id_reporte, @Part MultipartBody.Part[] body);

        /*@Multipart
        @POST("{path}")
            // Call<String> uploadImageToServer(@Path(value = "path") String path, @Part("image\"; filename=\"image.png\"") RequestBody body, String filename);
        Call<String> uploadImage(@Path(value = "path") String path, @Part MultipartBody.Part file);*/
    }
}

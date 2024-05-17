package com.example.musicplayer.api.shazam;

import com.example.musicplayer.api.shazam.models.ShazamRequestBody;
import com.example.musicplayer.api.shazam.models.ShazamResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShazamAPI {
    @POST("discovery/v5/en/US/android/-/tag/{uuidDNS}/{uuidURL}")
    Response<ShazamResponse> discovery(
            @Body ShazamRequestBody body,
            @Path("uuidDNS") String uuidDNS,
            @Path("uuidURL") String uuidURL,
            @Header("User-Agent") String userAgent,
            @Header("Content-Language") String contentLanguage,
            @Header("Content-Type") String contentType,
            @Query("sync") String sync,
            @Query("webv3") String webv3,
            @Query("sampling") String sampling,
            @Query("connected") String connected,
            @Query("shazamapiversion") String shazamapiversion,
            @Query("sharehub") String sharehub,
            @Query("video") String video
    );
}

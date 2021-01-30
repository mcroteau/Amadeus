package social.amadeus.service;

import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import social.amadeus.model.ReCaptchaInput;
import social.amadeus.model.ReCaptchaOutput;

@Service
public class ReCaptchaService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String RECAPTCHA_URI = "https://www.google.com/recaptcha/api/siteverify";

    Gson gson = new Gson();

    @Value("${recaptcha.key}")
    private String key;

    @Value("${recaptcha.secret.key}")
    private String secret;

    public boolean validates(String reCaptcha){

        ReCaptchaOutput reCaptchaOutput = null;

        try{

            OkHttpClient client = new OkHttpClient();

            ReCaptchaInput input = new ReCaptchaInput();
            input.setSecret(secret);
            input.setResponse(reCaptcha);

            String json = gson.toJson(input);

            okhttp3.RequestBody reCaptchaBody = okhttp3.RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(RECAPTCHA_URI + "?secret=" + secret + "&response=" + reCaptcha)
                    .post(reCaptchaBody)
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();
            reCaptchaOutput = gson.fromJson(body, ReCaptchaOutput.class);

        }catch(Exception e){
            e.printStackTrace();
        }

        return reCaptchaOutput.isSuccess();
    }

}

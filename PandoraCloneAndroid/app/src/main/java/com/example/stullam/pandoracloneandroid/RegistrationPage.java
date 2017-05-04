package com.example.stullam.pandoracloneandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegistrationPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
    }

    /*public void registerClient(String email, String username, String encryptedPassword) {
        try {
            GenericUrl url = new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/register"));
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

            Map<String, String> json = new HashMap<>();
            json.put("email", email);
            json.put("username", username);
            json.put("password", encryptedPassword);

            HttpContent content = new JsonHttpContent(new JacksonFactory(), json);
            HttpRequest request = requestFactory.buildPostRequest(url, content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType("application/json");
            request.setHeaders(headers);

            HttpResponse response = request.execute();
            jwt = response.parseAsString();
            System.out.println("jwt " + jwt);
        } catch (MalformedURLException e) {
            System.err.println("Register error 1");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Register error 2");
            e.printStackTrace();
        }
    }*/
}

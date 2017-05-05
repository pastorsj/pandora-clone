package com.example.stullam.pandoracloneandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    /*public void loginClient(String username, String password) {
        try {
            GenericUrl url = new GenericUrl(new URL("http://127.0.0.1:" + 8080 + "/login"));
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization(username + ":" + password);
            request.setHeaders(headers);

            HttpResponse response = request.execute();
            jwt = response.parseAsString();
            System.out.println("Response " + jwt);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Login error 1");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Login error 2");
        }
    }*/
}

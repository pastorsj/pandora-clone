package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginPage extends AppCompatActivity {

    String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button registerButton = (Button) findViewById(R.id.launch_login);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameET = (EditText) findViewById(R.id.login_email);
                EditText passwordET = (EditText) findViewById(R.id.login_password);


                String user = usernameET.getText().toString();
                String password = passwordET.getText().toString();

                System.out.println("username: " + user);
                System.out.println("login password: " + password);

                loginClient(user, password);
            }
        });
    }

    public void loginClient(String username, String password) {
        try {

            GenericUrl url = new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/login"));
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);

            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization(username + ":" + password);
            request.setHeaders(headers);

            HttpResponse response = request.execute();
            jwt = response.parseAsString();
            System.out.println("Response " + jwt);
            startSong(jwt);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Login error 1");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Login error 2");
        }
    }

    public void startSong(String jwt){
        Intent intent = new Intent(this, SongPage.class);
        intent.putExtra("token", jwt);
        intent.putExtra("ipAddress", "hello");
        startActivity(intent);
    }
}

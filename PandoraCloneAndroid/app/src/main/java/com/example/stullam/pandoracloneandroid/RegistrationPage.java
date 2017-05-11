package com.example.stullam.pandoracloneandroid;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RegistrationPage extends AppCompatActivity {

    private ClientStream cs = null;
    private Thread t;
    private String jwt;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        Button registerButton = (Button) findViewById(R.id.launch_registration);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameET = (EditText) findViewById(R.id.username);
                EditText passwordET = (EditText) findViewById(R.id.password);
                EditText emailET = (EditText) findViewById(R.id.email);
                EditText ipAddressET = (EditText) findViewById(R.id.ipAddress);


                String user = usernameET.getText().toString();
                String password = passwordET.getText().toString();
                String email = emailET.getText().toString();
                ipAddress = ipAddressET.getText().toString();

                registerClient(email, user, password);
            }
        });
    }

//    public void register(View v){
//        EditText usernameET = (EditText) findViewById(R.id.username);
//        EditText passwordET = (EditText) findViewById(R.id.password);
//        EditText emailET = (EditText) findViewById(R.id.email);
//        EditText ipAddressET = (EditText) findViewById(R.id.ipAddress);
//
//
//        String user = usernameET.getText().toString();
//        String password = passwordET.getText().toString();
//        String email = emailET.getText().toString();
//        ipAddress = ipAddressET.getText().toString();
//
//        registerClient(email, user, password);
//    }

    public void registerClient(String email, String username, String encryptedPassword) {
        try {
            GenericUrl url = new GenericUrl(new URL("http://ec2-34-224-40-124.compute-1.amazonaws.com:8080/register"));
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
            startSong(jwt);
        } catch (MalformedURLException e) {
            System.err.println("Register error 1");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Register error 2");
            e.printStackTrace();
        }
    }

    public void startSong(String jwt){
        Intent intent = new Intent(this, SongPage.class);
        intent.putExtra("token", jwt);
        intent.putExtra("ipAddress", ipAddress);
        startActivity(intent);
    }
}

package com.david.scavengerhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;

    ArrayList<String> allUsernames = new ArrayList<String>();
    ArrayList<String> allPasswords = new ArrayList<String>();

    public static boolean loggedIn = false;
    public static String TroopName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        allUsernames.add("taleat el asad");
        allUsernames.add("taleat el fahd");
        allUsernames.add("taleat el nemr");
        allUsernames.add("taleat el nesr");

        allPasswords.add("motakadem");
        allPasswords.add("motakadem");
        allPasswords.add("motakadem");
        allPasswords.add("motakadem");

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        MainActivity.sharedPreferences = getSharedPreferences("MySharedPreference", MODE_PRIVATE);
        MainActivity.editor = getSharedPreferences("MySharedPreference", MODE_PRIVATE).edit();

        loggedIn = getLoginState();
        TroopName = getLoginUsername();

        if (loggedIn && !TroopName.equals("")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

//        else {
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = editTextUsername.getText().toString().toLowerCase(); // not case sensitive
                    String password = editTextPassword.getText().toString(); // case sensitive

                    if(username == null || password == null || username.equals("") || password.equals("")){
                        Toast.makeText(LoginActivity.this, "Fill in both username and password fields.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        for(int i=0; i < allUsernames.size(); i++){
                            if(username.equals(allUsernames.get(i))){
                                if(password.equals(allPasswords.get(i))){
                                    //login successful
                                    loggedIn = true;

                                    //save logged in state to true
                                    saveLoginState(true, allUsernames.get(i));

                                    //launch MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }

                        if(!loggedIn){
                            Toast.makeText(LoginActivity.this, "Wrong username or password.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
//        }
    }

    public static void saveLoginState(boolean state, String troopName){
        loggedIn = state;
        TroopName = troopName;
        MainActivity.editor.putBoolean("loggedIn", state);
        MainActivity.editor.apply();
        MainActivity.editor.putString("TroopName", troopName);
        MainActivity.editor.apply();
    }

    public static boolean getLoginState(){
        return MainActivity.sharedPreferences.getBoolean("loggedIn", false);
    }

    public static String getLoginUsername(){
        return MainActivity.sharedPreferences.getString("TroopName", "");
    }
}

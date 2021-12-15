package com.example.mobile2021_02_grupo03.presenter;

import android.content.Intent;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.view.CreateAccountActivity;
import com.example.mobile2021_02_grupo03.view.LoginActivity;
import com.example.mobile2021_02_grupo03.view.SongListActivity;

public class LoginPresenter {
    public LoginActivity activity;

    public LoginPresenter(LoginActivity loginActivity){
        this.activity = loginActivity;
    }
    public void loginClick(){
        if(activity.layout.username.getText().toString().equals("admin") && activity.layout.password.getText().toString().equals("admin")){
            Toast.makeText(activity, "Login realizado com sucesso!",Toast.LENGTH_SHORT).show();
            SongData.isLogged = true;
            Intent intent = new Intent(activity, SongListActivity.class);
            activity.startActivity(intent);
        } else{
            Toast.makeText(activity, "Usuário ou senha incorretos!",Toast.LENGTH_SHORT).show();
        }
    }
    public void registerClick(){
        Intent intent = new Intent(activity, CreateAccountActivity.class);
        activity.startActivity(intent);
    }
}

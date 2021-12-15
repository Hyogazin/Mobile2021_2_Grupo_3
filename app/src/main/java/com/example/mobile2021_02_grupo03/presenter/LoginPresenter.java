package com.example.mobile2021_02_grupo03.presenter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.Toast;
import com.example.mobile2021_02_grupo03.model.User;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.view.CreateAccountActivity;
import com.example.mobile2021_02_grupo03.view.LoginActivity;
import com.example.mobile2021_02_grupo03.view.SongListActivity;

import java.util.ArrayList;

public class LoginPresenter {
    public LoginActivity activity;
    public LoginPresenter(LoginActivity loginActivity){
        this.activity = loginActivity;
    }

    public void loginClick(){
        if(accountExists(activity.layout.username.getText().toString(), activity.layout.password.getText().toString())){
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

    public boolean accountExists(String username, String password){
        MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
        SQLiteDatabase dbread = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.selectUser(dbread, username, username);
        if(cursor.getCount() > 0){
            return true;
        } else{
            return false;
        }
    }
}

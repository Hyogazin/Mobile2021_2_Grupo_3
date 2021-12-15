package com.example.mobile2021_02_grupo03.presenter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.model.SongData;
import com.example.mobile2021_02_grupo03.view.CreateAccountActivity;
import com.example.mobile2021_02_grupo03.view.SongListActivity;

public class CreateAccountPresenter {

    public CreateAccountActivity activity;

    public CreateAccountPresenter(CreateAccountActivity createAccountActivity){
        this.activity = createAccountActivity;

        activity.setSupportActionBar(activity.layout.toolbar);
        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void createAccountClick(){
        if(!activity.layout.createUsername.getText().toString().isEmpty() &&
                !activity.layout.createPassword.getText().toString().isEmpty() &&
                !activity.layout.confirmPassword.getText().toString().isEmpty()){
            if(activity.layout.createPassword.getText().toString().equals(activity.layout.confirmPassword.getText().toString())){
                MusicAppDBHelper dbHelper = new MusicAppDBHelper(activity.getApplicationContext());
                SQLiteDatabase dbread = dbHelper.getReadableDatabase();

                dbHelper.insertUser(dbread, MusicAppDBContract.usersTable.TABLE_NAME, activity.layout.createUsername.getText().toString(), activity.layout.createPassword.getText().toString());

                Toast.makeText(activity, "Conta criada com sucesso!",Toast.LENGTH_SHORT).show();
                SongData.isLogged = true;
                Intent intent = new Intent(activity, SongListActivity.class);
                activity.startActivity(intent);
            }else{
                Toast.makeText(activity, "Campos não coincidem!",Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(activity, "Preencha todos os campos!",Toast.LENGTH_SHORT).show();
        }

    }
}

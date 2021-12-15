package com.example.mobile2021_02_grupo03.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBContract;
import com.example.mobile2021_02_grupo03.SQLite.MusicAppDBHelper;
import com.example.mobile2021_02_grupo03.databinding.ActivityLoginBinding;
import com.example.mobile2021_02_grupo03.databinding.ActivitySongPlayerBinding;
import com.example.mobile2021_02_grupo03.model.Song;
import com.example.mobile2021_02_grupo03.presenter.LoginPresenter;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity{

    public LoginPresenter presenter;
    public ActivityLoginBinding layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = DataBindingUtil.setContentView(this, R.layout.activity_login);

        presenter = new LoginPresenter(this);

        //Username = admin
        //Password = admin

        layout.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.loginClick();
            }
        });

        layout.createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.registerClick();
            }
        });

    }


}
package com.example.mobile2021_02_grupo03.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.mobile2021_02_grupo03.R;
import com.example.mobile2021_02_grupo03.databinding.ActivityCreateAccountBinding;
import com.example.mobile2021_02_grupo03.presenter.CreateAccountPresenter;

public class CreateAccountActivity extends AppCompatActivity {

    public ActivityCreateAccountBinding layout;
    public CreateAccountPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = DataBindingUtil.setContentView(this, R.layout.activity_create_account);
        presenter = new CreateAccountPresenter(this);

        Button createBtn = findViewById(R.id.create_account_btn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.createAccountClick();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
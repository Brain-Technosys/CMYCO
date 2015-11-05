package com.braintech.cmyco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.braintech.cmyco.R;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MasterPageActivity extends AppCompatActivity {

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    SnakeOnClick snakeOnClick;

    CommonAPI logoutAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_page);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logoutAPI = new CommonAPI(this);

        handleSnakeRetryCall();

    }

    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClick, coordinatorLayout);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logoutAPI.logout(snakeOnClick, coordinatorLayout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_continue)
    void goNext() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


}

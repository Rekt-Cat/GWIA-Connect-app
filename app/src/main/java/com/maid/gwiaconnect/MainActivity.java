package com.maid.gwiaconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout draw;
    AppUpdateManager appUpdateManager;
    private static final int REQ_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUpdateManager= AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if(appUpdateInfo.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE
                        &&appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.FLEXIBLE,MainActivity.this,REQ_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d("Update", ""+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

        });
        appUpdateManager.registerListener(installStateUpdatedListene);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        draw=findViewById(R.id.drawLayout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, draw,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        draw.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(savedInstanceState==null){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new feed_fragment()).commit();
        navigationView.setCheckedItem(R.id.nav_view);
        }




    }
    @Override
    public void onBackPressed() {

        if(draw.isDrawerOpen(GravityCompat.START)){
            draw.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){

           case R.id.feeds:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new feed_fragment()).commit();
               break;
           case R.id.news:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new news_fragment()).addToBackStack(null).commit();
               break;
           case R.id.academics:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new academics_fragment()).commit();
               break;
           case R.id.wallet:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new wallet_fragment()).commit();
               break;
           case R.id.settings:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new settings_fragment()).commit();
               break;
           case R.id.myProfile:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new myProfile_fragment()).commit();
               break;

       }
       draw.closeDrawer(GravityCompat.START);

        return true;
    }
    private InstallStateUpdatedListener installStateUpdatedListene= new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if(installState.installStatus()== InstallStatus.DOWNLOADED){
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"New app is ready!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onStop() {
        if(appUpdateManager!=null){
            appUpdateManager.unregisterListener(installStateUpdatedListene);
        }
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQ_CODE && requestCode!=RESULT_OK){
            Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
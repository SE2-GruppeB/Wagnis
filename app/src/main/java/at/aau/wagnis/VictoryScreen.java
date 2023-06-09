package at.aau.wagnis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class VictoryScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.win_screen);
        Button buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(view -> {
            Intent intent = new Intent(VictoryScreen.this,MenuActivity.class);
            startActivity(intent);
        });

    }
}
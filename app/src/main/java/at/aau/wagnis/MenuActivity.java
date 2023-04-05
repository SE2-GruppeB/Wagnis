package at.aau.wagnis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button start;
    Button sources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        hideNavigationBar();

        sources = findViewById(R.id.btn_sources);
        sources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSources(sources);

            }
        });
        start = findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFighterPopUp(start);

            }
        });
    }

    public void hideNavigationBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    private void changeActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }
    public void showSources(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_sources, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        int width = (int)px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false;
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        Button btnClose = popUp.findViewById(R.id.btn_Close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });
    }


    public void chooseFighterPopUp(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_fighter, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        int width = (int)px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnAccept = popUp.findViewById(R.id.btn_Accept);
        RadioGroup rg = popUp.findViewById(R.id.radio);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton selectedTeam=popUp.findViewById(rg.getCheckedRadioButtonId());
                GlobalVariables.setAgency(selectedTeam.getText().toString());
                Toast.makeText(MenuActivity.this, "Agency: "+GlobalVariables.getAgency(), Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                changeActivity();
                return;
            }
        });
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    public void goToAppIcon (View view) {
        goToUrl ( "https://icons8.de");
    }
    public void goToBackground (View view) {
        goToUrl ( "https://www.vecteezy.com/vector-art/17535964-mars-landscape-with-craters-and-red-rocky-surface");
    }
    public void goToSurface (View view) {
        goToUrl ( "https://www.vecteezy.com/vector-art/13280678-moon-surface-seamless-background-with-craters");
    }

}
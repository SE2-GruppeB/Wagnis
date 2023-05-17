package at.aau.wagnis;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import at.aau.wagnis.application.GameManager;
import at.aau.wagnis.application.WagnisApplication;

public class MenuActivity extends AppCompatActivity {

    Button hostBtn;
    Button sourcesBtn;
    Button joinBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        GlobalVariables.mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.music1);
        GlobalVariables.mediaPlayer.start();
        GlobalVariables.mediaPlayer.setLooping(true);

        sourcesBtn = findViewById(R.id.btn_sources);
        sourcesBtn.setOnClickListener(view -> showSources(sourcesBtn));

        hostBtn = findViewById(R.id.btn_start);
        hostBtn.setOnClickListener(view -> handleNetwork(true));

        joinBtn = findViewById(R.id.btn_join);
        joinBtn.setOnClickListener(view -> handleNetwork(false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGameManager().setConnectionStateListener(newConnectionState -> runOnUiThread(() -> {
            switch (newConnectionState) {
                case CONNECTING:
                    Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTED:
                    changeActivity();
                    break;
                default:
                    break;
            }
        }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        getGameManager().setConnectionStateListener(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
           getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void changeActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }

    private void handleNetwork(boolean host){
        if(host){
            GlobalVariables.setIsClient(false);
            new Thread(getGameManager()::startNewGame).start();
        }else{
            GlobalVariables.setIsClient(true);
            getHostIp();
        }
    }
    public void getHostIp() {
        try {
            throw new Exception();
            //readQrCode();

        } catch (Exception e) {

            PopupWindow popupWindow= createPopUp(R.layout.popup_connect);
            popupWindow.showAtLocation(new View(getApplicationContext()), Gravity.CENTER, 0, 0);

            Button btnConnect = popupWindow.getContentView().findViewById(R.id.btn_connect);
            EditText hostIP = popupWindow.getContentView().findViewById(R.id.txtIP);
            btnConnect.setOnClickListener(view -> {
                popupWindow.dismiss();
                new Thread(() -> getGameManager().joinGameByServerAddress(hostIP.getText().toString())).start();
            });
        }
    }
    private void readQrCode(){
        IntentIntegrator ig11 = new IntentIntegrator(this);
        ig11.setOrientationLocked(true);
        ig11.setPrompt("Scan a QR Code");
        ig11.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> getGameManager().joinGameByServerAddress(intentResult.getContents())).start();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void showSources(View view) {

        PopupWindow popupWindow= createPopUp(R.layout.popup_sources);
        popupWindow.showAtLocation(new View(getApplicationContext()), Gravity.CENTER, 0, 0);
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private GameManager getGameManager() {
        return ((WagnisApplication) getApplication()).getGameManager();
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
    public void goToUI (View view) {
        goToUrl ( "https://www.vecteezy.com/vector-art/21604946-futuristic-vector-hud-interface-screen-design-digital-callouts-titles-hud-ui-gui-futuristic-user");
    }

    public PopupWindow createPopUp(int popupId){

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        PopupWindow popupWindow = new PopupWindow(popUp, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        return popupWindow;
    }


}


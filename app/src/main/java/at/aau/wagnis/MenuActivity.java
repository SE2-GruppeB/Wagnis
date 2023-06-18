package at.aau.wagnis;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import java.net.Inet4Address;
import java.net.InetAddress;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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

        GlobalVariables.setMediaPlayer(MediaPlayer.create(this.getApplicationContext(), R.raw.music2));
        GlobalVariables.getMediaPlayer().start();
        GlobalVariables.getMediaPlayer().setLooping(true);

        sourcesBtn = findViewById(R.id.btn_sources);
        sourcesBtn.setOnClickListener(view -> showSources());

        hostBtn = findViewById(R.id.btn_start);
        hostBtn.setOnClickListener(view -> handleNetwork(true));

        joinBtn = findViewById(R.id.btn_join);
        joinBtn.setOnClickListener(view -> handleNetwork(false));

        GlobalVariables.setLocalIpAddress(getIpAddress());
    }

    private String getIpAddress() {
        ConnectivityManager manager = getSystemService(ConnectivityManager.class);
        Network network = manager.getActiveNetwork();
        LinkProperties prop = manager.getLinkProperties(network);
        for (LinkAddress linkAddress : prop.getLinkAddresses()){
            InetAddress inetAddress = linkAddress.getAddress();
            if(inetAddress instanceof Inet4Address){
                return "1";

            }
        }
        return "wrong";
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalVariables.getMediaPlayer().start();
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
        GlobalVariables.getMediaPlayer().pause();
        getGameManager().setConnectionStateListener(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            windowInsetsController.setSystemBarsBehavior( WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
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
            readQrCode();

        } catch (Exception e) {
            popupIp();
        }
    }

    private void popupIp(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_connect);
        popupWindow.showAtLocation(new View(getApplicationContext()), Gravity.CENTER, 0, 0);

        Button btnConnect = popupWindow.getContentView().findViewById(R.id.btn_connect);
        EditText hostIP = popupWindow.getContentView().findViewById(R.id.txtIP);
        btnConnect.setOnClickListener(view -> {
            GlobalVariables.setHostIP(hostIP.getText().toString());
            popupWindow.dismiss();
            new Thread(() -> getGameManager().joinGameByServerAddress(hostIP.getText().toString())).start();
        });
    }
    private void readQrCode(){
        IntentIntegrator ig11 = new IntentIntegrator(this);
        ig11.setOrientationLocked(true);
        ig11.setPrompt("Scan a QR Code");
        ig11.initiateScan();
    }
    /**
     * @deprecated
     */
    @Deprecated
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
                popupIp();
            } else {
                String ip = intentResult.getContents();
                GlobalVariables.setHostIP(ip);
                new Thread(() -> getGameManager().joinGameByServerAddress(ip)).start();
            }
        } else {
            popupIp();
        }
    }



    public void showSources() {
        PopupWindow popupWindow= createPopUp(R.layout.popup_sources);
        popupWindow.showAtLocation(new View(getApplicationContext()), Gravity.CENTER, 0, 0);
        Button appIcon = popupWindow.getContentView().findViewById(R.id.btn_AppIcon);
        appIcon.setOnClickListener(view -> goToAppIcon());
        Button dome = popupWindow.getContentView().findViewById(R.id.btn_dome);
        dome.setOnClickListener(view -> goToAppIcon());
        Button background = popupWindow.getContentView().findViewById(R.id.btn_background);
        background.setOnClickListener(view -> goToBackground());
        Button surface = popupWindow.getContentView().findViewById(R.id.btn_surfaceBackground);
        surface.setOnClickListener(view -> goToSurface());
        Button ui = popupWindow.getContentView().findViewById(R.id.btn_surfaceBackground2);
        ui.setOnClickListener(view -> goToUI());
        Button music = popupWindow.getContentView().findViewById(R.id.btn_music);
        music.setOnClickListener(view -> goToMusic());
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private GameManager getGameManager() {
        return ((WagnisApplication) getApplication()).getGameManager();
    }

    public void goToAppIcon () {
        goToUrl ( "https://icons8.de");
    }
    public void goToBackground () {
        goToUrl ( "https://www.vecteezy.com/vector-art/17535964-mars-landscape-with-craters-and-red-rocky-surface");
    }
    public void goToSurface () {
        goToUrl ( "https://www.vecteezy.com/vector-art/13280678-moon-surface-seamless-background-with-craters");
    }
    public void goToUI () {
        goToUrl ( "https://www.vecteezy.com/vector-art/21604946-futuristic-vector-hud-interface-screen-design-digital-callouts-titles-hud-ui-gui-futuristic-user");
    }
    public void goToMusic () {
        goToUrl ( "https://pixabay.com/music/suspense-space-ambient-sci-fi-121842/");
    }

    public PopupWindow createPopUp(int popupId){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        return new PopupWindow(popUp, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
    }
}


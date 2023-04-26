package at.aau.wagnis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import at.aau.wagnis.server.communication.command.ProcessChatMessageCommand;
import at.aau.wagnis.server.communication.connection.ClientConnectionBus;
import at.aau.wagnis.server.communication.connection.ClientConnectionBusImpl;
import at.aau.wagnis.server.communication.connection.ClientConnectionListener;
import at.aau.wagnis.server.communication.connection.NetworkClientConnection;
import at.aau.wagnis.server.communication.connection.NetworkServerConnection;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MenuActivity extends AppCompatActivity {

    private static final int DEMO_PORT = 54321;
    private static final String EMULATOR_HOST_ADDRESS = "10.0.2.2";
    private static final String DEMO_SERVER_TAG = "DemoServer";
    private static final String DEMO_CLIENT_TAG = "DemoClient";

    Button hostBtn;
    Button sourcesBtn;
    Button joinBtn;

    Thread demoThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        hideNavigationBar();

        sourcesBtn = findViewById(R.id.btn_sources);
        sourcesBtn.setOnClickListener(view -> showSources(sourcesBtn));

        hostBtn = findViewById(R.id.btn_start);
        hostBtn.setOnClickListener(view -> {
            if (demoThread == null) {
                demoThread = new Thread(this::createNetworkingDemoServer);
                demoThread.start();
            }

            chooseFighterPopUp(hostBtn);
        });

        joinBtn = findViewById(R.id.btn_join);
        joinBtn.setOnClickListener(view -> {
            if (demoThread == null) {
                demoThread = new Thread(this::createNetworkingDemoClient);
                demoThread.start();
            }

            joinGame(joinBtn);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (demoThread != null) {
            demoThread.interrupt();
        }
    }

    public void hideNavigationBar() {
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
        int width = (int) px;
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
        int width = (int) px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnAccept = popUp.findViewById(R.id.btn_Accept);
        RadioGroup rg = popUp.findViewById(R.id.radio);
        setUnavailableAgencis(rg);


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

    public static void setUnavailableAgencis(RadioGroup rg){
        for(int i = 0;i<rg.getChildCount();i++){
            RadioButton rb = (RadioButton) rg.getChildAt(i);
            for(String s : GlobalVariables.getUnavailableAgencies()){
                if(rb.getText().toString().equals(s)){
                    rb.setEnabled(false);
                }
            }
        }
    }
    public void joinGame(View view) {
        try {
            readQrCode();

        } catch (Exception e) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_connect, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        int width = (int) px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnConnect = popUp.findViewById(R.id.btn_connect);
        EditText hostIP = popUp.findViewById(R.id.txtIP);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariables.setHostIP(hostIP.getText().toString());
                GlobalVariables.setIsClient(true);
                popupWindow.dismiss();
                chooseFighterPopUp(joinBtn);
                return;
            }
        });
    }

        //TODO: Initiate Server Connection

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
                GlobalVariables.setHostIP(intentResult.getContents());
                GlobalVariables.setIsClient(true);
                chooseFighterPopUp(joinBtn);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createNetworkingDemoClient() {
        Log.i(DEMO_CLIENT_TAG, "Connecting...");
        try (Socket clientSocket = new Socket(EMULATOR_HOST_ADDRESS, DEMO_PORT)) {
            Log.i(DEMO_CLIENT_TAG, "Connected");

            NetworkServerConnection serverConnection = NetworkServerConnection.fromSocket(
                    clientSocket,
                    Thread::new,
                    command -> Log.i(DEMO_CLIENT_TAG, command.toString())
            );

            serverConnection.start();

            while(!Thread.interrupted()) {
                serverConnection.send(new ProcessChatMessageCommand("Hello from client"));
                //noinspection BusyWait
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            Log.e(DEMO_CLIENT_TAG, "IOException while running demo client", e);
        }
    }

    private void createNetworkingDemoServer() {
        Log.i(DEMO_SERVER_TAG, "Starting demo server");
        ClientConnectionBus bus = new ClientConnectionBusImpl();

        try (ServerSocket serverSocket = new ServerSocket(DEMO_PORT)) {
            ClientConnectionListener listener = new ClientConnectionListener(
                    serverSocket,
                    bus,
                    NetworkClientConnection::fromSocket,
                    Thread::new
            );
            listener.start();

            while (!Thread.interrupted()) {
                Log.i(DEMO_SERVER_TAG, bus.getNextCommand().toString());
                bus.broadcastCommand(new ProcessChatMessageCommand("Hello from Server"));
            }

        } catch (IOException e) {
            Log.e(DEMO_SERVER_TAG, "IOException while running demo server", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            bus.close();
        }

        Log.i(DEMO_SERVER_TAG, "Closed demo server");
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


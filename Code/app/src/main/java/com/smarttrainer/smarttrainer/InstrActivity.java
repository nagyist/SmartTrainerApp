package com.smarttrainer.smarttrainer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.FilledButton;
import com.microsoft.band.tiles.pages.FilledButtonData;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;
import com.smarttrainer.smarttrainer.models.GetByID;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;

public class InstrActivity extends AppCompatActivity {
    TextToSpeech tts;
    private TextView txtStatus;
    private BandClient client = null;
    private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instr);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.instr_toolbar);
        setSupportActionBar(myToolbar);

        if(Build.VERSION.SDK_INT >= 21) {
            // set statusBar
            Window window = this.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(Color.parseColor("#a20000"));
        }

        Bundle b = getIntent().getExtras();
        final int id = b.getInt("ID");

        TextView exerName = (TextView) findViewById(R.id.exer_name);
        exerName.setText(GetByID.getExerName(id));

        GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_view);
        if (id == 1)
            gifImageView.setImageResource(R.drawable.curl);

        Button procedure = (Button) findViewById(R.id.prcd_btn);
        procedure.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                DialogFragment newFragment = new ProcedureFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        Button start = (Button) findViewById(R.id.start_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }
                Intent toExer = new Intent();
                toExer.putExtra("ID", id);
                toExer.setClass(InstrActivity.this, ExerActivity.class);
                startActivity(toExer);
            }
        });

        txtStatus = (TextView) findViewById(R.id.band);
        new AddTileWaitPress().execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent() != null && getIntent().getExtras() != null){
            processIntent(getIntent());
        }
    }

    private class AddTileWaitPress extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            txtStatus.setText("");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    if (addTile()) {
                        updatePages();
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                    return false;
                }
            } catch (BandException e) {
                handleBandException(e);
                return false;
            } catch (Exception e) {
                appendToUI(e.getMessage());
                return false;
            }

            return true;
        }
    }

    private void processIntent(Intent intent){
        String extraString = intent.getStringExtra("new Intent");

        if(extraString != null && extraString.equals("TileEventReceiver")){
            if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {
                TileEvent tileOpenData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                appendToUI("Tile open event received\n" + tileOpenData.toString() + "\n\n");
            } else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED)
            {
                TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                appendToUI("Button event received\n" + buttonData.toString() + "\n\n");

                if (tts != null) {
                    tts.stop();
                    tts.shutdown();
                }
                Intent toExer = new Intent();
                toExer.setClass(InstrActivity.this, ExerActivity.class);
                startActivity(toExer);
            }
            /*else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {
                TileEvent tileCloseData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                appendToUI("Tile close event received\n" + tileCloseData.toString() + "\n\n");
            }*/
        }
    }

    private boolean addTile() throws Exception {
        if (doesTileExist()) {
            Log.d("addTile", "Tile exists");
            return true;
        }

		/* Set the options */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.white, options);

        BandTile tile = new BandTile.Builder(tileId, "Smartrainer", tileIcon)
                .setPageLayouts(createButtonLayout())
                .build();
        appendToUI("Button Tile is adding ...\n");
        if (client.getTileManager().addTile(this, tile).await()) {
            appendToUI("Button Tile is added.\n");
            return true;
        } else {
            appendToUI("Unable to add button tile to the band.\n");
            return false;
        }
    }

    private void updatePages() throws BandIOException {
        client.getTileManager().setPages(tileId,
                new PageData(pageId1, 0)
                        .update(new FilledButtonData(12, Color.YELLOW))
                        .update(new TextButtonData(21, "Start")));
        appendToUI("Press black button on band to start \n\n");
    }

    private PageLayout createButtonLayout() {
        return new PageLayout(
                new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL)
                        .addElements(new FilledButton(0, 5, 210, 45).setMargins(0, 5, 0, 0).setId(12).setBackgroundColor(Color.RED))
                        .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0).setId(21).setPressedColor(Color.BLUE)));
    }

    private boolean doesTileExist() throws BandIOException, InterruptedException, BandException {
        List<BandTile> tiles = client.getTileManager().getTiles().await();
        for (BandTile tile : tiles) {
            if (tile.getTileId().equals(tileId)) {
                return true;
            }
        }
        return false;
    }


    private void handleBandException(BandException e) {
        String exceptionMessage = "";
        switch (e.getErrorType()) {
            case DEVICE_ERROR:
                exceptionMessage = "Please make sure bluetooth is on and the band is in range.\n";
                break;
            case UNSUPPORTED_SDK_VERSION_ERROR:
                exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                break;
            case SERVICE_ERROR:
                exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                break;
            case BAND_FULL_ERROR:
                exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.\n";
                break;
            default:
                exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                break;
        }
        appendToUI(exceptionMessage);
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        }
        else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.instr_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.play_sound){
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                        String toSpeak = "Lie on your back with your feet flat on the floor. If" +
                                "your feet don’t reach the floor, use a stable board to" +
                                "accommodate size. Grasp the barbell with a wider" +
                                "than shoulder-width grip, wrapping thumbs around" +
                                "the bar. Hold the barbell at arm’s length above your" +
                                "upper-chest area.";
                        tts.setSpeechRate((float) 0.7);
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
        else if (id == R.id.mute && tts != null) {
            tts.stop();
            tts.shutdown();
        }
        return true;
    }

    public void onDestroy()
    {
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

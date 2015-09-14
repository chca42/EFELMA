package org.schabi.efelma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private PlussyLedView plussyView;
    private ColorSeek colorSeek;
    private ColorSeek intesitySeek;
    private ColorSeek brightnessSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plussyView = (PlussyLedView) findViewById(R.id.plussyView);
        plussyView.setMapping(new int[] {
                         0, 1,
                         2, 3,
                4,   5,  6,  7,  8,  9,
                10, 11, 12, 13, 14, 15,
                        16, 17,
                        18, 19

        });

        colorSeek = (ColorSeek) findViewById(R.id.colorSeek);
        intesitySeek = (ColorSeek) findViewById(R.id.intensitySeek);
        brightnessSeek = (ColorSeek) findViewById(R.id.brightnessSeek);
        colorSeek.setOnColourSelectionChangeListener(new ColorSeek.OnColourSelectionChangeListener() {
            @Override
            public void selectionChanged(int color) {
                intesitySeek.setBaseColor(color);
            }
        });
        intesitySeek.setOnColourSelectionChangeListener(new ColorSeek.OnColourSelectionChangeListener() {
            @Override
            public void selectionChanged(int color) {
                brightnessSeek.setBaseColor(color);
            }
        });
        brightnessSeek.setOnColourSelectionChangeListener(new ColorSeek.OnColourSelectionChangeListener() {
            @Override
            public void selectionChanged(int color) {
                plussyView.setColourAtCursor(color);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


}

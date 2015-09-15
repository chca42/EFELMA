package org.schabi.efelma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Christian Schabesberger on 13.09.15.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * org.schabi.efelma.MainActivity.java is part of EFELMA.
 *
 * EFELMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EFELMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EFELMA.  If not, see <http://www.gnu.org/licenses/>.
 */

public class MainActivity extends AppCompatActivity {

    private PlussyDisplay plussyDisplay;
    private PlussyLedView plussyView;
    private ColorSeek colorSeek;
    private ColorSeek intesitySeek;
    private ColorSeek brightnessSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plussyDisplay = new PlussyDisplay();
        plussyView = (PlussyLedView) findViewById(R.id.plussyView);
        plussyView.setMapping(new int[] {
                         0x13, 0x12,
                         0x10, 0x11,
                0xf, 0xe, 0xd, 0xc, 0xb, 0xa,
                0x4, 0x5, 0x6, 0x7, 0x8, 0x9,
                          0x3, 0x2,
                          0x0, 0x1

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
        plussyView.setOnLedChangedListener(new PlussyLedView.OnLedChangedListener() {
            @Override
            public void onChange(int led, int color) {
                plussyDisplay.setLed(led, color);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        plussyDisplay.startNetworking();
    }

    @Override
    public void onStop() {
        super.onStop();
        plussyDisplay.stopNetworking();
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

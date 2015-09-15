package org.schabi.efelma;

import android.accounts.NetworkErrorException;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Christian Schabesberger on 14.09.15.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * PlussyDisplay.java is part of EFELMA.
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

public class PlussyDisplay {
    private static final String TAG = PlussyDisplay.class.toString();
    private static final String BROADCAST_IP = "255.255.255.255";
    private static final int UDP_PORT = 60000;
    private static final int TCP_PORT = 60000;

    private volatile boolean run = false;
    private Thread networkTread;
    private OnLedChangedListener onLedChangedListener = null;

    PrintWriter out;
    BufferedReader in;

    public interface OnLedChangedListener{
        void onChange(int led, int color);
    }

    class ReplyRunnable implements Runnable {
        String message;
        public ReplyRunnable(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            switch(message) {
                case "":
                    Log.e(TAG, "ERROR: Message from Server is empty.");
                    break;
                case "?":
                    Log.e(TAG, "ERROR: wrong command send to server");
                    break;
                case "M":
                    break;
                default:
                    Log.e(TAG, "ERROR: can't handle command: " + message);
            }
        }
    }

    class NetworkRunnable implements Runnable {
        Handler handler = new Handler();
        private InetAddress listenToSetupBroadcast() {
            byte[] rBuf = new byte[50];
            InetAddress serverAddress = null;
            DatagramSocket socket = null;
            try {
                InetAddress broadcastAddress = InetAddress.getByName(BROADCAST_IP);
                socket = new DatagramSocket(UDP_PORT, broadcastAddress);
                socket.setSoTimeout(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (run && serverAddress == null) {
                DatagramPacket packet = new DatagramPacket(rBuf, rBuf.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData()).trim();
                    if(message.contains("plussyDisplay")) {
                        serverAddress = InetAddress.getByName(
                                packet.getAddress().getHostAddress());
                    }
                } catch (InterruptedIOException e) {
                    Log.e(TAG, "Error: Timeout while waiting for setup packet.");
                } catch (UnknownHostException e) {
                    Log.e(TAG, "Error: Host not known.");
                } catch (IOException e) {
                    Log.e(TAG, "Error: could not recieve setup packet");
                }
            }
            socket.close();
            return serverAddress;
        }

        private void connect(InetAddress serverAddress) {
            String message = "";
            Socket socket = null;
            try {
                socket = new Socket(serverAddress, TCP_PORT);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch(Exception e) {
                Log.e(TAG, "Error could not set up connection to server.");
            }

            while(run) {
                try {
                    message = in.readLine();
                } catch (Exception e) {
                    Log.e(TAG, "Error reading from server");
                }
                handler.post(new ReplyRunnable(message));
            }
            try {
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            InetAddress serverAddress = listenToSetupBroadcast();
            connect(serverAddress);
        }
    }

    public void startNetworking() {
        run = true;
        networkTread = new Thread(new NetworkRunnable());
        networkTread.start();
    }

    public void stopNetworking() {
        run = false;
        try {
            networkTread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLed(int led, int color) {
        if(led >= 20) {
            Log.e(TAG, "Led " + Integer.toString(led) + " not known.");
            return;
        }
        String ledS = Integer.toHexString(led);
        if (ledS.length() < 2) {
            ledS = "0" + ledS;
        }
        String command = "m" + ledS + Integer.toHexString(color).substring(2, 8);
        out.println(command);
        out.flush();
    }
}

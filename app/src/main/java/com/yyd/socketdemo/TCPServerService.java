package com.yyd.socketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    public static final int PORT = 8888;
    private boolean mIsDestroy;
    private String[] mDefinedMessages = new String[]{
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
    };

    public TCPServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
        mIsDestroy = false;
        new Thread(new TCPServer()).start();
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestroy");
        mIsDestroy = true;
        super.onDestroy();
    }

    private class TCPServer implements Runnable {
        @Override
        public void run() {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (!mIsDestroy) {
                try {
                    final Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("response client");
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        System.out.println("response client2");
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        // 注意autoFlush要设为true
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        out.println("Welcome");
        while(!mIsDestroy) {
            String str = in.readLine();
            if (str == null) {
                break;
            }
            System.out.println("msg from client: " + str);
            int i = new Random().nextInt(mDefinedMessages.length);
            String msg = mDefinedMessages[i];
            out.println(msg);
            System.out.println("send: " + msg);
        }
        System.out.println("client quit.");
        CloseUtils.closeIO(in);
        CloseUtils.closeIO(out);
        client.close();
    }



}

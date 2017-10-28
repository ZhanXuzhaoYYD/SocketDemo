package com.yyd.socketdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class TCPClientActivity extends AppCompatActivity {
    public static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    public static final int MESSAGE_SOCKET_CONNECTED = 2;
    private MyHandler mHandler = new MyHandler(new WeakReference<TCPClientActivity>(this));
    private Socket mClientSocket = null;
    private PrintWriter mPrintWriter;

    private static class MyHandler extends Handler {
        private WeakReference<TCPClientActivity> mActivityWeakReference;

        private Activity getActivity() {
            return mActivityWeakReference == null ? null : mActivityWeakReference.get();
        }

        MyHandler(WeakReference<TCPClientActivity> activityWeakReference) {
            super();
            mActivityWeakReference = activityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    String str = (String) msg.obj;
                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_SOCKET_CONNECTED:

                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        initView();
        startTCPServerService();
        connectSocket();
    }

    private void initView() {
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrintWriter != null) {
                    String msg = "hello";
                    sendMsg(msg);
                }
            }
        });
    }

    private void connectSocket() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                connectTCPServer();
            }
        }.start();
    }

    private void sendMsg(final String msg) {
        new Thread() {
            @Override
            public void run() {
                mPrintWriter.println(msg);
                System.out.println("send msg: " + msg);
            }
        }.start();

    }

    private void startTCPServerService() {
        startService(new Intent(this, TCPServerService.class));
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownOutput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", TCPServerService.PORT);
                mClientSocket = socket;
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                System.out.println("connect server success");
            } catch (IOException e) {
                e.printStackTrace();
                SystemClock.sleep(1000);
                System.out.println("connect server failed, retry...");
            }
        }
        try {
            // 注意autoFlush要设为true
            mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!isFinishing()) {
                String msg = br.readLine();
                System.out.println("receive: " + msg);
                if (msg != null) {
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, msg)
                            .sendToTarget();
                }
            }
            System.out.println("quite...");
            CloseUtils.closeIO(mPrintWriter);
            CloseUtils.closeIO(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

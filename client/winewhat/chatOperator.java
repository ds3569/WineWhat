package com.kpu.winewhat;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class chatOperator extends Service {
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;
    private Socket client;
    static int orderCode = 0;

    static String userID;

    private String SERVER_IP = "182.209.238.112";
    //private String SERVER_IP = "192.168.219.106";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            return Service.START_STICKY;
        }
        else {
            if (client == null){
                ChatOperator chatOperator = new ChatOperator();
                chatOperator.execute();
            }
            else {
                if(orderCode == 1){
                    String text = "Login" + intent.getStringExtra("user_info");
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
                else if (orderCode == 2){
                    String text = "Register" + intent.getStringExtra("user_info");
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
                else if (orderCode == 3){
                    String text = intent.getStringExtra("msg");
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
                else if (orderCode == 4) {
                    String text = intent.getStringExtra("msg") + userID;
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
                else if (orderCode == 5){
                    String text = "Review" + userID + ", " + intent.getStringExtra("wine_ID") + ", " + intent.getStringExtra("point");
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
                else if (orderCode == 6){
                    String text = "Info" + intent.getStringExtra("info");
                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }
                else if (orderCode == 7){
                    String text = intent.getStringExtra("msg") + userID;

                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text);
                    } else {
                        messageSender.execute(text);
                    }
                }



            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                client = new Socket(SERVER_IP, 9999); // Creating the server socket.
                if (client != null) {
                    //자동 flushing 기능이 있는 PrintWriter 객체를 생성한다.
                    //client.getOutputStream() 서버에 출력하기 위한 스트림을 얻는다.
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    //입력 스트림 inputStreamReader에 대해 기본 크기의 버퍼를 갖는 객체를 생성한다.
                    bufferedReader = new BufferedReader(inputStreamReader);
                    android.util.Log.i("chatOperator", "Connected");
                } else {
                    android.util.Log.i("chatOperator", "Server has not bean started on port 9999.");
                }

            } catch (UnknownHostException e) {
                android.util.Log.i("chatOperator", "Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            } catch (IOException e) {
                android.util.Log.i("chatOperator", "Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            }

            return null;
        }

       @Override
        protected void onPostExecute(Void result) {
            if (client != null) {
                Receiver receiver = new Receiver(); // Initialize chat receiver AsyncTask.
                receiver.execute();
            }
        }
    }

    private class Receiver extends AsyncTask<Void, Void, Void> {
        private String message;

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                try {
                    //스트림으로부터 읽어올수 있으면 true를 반환한다.
                    if (bufferedReader.ready()) {
                        //'\n', '\r'을 만날 때까지 읽어온다.(한줄을 읽어온다.)
                        message = bufferedReader.readLine();
                        publishProgress(null);


                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
            }
        }

        //publishProgress(null)에 의해서 호출된다. '
        //서버에서 전달받은 문자열을 읽어서 화면에 출력해준다.

        @Override
        protected void onProgressUpdate(Void... values) {
            if (orderCode == 1){
                Intent msg = new Intent(getApplicationContext(), login.class);
                msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                msg.putExtra("message", message);
                startActivity(msg);
            }
            else if (orderCode == 2) {
                Intent msg = new Intent(getApplicationContext(), register.class);
                msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                msg.putExtra("message", message);
                startActivity(msg);
            }
            else if (orderCode == 3){
                Intent msg = new Intent(getApplicationContext(), wineInfo.class);
                msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                msg.putExtra("message", message);
                startActivity(msg);
            }
            else if (orderCode == 4){
                Intent msg = new Intent(getApplicationContext(), mainPage.class);
                msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                msg.putExtra("message", message);
                startActivity(msg);
            }
            else if (orderCode == 6){
                if (message.equals("failed")){
                    Toast myToast = Toast.makeText(getApplicationContext(), "다시 시도해 주세요", Toast.LENGTH_SHORT);
                    myToast.show();
                }
                else {
                    Intent msg = new Intent(getApplicationContext(), mainPage.class);
                    msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    msg.putExtra("message", message);
                    startActivity(msg);
                }
            }
            else if (orderCode == 7){
                Intent msg = new Intent(getApplicationContext(), myPage.class);
                msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                msg.putExtra("message", message);
                startActivity(msg);
            }

        }
    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */

   private class Sender extends AsyncTask<String, String, Void> {
        private String message;

        @Override
        protected Void doInBackground(String... params) {
            message = params[0];
            //문자열을 스트림에 기록한다.
            printwriter.write(message + "\n");
            //스트림을 플러쉬한다.
            printwriter.flush();
            return null;
        }
    }

}

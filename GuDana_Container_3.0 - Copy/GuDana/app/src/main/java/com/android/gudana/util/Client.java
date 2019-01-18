package com.android.gudana.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, String> {

    String dstAddress;
    int dstPort;
    String response = "";
    String message_to_send;
    Socket socket;
    InputStream inputStream;
    BufferedReader reader;
    OutputStream DataOutput;
    PrintWriter writer;
    Context context;

    public Client(Context context , String addr, int port, String message) {
        this.context = context;
        dstAddress = addr;
        dstPort = port;
        this.message_to_send = message;
    }

    public void  SendData(String msg){


        try {
            this.writer.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... arg0) {

        // Socket socket = null;

        try {
            this.socket = new Socket(dstAddress, dstPort);


            inputStream = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(inputStream));

            DataOutput = socket.getOutputStream();
            this.writer = new PrintWriter(DataOutput, true);

            if(this.socket != null){

                        String ResponseServer = "";
                        this.writer.println("ID_AST#"+this.message_to_send.toString()+"#end");
                        ResponseServer = reader.readLine();
                        Toast.makeText(this.context, ResponseServer.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            Thread.sleep(1000);
                            Log.d(ResponseServer.toString(), ResponseServer.toString());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    /*

                    while(this.socket != null){

                        this.writer.println("ID_AST=40012");
                        ResponseServer = reader.readLine();
                        Toast.makeText(this.context, ResponseServer.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            Thread.sleep(1000);
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
            */
            }else{
                Toast.makeText(this.context, "  error connection  with remote Server ", Toast.LENGTH_SHORT).show();

            }


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }



    @Override
    protected void onPostExecute(String result) {
        // textResponse.setText(response);
        super.onPostExecute(result);
    }

}

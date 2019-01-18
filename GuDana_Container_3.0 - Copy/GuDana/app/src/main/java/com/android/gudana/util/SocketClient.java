package com.android.gudana.util;

import java.io.IOException;
import java.net.Socket;

public class SocketClient {

  private String AdresseServer;
  private int PortServer;
  private String msg;

    public SocketClient(String adresseServer, int portServer) {
        AdresseServer = adresseServer;
        PortServer = portServer;
        // construct the connection with server

        try {
            Socket socket = new Socket(this.AdresseServer, this.PortServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // send the data trhought the socket connection




}

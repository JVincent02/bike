package com.example.bike;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessageSender extends AsyncTask<String, Void, Void>
{
    DatagramSocket datagramSocket;
    String ip;
    public MessageSender(DatagramSocket datagramSocket, String ip){
        this.datagramSocket = datagramSocket;
        this.ip=ip;
}
    @Override
    protected Void doInBackground(String... voids) {

        byte[] message = voids[0].getBytes();
        try
        {
            InetAddress inetAddress = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(message,message.length,inetAddress,8888);
            datagramSocket.send(packet);

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

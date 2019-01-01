package com.team3316.bugeyed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DBugNetwork {
    /*
     * Singleton stuff
     */
    private static DBugNetwork _instance = null;
    public static DBugNetwork getInstance() throws SocketException, UnknownHostException {
        if (_instance == null)
            _instance = new DBugNetwork();
        return _instance;
    }

    private DatagramSocket _socket;
    private InetAddress _addres;
    private byte[] _buffer;

    private DBugNetwork() throws SocketException, UnknownHostException {
        this._socket = new DatagramSocket();
        this._addres = InetAddress.getByName("192.168.1.25");
    }

    public void sendMessage(String msg) throws IOException {
        this._buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(this._buffer, this._buffer.length, this._addres, 8080);
        this._socket.send(packet);
    }

    public void close() {
        this._socket.close();
    }
}

package com.robocompany.robonitor;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class Comunicator implements Serializable {

    class Command implements Serializable{

        static final int CHECK_ONLINE    = 1000;
        static final int LOGIN           = 2000;
        static final int GET_INFO        = 3000;
        static final int START_SAMPLES   = 4000;
        static final int STOP_SAMPLES    = 5000;
        static final int ASK_SAMPLE      = 6000;
        static final int ASK_IMAGE       = 7000;

    }

    class Response  implements Serializable{

        static final int OK              = 1001;
        static final int SCAN_KEY_WRONG  = 1002;
        static final int ACC_TOK_WRONG   = 1003;
        static final int LOG_TOK_WRONG   = 1004;
        static final int IMG_ERROR       = 1005;

    }

    Comunicator(Robot robot) {

        this.serverHost = robot.address;
        this.portHost = robot.port;

        try {
            this.socketComm = new DatagramSocket();
            this.socketSample = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    //Query
    private JsonObject query_data;

    //Reponse

    private int response_code;
    private JSONObject response_data;

    // Comunication socket

    private DatagramSocket socketComm;
    private DatagramSocket socketSample;
    private String serverHost;
    private int portHost;


    void create_query(int command, String param_value) {

        JsonObject query = new JsonObject();
        JsonObject params = new JsonObject();

        switch (command) {
            case Command.CHECK_ONLINE:

                params.addProperty("scan_key", param_value);

                break;
            case Command.LOGIN:

                params.addProperty("access_key", param_value);

                break;
            case Command.GET_INFO:

                params.addProperty("login_key", param_value);

                break;

            case Command.START_SAMPLES:

                params.addProperty("login_key", param_value);
                params.addProperty("sample_port", socketSample.getLocalPort());

                break;

            case Command.STOP_SAMPLES:

                params.addProperty("login_key", param_value);

                break;

            case Command.ASK_SAMPLE:

                params.addProperty("login_key", param_value);

                break;

            case Command.ASK_IMAGE:

                params.addProperty("login_key", param_value);

                break;
            default:
                return;
        }


        query.addProperty("command", Integer.toString(command));
        query.add("params", params);

        this.query_data = query;

    }

    void send_query() {

        String query_string = this.query_data.toString();

        byte[] query_bytes = query_string.getBytes();

        InetAddress serverInet = null;

        try {
            serverInet = InetAddress.getByName(this.serverHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatagramPacket peticion = new DatagramPacket(query_bytes, query_string.length(), serverInet, this.portHost);

        try {
            this.socketComm.send(peticion);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    void get_response(int timeout) {

        byte[] response_buffer = new byte[65535];
        DatagramPacket response_datagram = new DatagramPacket(response_buffer, response_buffer.length);

        try {
            this.socketComm.setSoTimeout(timeout);
            this.socketComm.receive(response_datagram);

        } catch (SocketTimeoutException e){
            //Log.d("Socket","Timeout getting response");
            return;

        } catch (IOException e) {
            e.printStackTrace();
            return;

        }
        String data = new String(response_datagram.getData(), response_datagram.getOffset(), response_datagram.getLength());
        Log.d("Response data",data);
        //JsonObject response = new JsonParser().parse(data).getAsJsonObject();
        JSONObject response;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.response_code = response.getInt("response");
            this.response_data = response.getJSONObject("params");
        }
        catch (JSONException ignored) {
        }

    }

    String get_chunked_image(int timeout, int nchunks) {

        byte[] response_buffer = new byte[65535];
        DatagramPacket response_datagram = new DatagramPacket(response_buffer, response_buffer.length);

        List<String> chunks = new ArrayList<String>();

        for(int i = 0; i<nchunks; i++){

            chunks.add(null);
        }

        for(int i = 0; i<nchunks; i++){

            try {
                this.socketComm.setSoTimeout(timeout);
                this.socketComm.receive(response_datagram);

            } catch (SocketTimeoutException e){
                //Log.d("Socket","Timeout getting response");
                return null;

            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }
            String data = new String(response_datagram.getData(), response_datagram.getOffset(), response_datagram.getLength());
            //Log.d("Image-Chunk data",data);
            //JsonObject response = new JsonParser().parse(data).getAsJsonObject();
            JSONObject response;
            try {
                response = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            try {

                chunks.add(response.getInt("nchunk"),response.getString("data"));
            } catch (JSONException e) {
                return null;
            }

            String query_string = "OK";

            byte[] query_bytes = query_string.getBytes();

            InetAddress serverInet = null;

            try {
                serverInet = InetAddress.getByName(this.serverHost);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            DatagramPacket peticion = new DatagramPacket(query_bytes, query_string.length(), serverInet, this.portHost);

            try {
                this.socketComm.send(peticion);
            } catch (IOException e) {
                e.printStackTrace();

            }


        }

        //String imagestring = "data:image/jpg;base64;";

        String imagestring = "";

        for(int i = 0; i<nchunks; i++){

            imagestring = imagestring + chunks.get(i);
        }

        return imagestring;
    }

    int lastResponse_code(){
        return this.response_code;
    }

    JSONObject lastResponse_data(){
        return this.response_data;
    }
}


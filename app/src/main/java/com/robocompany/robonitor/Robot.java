package com.robocompany.robonitor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Robot implements Serializable {

    String name;

    String address;
    int port;

    int state;

    //Comunication info

	int loged_in;
	public  String info;
	public  String device;

	private String scan_key;
	private String access_token;
	private String password_hash;
	private String login_token;
	private int sampling;

	private transient Comunicator com;

	Robot(String name, String address, int port, String scan_key) {
	    this.name = name;
	    this.address = address;
	    this.port = port;
	    this.scan_key = scan_key;
	    this.state = 0;
	    this.loged_in = 0;
    }

	private Comunicator comunicator(){

		if(com== null){
			com = new Comunicator(this);
		}
		return com;
	}

    void chk_connection() {

        if(!this.address.equals("") && this.port != 0 && !this.scan_key.equals("")) {

        	//Comunicator com = null;
			try {
				com = comunicator();

				com.create_query(Comunicator.Command.CHECK_ONLINE, this.scan_key);
				com.send_query();

				Thread.sleep(50);

				com.get_response(10000);

				if (com.lastResponse_code() == Comunicator.Response.OK) {

					this.state = 3;

				} else this.state = 2;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

    }

    void login(){

		//Comunicator com;
		try {
			com = comunicator();

			com.create_query(Comunicator.Command.LOGIN, "");
			com.send_query();

			Thread.sleep(50);

			com.get_response(10000);

			if (com.lastResponse_code() == Comunicator.Response.OK) {

				JSONObject response = com.lastResponse_data();
				this.access_token = response.getString("access_token");
				this.loged_in = 1;
			}
			else this.loged_in = 0;


		} catch (InterruptedException e) {
			e.printStackTrace();
			this.loged_in = 0;
		} catch (JSONException e) {
			e.printStackTrace();
			this.loged_in = 0;
		}

	}

	public void start_samples() {

		//Comunicator com;
		try {
			com = comunicator();

			com.create_query(Comunicator.Command.START_SAMPLES, login_token);
			com.send_query();

			Thread.sleep(50);

			com.get_response(10000);

			if (com.lastResponse_code() == Comunicator.Response.OK) {

				this.sampling = 1;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void stop_samples() {

		//Comunicator com;
		try {
			com = comunicator();

			com.create_query(Comunicator.Command.STOP_SAMPLES, login_token);
			com.send_query();

			Thread.sleep(50);

			com.get_response(10000);

			if (com.lastResponse_code() == Comunicator.Response.OK) {

				this.sampling = 0;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public JSONObject ask_sample(){

		//Comunicator com;
		try {
			com = comunicator();

			String login_token = this.get_LoginToken();

			com.create_query(Comunicator.Command.ASK_SAMPLE, login_token);
			com.send_query();

			Thread.sleep(50);

			com.get_response(10000);

			if (com.lastResponse_code() == Comunicator.Response.OK) {

				JSONObject response = com.lastResponse_data().optJSONObject("sample");
				return response;

			}


		} catch (InterruptedException e) {
			e.printStackTrace();

		}
		return null;
	}

	public String ask_image(){

		//Comunicator com;
		try {
			com = comunicator();

			String login_token = this.get_LoginToken();

			com.create_query(Comunicator.Command.ASK_IMAGE, login_token);
			com.send_query();

			Thread.sleep(50);

			com.get_response(10000);

			if (com.lastResponse_code() == Comunicator.Response.OK) {

				//String imagedata = com.lastResponse_data().getString("image");

				int imagesize = com.lastResponse_data().getInt("imagesize");
				int nchunks = com.lastResponse_data().getInt("nchunks");
				int chunksize = com.lastResponse_data().getInt("chunksize");

				String imagedata = com.get_chunked_image(10000, nchunks);

				if (imagedata == null){
					return null;
				}

				return imagedata;

			}

		} catch (InterruptedException e) {
			e.printStackTrace();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	void get_info(){

		//Comunicator com;
		try {
			com = comunicator();

			String login_token = this.get_LoginToken();

			com.create_query(Comunicator.Command.GET_INFO, login_token);
			com.send_query();

			Thread.sleep(50);

			com.get_response(10000);

			if (com.lastResponse_code() == Comunicator.Response.OK) {

				JSONObject response = com.lastResponse_data();
				this.info = response.getJSONObject("info").toString();
				this.device = response.getJSONObject("device").toString();
				this.loged_in = 3;
			}
			else this.loged_in = 0;

		} catch (InterruptedException e) {
			e.printStackTrace();
			this.loged_in = 0;
		} catch (JSONException e) {
			e.printStackTrace();
			this.loged_in = 0;
		}
	}

	void auth(String pass) {

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hashbytes = digest.digest(pass.getBytes());
			this.password_hash = toHexString(hashbytes);

		} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();

		}
	}

	private String get_LoginToken(){
		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			String a = this.access_token + this.password_hash + this.scan_key;

			byte[] hashbytes = digest.digest(a.getBytes());
			this.login_token = toHexString(hashbytes);
			return this.login_token;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
    	return null;
	}

	private static String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}

		return hexString.toString();
	}

}


package com.myfabric.myutil;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;

public class MyUtil {
	private String prompt = ">"; // ,Windows>,Linux#
	private char promptChar = '>'; //
	private TelnetClient telnet;
	private InputStream in; // ,
	private PrintStream out; //

	public boolean checkConn(String ip, int port) {
		telnet = new TelnetClient();
		try {
			telnet.connect(ip, port);
			//in = telnet.getInputStream();
			//out = new PrintStream(telnet.getOutputStream());
			telnet.disconnect();

		} catch (Exception e) {
			return false;
			// throw new RuntimeException(e);
		}
		return true;
	}

	public static void main(String[] args) {
		MyUtil telnet = new MyUtil();
		boolean a = telnet.checkConn("127.0.0.1", 7051);
		System.out.println(a);

	}

}

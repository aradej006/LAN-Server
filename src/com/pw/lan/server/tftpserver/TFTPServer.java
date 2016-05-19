package com.pw.lan.server.tftpserver;

import java.net.*;
import java.util.Hashtable;
import java.io.*;

import com.pw.lan.server.tftp.*;

import javax.net.ssl.SSLServerSocketFactory;

public class TFTPServer {
	
	static final int MAX_CONNECTIONS = 10; 
	
	protected String ftproot;
	protected int port;
	
	protected ServerSocket serverSocket;
	protected Socket clientSocket = null;
	
	protected Hashtable<Socket, TFTPServerClient> clients;

	public TFTPServer(int port, String ftproot, InetAddress inetAddress) {
		//- initialize settings
		this.port = port;
		this.ftproot = ftproot;
		clients = new Hashtable<Socket, TFTPServerClient>();
		
	    try {
//	    	serverSocket = new ServerSocket(port);
			serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port, 10, inetAddress);
	    } catch (IOException e) {
	    	TFTPUtils.fatalError("Could not listen on port: " + port);
	    }
	    TFTPUtils.puts("Server successfully started. Listening on port " + port + " started");
	    while (true) {
	    	try {
	    		clientSocket = serverSocket.accept();
	    	} catch (Exception e) {
	    		TFTPUtils.fatalError("An exeption was thrown while accepting a client connection");
	    	}
	    	if (clients.size() == MAX_CONNECTIONS) {
	    		TFTPPacket packet_error = new TFTPPacket();
	    		packet_error.createError(0, "Server is full, please try again later");
	    		try {
	    			packet_error.sendPacket(new BufferedOutputStream(clientSocket.getOutputStream()));
	    		} catch (Exception e) {
					TFTPUtils.puts("Unable to send error message to client");
				}
	    	} else {
	    		clients.put(clientSocket, new TFTPServerClient(this, clientSocket));
	    	}
	    }
	}
	
	public void removeConnection(Socket clientSocket) {
		TFTPUtils.puts("Removing client connection " + clientSocket.getInetAddress().getHostAddress());
		if (clients.get(clientSocket) != null) {
			try {
				clients.get(clientSocket).fin.close();
				clients.get(clientSocket).fout.close();
				clients.get(clientSocket).in.close();
				clients.get(clientSocket).out.close();
				clients.get(clientSocket).interrupt();
				clientSocket.close();
			} catch (Exception ie) {}

		}
		try {		
			clients.remove(clientSocket);
		} catch (Exception ie) {}
	}
	
	public void shutdown() {
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}
}

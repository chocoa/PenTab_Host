package com.akigon.java.pentab_pc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SeverSocketConectionListener {

	public SeverSocketConectionListener(final int port) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(port);
					System.out.println("Connection open!");
					onOpenServerSocket(ss);

					Socket socket = ss.accept();
					System.out.println("Connection connected!");
					onConectSocket(socket);

				} catch (IOException e) {
					System.out.println("Connection fail!");
					e.printStackTrace();
				}

				
			}
		}).start();

	}

	public abstract void onOpenServerSocket(ServerSocket ss);

	public abstract void onConectSocket(Socket socket);

}

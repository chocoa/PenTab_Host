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
					// ソケットの作成
					ServerSocket ss = new ServerSocket(port);
					System.out.println("Connection open!");
					// ソケット作成イベントの発行
					onOpenServerSocket(ss);

					// クライアントの待ち受け
					Socket socket = ss.accept();
					System.out.println("Connection connected!");
					// クライアントからの接続イベントの発行
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

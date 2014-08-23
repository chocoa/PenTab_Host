package com.akigon.java.pentab_pc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;



public abstract class ReceivedDataListener {
	private Socket mSocket;
	private String deta;

	public ReceivedDataListener(Socket socket) {
		mSocket = socket;
	}

	public void start() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					// 入力ソケットの準備
					InputStream in = mSocket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));

					// ソケットからのデータを取得
					while(true) {
						deta = br.readLine();
						onReceivedData(deta);

					}

				} catch(IOException e) {
					System.out.println("Reception fail!");
					e.printStackTrace();
				}

			}

		}).start();

	}

	protected abstract void onReceivedData(String deta);

}

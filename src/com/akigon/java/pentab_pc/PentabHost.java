package com.akigon.java.pentab_pc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class PentabHost extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final int PORT = 12345;
	private static final float ZOOM = 0.5f;
	private ServerSocket mServerSocket;
	private Graphics2D mG2;
	private int mNowX;
	private int mNowY;
	private int mOldX;
	private int mOldY;
	private boolean isLooper;
	private LinkedBlockingQueue<String> mReceiveQueue;
	private boolean isFirstDraw;

	public static void main(String[] args) {

		new PentabHost().init();

	}

	public PentabHost () {
		setTitle("Pentab(Build:1)");
		setSize(480, 600);
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addWindowListener(new PentabWindowListener());

		mG2 = (Graphics2D)getGraphics();
		mG2.setStroke(new BasicStroke(4.0f));
		mG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		mG2.setColor(Color.RED);

	}

	public void init() {

		new SeverSocketConectionListener(PORT) {

			@Override
			public void onOpenServerSocket(ServerSocket ss) {
				mServerSocket = ss;

			}

			@Override
			public void onConectSocket(Socket socket) {
				mReceiveQueue = new LinkedBlockingQueue<String>();

				new RemoteListener(socket) {

					@Override
					protected void onReceivedData(String deta) {
						if(deta != null) {

							if(!isFirstDraw)
								isFirstDraw = true;
							mReceiveQueue.offer(deta);

							if(!isLooper)
								drawLooper();

						}
					}
				}.start();
			}
		};

	}

	public void paint(Graphics g){

		if(isFirstDraw)
			mG2.draw(new Line2D.Double(mOldX, mOldY, mNowX, mNowY));

    }

	private void drawLooper() {
		isLooper = true;
		String deta;
		try {
			while((deta = mReceiveQueue.poll(50, TimeUnit.MILLISECONDS)) != null) {
				String[] drawDeta = deta.split(",", -1);
				if(drawDeta[0].equals("1")) {
					mNowX = (int)(Integer.parseInt(drawDeta[1]) * ZOOM);
					mNowY = (int)(Integer.parseInt(drawDeta[2]) * ZOOM);
					mOldX = mNowX;
					mOldY = mNowY;

				}else{
					mOldX = mNowX;
					mOldY = mNowY;
					mNowX = (int)(Integer.parseInt(drawDeta[1]) * ZOOM);
					mNowY = (int)(Integer.parseInt(drawDeta[2]) * ZOOM);
				}
				repaint();
			}
			isLooper = false;

		} catch (NumberFormatException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();

		}

	}

	class PentabWindowListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent event) {
			if (mServerSocket != null) {

				try {
					mServerSocket.close();
					System.out.println("Connection close!");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}

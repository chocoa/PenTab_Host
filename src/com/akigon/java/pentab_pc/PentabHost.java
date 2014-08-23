package com.akigon.java.pentab_pc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private int mNowP;
	private int mOldX;
	private int mOldY;
	private int mOldP;
	private boolean isLooper;
	private LinkedBlockingQueue<String> mReceiveQueue;

	// エントリーポイント
	public static void main(String[] args) {
		new PentabHost().init();
	}

	// サーバーコンストラクタ
	public PentabHost () {
		setTitle("Pentab(Build:1)");		// タイトル
		setSize(480, 600);					// ウィンドウサイズ
		setLocationRelativeTo(null);		// 画面中央へ
		setBackground(Color.WHITE);			// 背景
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// 終了イベント
		setVisible(true);					// ウィンドウ表示
		// ウィンドウ用のリスナーを登録
		addWindowListener(new PentabWindowListener());

		// グラフィック描画の初期化
		mG2 = (Graphics2D)getGraphics();
		mG2.setStroke(new BasicStroke(1.0f));
		//mG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	//AA

		mG2.setColor(Color.RED);
	}

	// 初期化
	public void init() {

		new SeverSocketConectionListener(PORT) {
			// 接続完了時のイベント
			@Override
			public void onOpenServerSocket(ServerSocket ss) {
				// サーバーソケットの取得
				mServerSocket = ss;
			}

			// ソケット接続時のイベント
			@Override
			public void onConectSocket(Socket socket) {
				// バッファ用にキューを作成
				mReceiveQueue = new LinkedBlockingQueue<String>();

				new ReceivedDataListener(socket) {
					// データが届いた時のイベント
					@Override
					protected void onReceivedData(String deta) {
						if(deta != null) {

							// 届いたデータをキューにPush
							mReceiveQueue.offer(deta);
							// 描画ループを動作
							if(!isLooper)
								drawLooper();

						}
					}
				}.start();
			}
		};

	}

	// 描画
	public void draw(){
		//mG2.draw(new Line2D.Double(mOldX, mOldY, mNowX, mNowY));

		Vector2D[] vs = getStrokePoints(mOldX,mOldY,mOldP,mNowX,mNowY,mNowP);
		int xPoints[] = {(int)vs[0].x, (int)vs[1].x, (int)vs[2].x, (int)vs[3].x};
		int yPoints[] = {(int)vs[0].y, (int)vs[1].y, (int)vs[2].y, (int)vs[3].y};
		mG2.fillPolygon(xPoints, yPoints, 4);
		int P2 = mNowP*2;
		mG2.fillOval(mNowX-mNowP, mNowY-mNowP, P2, P2);

    }

	private void drawLooper() {
		isLooper = true;
		String deta;
		try {
			// キューから全てデータを取り出し、描画
			while((deta = mReceiveQueue.poll(50, TimeUnit.MILLISECONDS)) != null) {	// 50ms タイムアウト
				String[] drawDeta = deta.split(",", -1);
				if(drawDeta[0].equals("1")) {
					mNowX = (int)(Integer.parseInt(drawDeta[1]) * ZOOM);
					mNowY = (int)(Integer.parseInt(drawDeta[2]) * ZOOM);
					mNowP = (int)(Integer.parseInt(drawDeta[3]) * ZOOM);
					mOldX = mNowX;
					mOldY = mNowY;
					mOldP = mNowP;
				}else{
					mOldX = mNowX;
					mOldY = mNowY;
					mOldP = mNowP;
					mNowX = (int)(Integer.parseInt(drawDeta[1]) * ZOOM);
					mNowY = (int)(Integer.parseInt(drawDeta[2]) * ZOOM);
					mNowP = (int)(Integer.parseInt(drawDeta[3]) * ZOOM);
				}
				// 描画
				draw();
			}
			isLooper = false;

		} catch (NumberFormatException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();

		}

	}



	// 開始点と終了点の筆厚から描画ストローク４点を計算
	private Vector2D[] getStrokePoints(float sx, float sy, float sp, float ex, float ey, float ep) {
		Vector2D vAngle = new Vector2D(ex-sx, ey-sy);
		Vector2D vAngleN = vAngle.normalize();
		Vector2D vNP = vAngleN.perpendicular();
		Vector2D[] ret = new Vector2D[4];
		ret[0] = new Vector2D(sx+vNP.x*sp, sy+vNP.y*sp);
		ret[1] = new Vector2D(sx-vNP.x*sp, sy-vNP.y*sp);
		ret[2] = new Vector2D(ex-vNP.x*ep, ey-vNP.y*ep);
		ret[3] = new Vector2D(ex+vNP.x*ep, ey+vNP.y*ep);
		return ret;
	}

	// ウィンドウの状態をイベントリスナーで取得できるようにするクラス
	class PentabWindowListener extends WindowAdapter {
		// JFrame.EXIT_ON_CLOSE リスナー
		@Override
		public void windowClosing(WindowEvent event) {
			// ソケットを閉じる
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

// пример клиента
public class Client1 {
	public static long delta = 0;

	class MySync extends Thread {
		@Override
		public void run() {
			long t1, t2, t3, d;
			int i = 1;
			Socket socket = null;
			DataInputStream input = null;
			DataOutputStream output = null;
			try {
				socket = new Socket("178.79.155.166", 5001);
				System.out.println("Подключенооооо");
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				while (true) {
					output.writeLong(System.currentTimeMillis() + delta);
					output.flush();
					t1 = input.readLong();
					t2 = input.readLong();
					t3 = System.currentTimeMillis() + delta;
					d = t2 - (t1 + t3) / 2;
					delta += Math.abs(d) > 10 ? d / 10 : d;
					Thread.sleep(300);
					// вычисление дельты времени
					// в проекте Screen то же самое в файле Sync
					if (i % 4 == 0) {
						i = 1;
						System.out.println(d + "|" + delta);
					} else
						i++;

				}
			} catch (Exception e) {

			} finally {
				// закрыть соединение
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void main(String[] args) throws Exception {
		Client1 client = new Client1();
		Thread t = client.new MySync();
		t.start();
		String str = new Scanner(System.in).next();
		Date runTo = new SimpleDateFormat("dd.MM.yyyy-HH:mm").parse("14.01.2019-" + str);
		long t1 = runTo.getTime() - delta;
		System.err.println(t1);
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                System.err.println(System.currentTimeMillis());
		            }
		        }, 
		        t1 - System.currentTimeMillis() 
		);
	}
}

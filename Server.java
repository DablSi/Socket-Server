import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {
	static int counter = 0;
	ServerSocket serverSocket = null;

	class MyConnect {
		int num;
		DataInputStream in;
		DataOutputStream out;
		Socket socket;

		public MyConnect(DataInputStream in, DataOutputStream out, Socket socket) {
			num = counter++;
			this.in = in;
			this.out = out;
			this.socket = socket;
		}

		public void close() throws IOException {
			num = -1;
			socket.close();
			out.close();
			in.close();
		}
	}

	// главный поток
	class WorkerThread extends Thread {
		MyConnect connect;
		long timeIn;

		WorkerThread(MyConnect connect) {
			this.connect = connect;
		}

		@Override
		public void run() {
			try {
				while (true) {
					timeIn = connect.in.readLong();
					connect.out.writeLong(timeIn);
					connect.out.flush();

					connect.out.writeLong(System.currentTimeMillis());
					connect.out.flush();
					//отправление серверного времени
				}
			} catch (IOException e) {
				synchronized (Server.this) {
					System.err.println(e.getMessage());
					try {
						connect.close();
					} catch (IOException e1) {
						System.out.println("Соединение закрыто" + e1.getMessage());
					}
				}

			}
		}
	}

	public Server(int port) {
		// Запускает сервер и ждет соединения
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Сервер запущен");
			System.out.println("Ожидание клиента...");
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					System.out.println("Клиент принят");
					new WorkerThread(new MyConnect(in, out, socket)).start();
				} catch (Exception i) {
					System.out.println(i);
				}
			}
		} catch (Exception i) {
			System.out.println(i);
		} finally {
			// закрывает сервер
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server(5001);
	}

}

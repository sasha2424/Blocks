
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

	public static HashMap<Integer, Player> playerMap;
	public static int ID = (int) (Math.random() * 10);

	public static ServerSocket server;
	public static Socket socket;
	public static InputStream inputStream;
	public static OutputStream outputStream;

	public static final float WIDTH = 500;
	public static final float HEIGHT = 500;

	public static void main(String[] args) {
		playerMap = new HashMap<Integer, Player>();

		try {
			server = new ServerSocket(59090);
			while (true) {
				try {
					socket = server.accept();
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();

					BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

					String input = "";
					String length = in.readLine();
					if (length.startsWith("length:")) {
						int l = Integer.parseInt(length.substring(7));
						for (int i = 0; i < l; i++) {
							input += (char) in.read();
						}
					} else {
						// TODO
						// Invalid Message Format
					}

					String output = "";
					if (input.startsWith("NEW")) {
						Player p = new Player((float) (Math.random() * WIDTH), (float) (Math.random() * HEIGHT));
						playerMap.put(ID, p);
						System.out.println("New Player: " + ID + "  " + p.toString());
						output = ID + "," + p.x + "," + p.y;
						ID += (int) (Math.random() * 10);

					} else if (input.startsWith("QUIT")) {
						int id = Integer.parseInt(input.substring(4));
						playerMap.remove(id);
					} else {
						output = getDataFor(input);
					}

					// send reply

					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
					out.write("length:" + output.length() + "\n");
					out.write(output + "\n\n");
					out.flush();

					inputStream.close();
					outputStream.close();
					socket.close();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} finally {
					socket.close();
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				socket.close();
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String getDataFor(String input) {
		String[] coords = input.split(",");
		try {
			int id = Integer.parseInt(coords[0]);
			float x = Float.parseFloat(coords[1]);
			float y = Float.parseFloat(coords[2].trim());

			String r = "";
			for (int i : playerMap.keySet()) {
				Player p = playerMap.get(i);
				if (i == id) {
					p.x = x;
					p.y = y;
					bindPlayer(p);
				} else {
					r += p.toString() + "\n";
				}
			}
			if (r.length() == 0) {
				return "";
			}
			return r.substring(0, r.length() - 1);
		} catch (NumberFormatException e) {
			return "";
		}

	}

	private static void bindPlayer(Player p) {
		if (p.x < 0) {
			p.x = 0;
		}
		if (p.x > WIDTH) {
			p.x = WIDTH;
		}
		if (p.y < 0) {
			p.y = 0;
		}
		if (p.y > HEIGHT) {
			p.y = HEIGHT;
		}
	}

}
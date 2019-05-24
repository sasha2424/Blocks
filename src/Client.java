import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import processing.core.PApplet;

public class Client extends PApplet {

	private String host = "127.0.0.1";
	private int port = 59090;

	Pattern pattern = Pattern.compile("[\\[(<][\\w.-]+,[\\w.-]+[\\])>]");

	int id;
	float x;
	float y;

	final float size = Player.size;
	final float speed = 5;
	int direction = 0; // 0 right 1 down 2 left 3 up

	public void settings() {
		size(600, 600);
	}

	public void setup() {
		Socket socket = null;
		System.out.println("Connecting");
		while (socket == null) {
			try {
				socket = new Socket(host, port);
			} catch (UnknownHostException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		System.out.println("Connected");
		String newPlayer = promptServer(socket, "NEW");
		String[] coords = newPlayer.split(",");
		id = Integer.parseInt(coords[0]);
		x = Float.parseFloat(coords[1]);
		y = Float.parseFloat(coords[2].trim());
		System.out.println("New Player: " + id + "  " + x + "," + y);
		rectMode(CENTER);
	}

	public void draw() {

		background(255);
		updateLocation();
		boundry();
		noFill();
		rect(width / 2 - x + (Server.WIDTH) / 2, height / 2 - y + (Server.HEIGHT) / 2, Server.WIDTH + size,
				Server.HEIGHT + size);
		fill(100);

		Socket socket;
		rect(width / 2, height / 2, size, size);
		try {
			socket = new Socket(host, port);
			if (key == 'q') {

				promptServer(socket, "QUIT" + id);
				this.exit();
			} else {
				String update = promptServer(socket, id + "," + x + "," + y);
				if (update.length() > 1) {
					drawUpdate(update);
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void exit() {
		try {
			promptServer(new Socket(host, port), "QUIT" + id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.exitActual();
	}

	public void updateLocation() {
		if (direction == 4)
			return;
		if (direction == 0)
			x += speed;
		if (direction == 1)
			y += speed;
		if (direction == 2)
			x -= speed;
		if (direction == 3)
			y -= speed;

	}

	public void keyPressed() {
		if (key == 'd')
			direction = 0;
		if (key == 's')
			direction = 1;
		if (key == 'a')
			direction = 2;
		if (key == 'w')
			direction = 3;
		if (key == ' ')
			direction = 4;
	}

	public void boundry() {
		if (x < 0) {
			x = 0;
		}
		if (x > Server.WIDTH) {
			x = Server.WIDTH;
		}
		if (y < 0) {
			y = 0;
		}
		if (y > Server.HEIGHT) {
			y = Server.HEIGHT;
		}
	}

	private void drawUpdate(String update) {
		float x = 0;
		float y = 0;
		Matcher m = pattern.matcher(update);
		while (m.find()) {
			String block = m.group();
			if (block.startsWith("[")) {
				x = Float.parseFloat(block.substring(1, block.indexOf(",")));
				y = Float.parseFloat(block.substring(block.indexOf(",") + 1, block.length() - 1));
				fill(100);
				rect(width / 2 + x - this.x, height / 2 + y - this.y, size, size);
			} else if (block.startsWith("<")) {
				x = Float.parseFloat(block.substring(1, block.indexOf(",")));
				y = Float.parseFloat(block.substring(block.indexOf(",") + 1, block.length() - 1));
				fill(0, 0, 100);
				rect(width / 2 + x - this.x, height / 2 + y - this.y, size, size);
			} else if (block.startsWith("(")) {
				float xs = Float.parseFloat(block.substring(1, block.indexOf(",")));
				float ys = Float.parseFloat(block.substring(block.indexOf(",") + 1, block.length() - 1));
				fill(200);
				rect(width / 2 + x + xs * size - this.x, height / 2 + y + ys * size - this.y, size, size);
			}
		}
	}

	private String promptServer(Socket socket, String s) {
		try {

			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			out.write("length:" + s.length() + "\n");
			out.write(s + "\n\n");
			out.flush();

			String input = "";
			String length = in.readLine();
			if (length.startsWith("length:")) {
				int l = Integer.parseInt(length.substring(7));
				for (int i = 0; i < l; i++) {
					input += (char) in.read();
				}
			}
			if (input.startsWith("QUIT")) {
				JOptionPane.showMessageDialog(null, "You Have Died");
				this.exit();
			}

			inputStream.close();
			outputStream.close();
			socket.close();
			return input;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		PApplet.main("Client");
	}
}

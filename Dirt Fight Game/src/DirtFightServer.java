import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DirtFightServer extends JPanel {

	int fps = 10;
	public int width = 100;
	public int height = 30;
	public int scale = 10;
	DirtFightGame game;

	public DirtFightServer() {
		game = new DirtFightGame(width, height);

		JFrame frame = new JFrame("Cellular");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(width * scale, height * scale));
		// setSize(width, height);
		frame.setContentPane(this);
		frame.pack();
		frame.setVisible(true);
		this.addComponentListener(new ResizeListener());
	}

	public void reset() {
		game = new DirtFightGame(width, height);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(scale, scale);
		game.paint(g2);
	}

	public static void main(String args[]) throws IOException {

		int connections = 0;
		ServerSocket listener = new ServerSocket(9090);
		String name1 = "", name2 = "";
		PrintWriter out1, out2;
		out1 = new PrintWriter(System.out);
		out2 = new PrintWriter(System.out);
		BufferedReader in1, in2;
		in1 = new BufferedReader(new InputStreamReader(System.in));
		in2 = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("waiting for connections");
		while (connections < 2) {
			try {
				Socket s = listener.accept();
				if (connections == 0) {
					out1 = new PrintWriter(s.getOutputStream(), true);
					in1 = new BufferedReader(new InputStreamReader(s.getInputStream()));
					name1 = in1.readLine();
					System.out.println("player 1 connected");
				}
				if (connections == 1) {
					out2 = new PrintWriter(s.getOutputStream(), true);
					in2 = new BufferedReader(new InputStreamReader(s.getInputStream()));
					name2 = in2.readLine();
					System.out.println("player 2 connected");
				}
				connections++;
			} catch (Exception e) {

			}
		}
		System.out.println(name1 + " and " + name2 + " are ready to go");
		DirtFightServer dfs = new DirtFightServer();
		while (true) {
			if (dfs.game.winner() > 0) {
				if (dfs.game.winner() == 1) {
					System.out.println(name1 + " wins");
				} else {
					System.out.println(name2 + " wins");
				}
				dfs.reset();
			}
			if (dfs.game.ply % 2 == 0) {
				out1.println(dfs.game);
				dfs.game.parseCommand(in1.readLine());
			} else {
				out2.println(dfs.game);
				dfs.game.parseCommand(in2.readLine());
			}
			dfs.game.evolve();
			dfs.invalidate();
			dfs.repaint();
			try {
				Thread.sleep(10);
			} catch (Exception e) {

			}
		}

	}

	class ResizeListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			int new_scale = Math.min(e.getComponent().getWidth() / width, e.getComponent().getHeight() / height);
			scale = new_scale;
		}
	}
}

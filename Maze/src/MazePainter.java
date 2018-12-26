import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MazePainter extends JPanel {

	private static final long serialVersionUID = 4208856149587726093L;

	private final Maze maze;
	private final JLabel[][] squares;

	public MazePainter(Maze maze) {
		this.maze = maze;

		squares = new JLabel[maze.getWidth()][maze.getHeight()];

		this.setLayout(new GridLayout(maze.getHeight(), maze.getWidth()));

		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				squares[x][y] = new JLabel();
				// squares[x][y].setBorder(BorderFactory.createLineBorder(Color.green,
				// 1));
				squares[x][y].setOpaque(true);
				this.add(squares[x][y]);
			}
		}
		updateAll();
	}

	public void updateAll() {
		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				updateNoRepaint(x, y);
			}
		}
		repaint();
	}

	public void update(int x, int y) {
		updateNoRepaint(x, y);
		repaint();
	}

	public void setColor(int x, int y, Color col) {
		squares[x][y].setBackground(col);
		repaint();
	}

	public void updateNoRepaint(int x, int y) {
		if (maze.get(x, y)) {
			squares[x][y].setBackground(Color.white);
		} else {
			squares[x][y].setBackground(Color.black);
		}
	}

}

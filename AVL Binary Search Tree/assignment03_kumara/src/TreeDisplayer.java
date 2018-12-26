import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TreeDisplayer extends JFrame {

	private static final long serialVersionUID = 1812997056711161827L;

	private AVLTree<?> tree;
	private TreePainter painter;

	private int treeHeight;
	private int maxTreeWidth;

	public TreeDisplayer(AVLTree<?> tree) {
		super();
		this.tree = tree;

		refreshTree();

		painter = new TreePainter();
		this.add(painter);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900, 900);
		this.setVisible(true);
	}

	public BufferedImage getTreeImage() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		painter.paintImage(img);
		return img;
	}

	public void refreshTree() {
		treeHeight = tree.getTreeHeight();
		maxTreeWidth = (int) Math.pow(2, treeHeight - 1);
	}

	class TreePainter extends JPanel {

		private static final long serialVersionUID = 7140544404825185190L;

		@Override
		public void paintComponent(Graphics g) {

			Node<?>[] nodes = new Node<?>[1];
			nodes[0] = tree.getRoot();
			for (int h = 0; h < treeHeight;) {
				int y = (int) ((double) getHeight() / treeHeight * h);
				int texty = y + getHeight() / treeHeight / 2;

				for (int w = 0; w < nodes.length; w++) {
					int x = (int) ((double) getWidth() / nodes.length * w);

					int textx = (int) (x + (double) (getWidth() / nodes.length / 2)) - 6;
					g.drawString(nodes[w] == null ? "" : nodes[w].getData().toString(), textx, texty);
					g.drawRect(x, y, x + getWidth() / nodes.length, y + getHeight() / treeHeight);
				}
				nodes = getNextLevelNodes(nodes, ++h);
			}
		}

		public void paintImage(BufferedImage img) {
			paintComponent(img.getGraphics());

			Graphics g = img.createGraphics();

			g.setColor(Color.white);
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
			g.setColor(Color.black);
			paintComponent(g);
		}

	}

	public Node<?>[] getNextLevelNodes(Node<?>[] currentLevel, int h) {
		Node<?>[] nodes = new Node<?>[(int) Math.pow(2, h)];
		for (int i = 0; i < currentLevel.length; i++) {
			if (currentLevel[i] != null) {
				nodes[2 * i] = currentLevel[i].left;
				nodes[2 * i + 1] = currentLevel[i].right;
			}
		}
		return nodes;
	}

	public List<Node<?>> getNodesAtHeight(int h) {
		List<Node<?>> nodes = new ArrayList<Node<?>>();
		traverseAndAddAtHeight(h, tree.getRoot(), nodes);
		return nodes;
	}

	public void traverseAndAddAtHeight(int h, Node<?> node, List<Node<?>> nodeList) {
		int height = AVLTree.getNodeHeight(node);
		if (treeHeight - height == h) {
			nodeList.add(node);
		} else {
			if (treeHeight - height + 1 == h) {
				nodeList.add(node.left);
				nodeList.add(node.right);
			} else {
				if (node.left != null) {
					traverseAndAddAtHeight(h, node.left, nodeList);
				}
				if (node.right != null) {
					traverseAndAddAtHeight(h, node.right, nodeList);
				}
			}
		}
	}
}

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	public static TreeDisplayer displayer;

	public static void main(String[] args) {
		AVLTree<Integer> tree = new AVLTree<Integer>((o1, o2) -> o1 - o2);
		displayer = new TreeDisplayer(tree);

		for (int i = 0; i < 20; i++) {
			int num = (int) (Math.random() * 100);
			tree.add(num);
		}

		System.out.println(tree);
		System.out.println(tree.getTreeHeight());
		System.out.println(tree.size());
		System.out.println(tree.isProperlyTied() + "\n\n");

		displayer.refreshTree();
		displayer.repaint();

		System.out.println("\n\n" + tree);
		System.out.println(tree.getTreeHeight());
		System.out.println(tree.size());
		System.out.println(tree.isProperlyTied());

		displayer.refreshTree();
		displayer.repaint();

		BufferedImage img = displayer.getTreeImage();
		try {
			ImageIO.write(img, "png", new File("picture.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

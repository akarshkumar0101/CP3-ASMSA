import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageViewer extends JFrame {

	private static final long serialVersionUID = -2586945762397462416L;

	private final ImageViewerPanel ivp;

	public ImageViewer() {
		this(null, true);
	}

	public ImageViewer(Image image) {
		this(image, true);
	}

	public ImageViewer(Image image, boolean keepAspectRatioOfImg) {
		super();

		ivp = new ImageViewerPanel(image, keepAspectRatioOfImg);

		this.setContentPane(ivp);

		this.setSize(900, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void show(Image image) {
		ivp.setImage(image);
		repaint();
	}

}

class ImageViewerPanel extends JPanel {

	private static final long serialVersionUID = 2920218638545057480L;

	private Image image;
	private boolean keepImgAspectRatio;

	public ImageViewerPanel(Image image, boolean keepImgAspectRatio) {
		super();
		this.image = image;
		this.keepImgAspectRatio = keepImgAspectRatio;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void paintComponent(Graphics g) {
		Image image = this.image;
		g.setColor(Color.gray);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (image == null)
			return;
		if (keepImgAspectRatio) {
			double frameratio = (double) getWidth() / getHeight();
			double imgratio = (double) image.getWidth(null) / image.getHeight(null);

			// image is too wide, put space on top
			if (imgratio > frameratio) {
				double scaleImg = (double) getWidth() / image.getWidth(null);
				int imgwidth = (int) (scaleImg * image.getWidth(null));
				int imgheight = (int) (scaleImg * image.getHeight(null));

				int shiftdown = (getHeight() - imgheight) / 2;
				g.drawImage(image, 0, shiftdown, imgwidth, imgheight, null);
			} else {
				double scaleImg = (double) getHeight() / image.getHeight(null);
				int imgwidth = (int) (scaleImg * image.getWidth(null));
				int imgheight = (int) (scaleImg * image.getHeight(null));

				int shiftright = (getWidth() - imgwidth) / 2;
				g.drawImage(image, shiftright, 0, imgwidth, imgheight, null);

			}
		} else {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
	}
}

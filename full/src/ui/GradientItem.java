/*
 * GradientItem.java
 *
 * Created on 2 Ноябрь 2006 г., 14:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import ui.polish.DrawUtil;

public class GradientItem extends CustomItem {
	

	private final int width;
	private final int height;
	private int topColor = 0xFFFFFF;
	private int midColor = 0xFFFF00;
	private int bottomColor = 0x0000FF;
	private Image image;

	public GradientItem( int width, int height ) {
		super(null);
		this.width = width;
                this.height=height;
		updateImage();
	}

        protected int getMinContentWidth() {
		return this.width;
	}

	protected int getMinContentHeight() {
		return this.height;
	}

	protected int getPrefContentWidth(int maxHeight) {
		return this.width;
	}

	protected int getPrefContentHeight(int grantedWidth) {
		return this.height;
	}

	protected void paint(Graphics g, int w, int h) {
		g.drawImage( this.image, 0, 0, Graphics.LEFT | Graphics.TOP );
	}

	public int getBottomColor() {
		return this.bottomColor;
	}

	public void setBottomColor(int bottomColor) {
		this.bottomColor = bottomColor;
		updateImage();
	}

	public int getMidColor() {
		return this.midColor;
	}

	public void setMidColor(int midColor) {
		this.midColor = midColor;
		updateImage();
	}

	public int getTopColor() {
		return this.topColor;
	}

	public void setTopColor(int topColor) {
		this.topColor = topColor;
		updateImage();
	}

	private void updateImage() {
		if (this.image == null) {
			this.image = Image.createImage( this.width, this.height );
		}
		Graphics g = this.image.getGraphics();
		int steps = this.height/2;
		int[] gradient = DrawUtil.getGradient(this.topColor, this.midColor, steps);
		for (int i = 0; i < gradient.length; i++) {
			int color = gradient[i];
			g.setColor(color);
			g.drawLine( 0, i, this.width, i );
		}
		DrawUtil.getGradient(this.midColor, this.bottomColor, gradient);
		for (int i = 0; i < gradient.length; i++) {
			int color = gradient[i];
			g.setColor(color);
			g.drawLine( 0, i + steps, this.width, i + steps );
		}
		invalidate();
	}

}

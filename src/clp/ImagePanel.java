package clp;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    public BufferedImage image;

    public ImagePanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(image, 0, 0, null);            
    }
    
    public void drawImage()
    {
        invalidate();
    }

}

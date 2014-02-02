/*
 * BgImagePanel.java
 * Copyright (C) 2003 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class BgImagePanel extends JPanel {
    
    Image image;  
    
    public BgImagePanel(Image image) {    
        this.image = image; 
    }  
    
    public BgImagePanel() {    
        image = null;  
    }  
    
    public void paintComponent(Graphics g) {    
        super.paintComponent(g);    
        if (image != null) {
            Rectangle rectFull = new Rectangle(0, 0, getSize().width, getSize().height);      
            Graphics2D g2d = (Graphics2D)g;      
            BufferedImage buffImg = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_RGB); //TYPE_INT_RGB);      
            Graphics2D g2d2 = buffImg.createGraphics();      
            g2d2.drawImage(image, 0, 0, Color.white, this);      
            TexturePaint imagePaint = new TexturePaint(buffImg, new Rectangle(0, 0, image.getWidth(this), image.getHeight(this)));      
            g2d.setPaint(imagePaint);      
            g2d.fill(rectFull);    
        }  
    }  
    
    public void setImage(String imageURL) {    
        MediaTracker mt = new MediaTracker(this);    
        image = Toolkit.getDefaultToolkit().getImage(imageURL);    
        mt.addImage(image, 0);    
        try {      
            mt.waitForID(0);    
        } catch (InterruptedException ex) {      
            ex.printStackTrace();    
        }  
    } 
    
    public void setImage (Image newImage) {    
        image = newImage;  
    }
    
        
    /*public static void main (String[] args) {  
        ChangeBGPanel panel = new ChangeBGPanel();
        ImageIcon img = new ImageIcon(ChangeBGPanel.class.getResource("/test/T1.gif"));
        panel.setImage(img.getImage());    
        
        JFrame frame = new JFrame();
        frame.getContentPane().add(panel);    
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            
        frame.setSize(400, 300);    
        frame.setVisible(true);  
    }*/
}

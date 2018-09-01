package com.wf.Utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ChangeRGB {
	static int changeValue = 1;
	static int i=2;
	public static void main(String[] args) throws IOException {
		String path="E:\\biyesheji\\image\\PosImage\\cupss1.jpeg";
		//String path="E:\\biyesheji\\image\\NegImage\\backssss1.jpeg";
		File file=new File(path);
		Image image = ImageIO.read(file);
		BufferedImage bi_scale = toBufferedImage(image);
		processImg(bi_scale, changeValue);
		for(;changeValue<=1000;changeValue +=2)
		{
			String path2="E:\\biyesheji\\image\\PosImage\\cupss"+i+".jpeg";
			//String path2="E:\\biyesheji\\image\\NegImage\\backssss"+i+".jpeg";
			ImageIO.write(bi_scale, "jpeg",new File(path2));
			i++;
		}
	}
	 /**
     * 处理图片
     *
     */
    private static void processImg(BufferedImage img,int changeValue)
    {
        for (int x = 0; x < img.getWidth(); x++)
        {
            for (int y = 0; y < img.getHeight(); y++)
            {
                // 获取到rgb的组合值
                int rgb = img.getRGB(x, y);
                Color color = new Color(rgb);
                int r = color.getRed() + changeValue;
                int g = color.getGreen() + changeValue;
                int b = color.getBlue() + changeValue;
                if (r > 255)
                {
                    r = 255;
                } else if (r < 0)
                {
                    r = 0;
                }
 
                if (g > 255)
                {
                    g = 255;
                } else if (g < 0)
                {
                    g = 0;
                }
 
                if (b > 255)
                {
                    b = 255;
                } else if (b < 0)
                {
                    b = 0;
                }
 
                color = new Color(r, g, b);
                img.setRGB(x, y, color.getRGB());
            }
        }
    }

    //Image转BufferedImage
    public static BufferedImage toBufferedImage(Image image) {  
        if (image instanceof BufferedImage) {  
             return (BufferedImage)image;  
        }                   
        // 加载所有像素 
        image = new ImageIcon(image).getImage();                    
        BufferedImage bimage = null;  
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        try {                        
             int transparency = Transparency.OPAQUE;                        
             // 创建buffer图像  
             GraphicsDevice gs = ge.getDefaultScreenDevice();  
             GraphicsConfiguration gc = gs.getDefaultConfiguration();  
             bimage = gc.createCompatibleImage(  
             image.getWidth(null), image.getHeight(null), transparency);  
        } catch (HeadlessException e) {  
              e.printStackTrace(); 
        }                   
        if (bimage == null) {                         
            int type = BufferedImage.TYPE_INT_RGB;  
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);  
        }                   
        // 复制
        Graphics g = bimage.createGraphics();                   
        // 赋值  
        g.drawImage(image, 0, 0, null);  
        g.dispose();                    
        return bimage;
} 
}

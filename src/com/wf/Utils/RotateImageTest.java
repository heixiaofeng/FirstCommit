package com.wf.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RotateImageTest {
	public static void main(String[] args) throws IOException {
		 BufferedImage src = ImageIO.read(new File("E:\\biyesheji\\INRIAPerson\\PennFudanPed\\train\\resultImage\\ID_train_000 (1).jpg"));
		 System.out.println(src.getHeight()+","+src.getWidth());
	        BufferedImage des = RotateImage.Rotate(src, 30);  
	        ImageIO.write(des, "jpg", new File("E:\\biyesheji\\INRIAPerson\\PennFudanPed\\train\\resultImage\\ID_train_000 (1)1.jpg"));  
	  
	       /* // bigger angel  
	        des = RotateImage.Rotate(src, 150);  
	        ImageIO.write(des, "jpeg", new File("E:\\biyesheji\\image\\PosImage\\cup3.jpeg"));  
	  
	        // bigger angel  
	        des = RotateImage.Rotate(src, 270);  
	        ImageIO.write(des, "jpeg", new File("E:\\biyesheji\\image\\PosImage\\cup4.jpeg"));  */
	}
}

package com.wf.Implement;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;

import com.wf.Interface.AllMemory;
import com.wf.UI.UIDesign;
/**
 * 对背景截图和存储
 * @author wang feng
 *
 */
public class BackMemoryImp implements AllMemory{
	/**
	 * @pointList:坐标集合,用于计算截取部分的宽和高
	 * @icon:传入打开的图片
	 * @img:将Image转成BufferedImage以便截图
	 */
	public void Memory(ArrayList pointList, ImageIcon icon, BufferedImage img) {
		int width2=0;
  	    int height2=0;
  	    Point p1=null,p2=null;
  	    UIDesign uiDesign = new UIDesign();
  	    
		for(int i =0;i < pointList.size();i=i+2){
   		 p1 = (Point) pointList.get(i);
   	     p2 = (Point) pointList.get(i+1);
   	     width2  = p2.x - p1.x;
   	     height2 = p2.y - p1.y;
   	     if(width2<0)
   	     {
   	    	 width2 = -width2;
   	     }
   	     if(height2<0)
   	     {
   	    	 height2 = -height2;
   	     }
   	     System.out.println("sub:"+p1.x+","+p1.y+","+width2+","+height2);
   		}
		BufferedImage imgIcon = uiDesign.toBufferedImage(icon.getImage());
		//调整截图坐标，合适截取
		//img = imgIcon.getSubimage(p1.x-5, p1.y-20, width2, height2);
		img = imgIcon.getSubimage(p1.x-10, p1.y-40, width2, height2);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
	    String fileName = sdf.format(new Date());
		String imgName = "back"+fileName+".png";
	
		uiDesign.saveImage(img,imgName);
		String content = "E:\\biyesheji\\image\\resultImage\\"+imgName+"	"+0;
		try {
			uiDesign.saveFile(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

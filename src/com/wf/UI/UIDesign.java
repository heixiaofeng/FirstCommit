package com.wf.UI;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.wf.Implement.BackMemoryImp;
import com.wf.Implement.PeopleMemoryImp;

public class UIDesign{

	public JLabel label;
    public JFileChooser chooser;
    public final int DEFAULT_WIDTH = 500;
    public final int DEFAULT_HEIGHT = 500;
    public JFrame jFrame;
    int startX =0, startY=0, endX=0, endY=0;
    int mouseX=0,mouseY=0;
    ImageIcon icon;
    ArrayList<Point> pointList;
    BufferedImage img;
    
	public UIDesign()
	{
		pointList = new ArrayList<Point>();
	}
	
	/**
     * 判断输入用于标记
     * @throws Exception 
     */
    public void controlChose() throws Exception
    {
    		int labelc=0;
    		System.out.println("Please input the label");
    		pointList.clear();//进行下次标记前把map清空
    		Scanner scan = new Scanner(System.in);
    		
    		Object label = scan.next();
    		if(label == null)
    		{
    			System.out.println("Please input number!");
    		}else{
    			labelc = Integer.parseInt(label.toString());
    		}
    		//scan.close(); //报错
    		
    		switch(labelc){
    		case 0:
    			System.out.println("Saving....");
    			backMemory();
    			break;
    		case 1:
    			System.out.println("Saving....");
    			peopleMemory();
    			break;
    		}
    	
    }
    /**
     * 背景处理
     */
    public void backMemory()
    {
    	BackMemoryImp backMemoryImp = new BackMemoryImp();
    	backMemoryImp.Memory(pointList, icon, img);
    }
    /**
     * 行人处理，保存图片和存储路径
     * @throws Exception 
     */
	public void peopleMemory() throws Exception
	{
		PeopleMemoryImp peopleMemoryImp = new PeopleMemoryImp();
		peopleMemoryImp.Memory(pointList, icon, img);
	}
	/**
	 * 保存图片和存储地址文件
	 * @param image
	 */
	public void saveImage(BufferedImage image,String imageName)
	{
		FileOutputStream fos=null;
		String file = "E:\\biyesheji\\image\\resultImage\\"+imageName;
		File f = new File(file);
		if(!f.exists()){
		    try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fos = new FileOutputStream(f);
			ImageIO.write(image, "png", fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(fos != null)
			{
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	 /**
     * 内容保存到文件
     * @param newStr
     * @throws Exception
     */
    public  void saveFile(String newStr) throws Exception
    {
    	String file = "E:\\biyesheji\\image\\traindata.txt";
    	String filein = newStr + "\r\n";
        String temp = ""; 
        BufferedReader br = null;
        PrintWriter pw = null;
         
         try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
			StringBuffer buf = new StringBuffer();
			 // 保存该文件原有的内容  
            while((temp = br.readLine()) != null) {
                buf = buf.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);
            
            pw = new PrintWriter(new FileOutputStream(new File(file)));
            pw.write(buf.toString().toCharArray());
            pw.flush();
		} catch (Exception e) {
			throw e;
		}finally{
			if(pw != null)
			{
				pw.close();
			}
			if(br !=null)
			{
				br.close();
			}
		}
    }
	
	
	/**
	 * Image转BufferedImage
	 * @param image
	 * @return
	 */
	public  BufferedImage toBufferedImage(Image image) {  
        if (image instanceof BufferedImage) {  
            return (BufferedImage)image;  
         }  
        
        // This code ensures that all the pixels in the image are loaded  
         image = new ImageIcon(image).getImage();  
        
        // Determine if the image has transparent pixels; for this method's  
        // implementation, see e661 Determining If an Image Has Transparent Pixels  
        //boolean hasAlpha = hasAlpha(image);  
        
        // Create a buffered image with a format that's compatible with the screen  
         BufferedImage bimage = null;  
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        try {  
            // Determine the type of transparency of the new buffered image  
            int transparency = Transparency.OPAQUE;  
           /* if (hasAlpha) { 
             transparency = Transparency.BITMASK; 
             }*/  
        
            // Create the buffered image  
             GraphicsDevice gs = ge.getDefaultScreenDevice();  
             GraphicsConfiguration gc = gs.getDefaultConfiguration();  
             bimage = gc.createCompatibleImage(  
             image.getWidth(null), image.getHeight(null), transparency);  
         } catch (HeadlessException e) {  
            // The system does not have a screen  
         }  
        
        if (bimage == null) {  
            // Create a buffered image using the default color model  
            int type = BufferedImage.TYPE_INT_RGB;  
            //int type = BufferedImage.TYPE_3BYTE_BGR;//by wang  
            /*if (hasAlpha) { 
             type = BufferedImage.TYPE_INT_ARGB; 
             }*/  
             bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);  
         }  
        
        // Copy image to buffered image  
         Graphics g = bimage.createGraphics();  
        
        // Paint the image onto the buffered image  
         g.drawImage(image, 0, 0, null);  
         g.dispose();  
        
        return bimage;  
    }  
	
	
	
	/**
	 * 构造界面并添加鼠标处理事件
	 */
    public void UIDesignMethod()
    {
    	jFrame = new JFrame();
    	jFrame.setTitle("ImageViewer");
    	jFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    	jFrame.setLocationRelativeTo(null);
    	
    	
		label = new JLabel();
		jFrame.add(label);
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		
		JMenuBar menubar = new JMenuBar();
		jFrame.setJMenuBar(menubar);
		JMenu menu = new JMenu("File");
		menubar.add(menu);
		JMenuItem openItem = new JMenuItem("Open");
		menu.add(openItem);
		JMenuItem exitItem = new JMenuItem("Close");
		menu.add(exitItem);
		openItem.addActionListener(new ActionListener() {
			//选择图片
			public void actionPerformed(ActionEvent arg0) {
				
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String name = chooser.getSelectedFile().getPath();
					name = name.trim();
					icon = new ImageIcon(name);
					jFrame.setSize(icon.getIconWidth(),icon.getIconHeight()+30);
					label.setIcon(icon);
				    /*String fileName = name.substring(name.lastIndexOf("\\")+1);
					String dest = "E:\\biyesheji\\image\\destImage\\"+fileName;
					//对图片进行缩放
					try {
						zoomImage(name, dest, DEFAULT_WIDTH, DEFAULT_HEIGHT);
					} catch (Exception e) {
						e.printStackTrace();
					}
					icon = new ImageIcon(dest);
					label.setIcon(icon);*/
				}
			}
		});
		exitItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		jFrame.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				//String text = "鼠标位置："+mouseX+","+mouseY;
				//System.out.println(text);
			}

			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				//String text = "鼠标位置："+mouseX+","+mouseY;
				//System.out.println(text);
			}
		});
		jFrame.addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent e) {
				startX = e.getX();

				startY = e.getY();
				
				pointList.add(new Point(startX, startY));
			}

			public void mouseReleased(MouseEvent e) {
				//System.out.println("release...");
				endX = e.getX();

				endY = e.getY();
				
				pointList.add(new Point(endX, endY));
				
				System.out.println("pointListSize:"+pointList.size());
	    		for(int i =0;i < pointList.size();i=i+2){
	    		 Point p1 = (Point) pointList.get(i);
	    	     Point p2 = (Point) pointList.get(i+1);
	    	     int width2  = p2.x - p1.x;
	    	     int height2 = p2.y - p1.y;
	    	     System.out.println("Rect:"+p1.x+","+p1.y+","+width2+","+height2);
	    	    jFrame.getGraphics().drawRect(p1.x,p1.y,width2,height2);
	    		}
			}
		});
        
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
    /*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    public static void zoomImage(String src,String dest,int w,int h) throws Exception {
        
        double wr=0,hr=0;
        File srcFile = new File(src);
        File destFile = new File(dest);

        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);//设置缩放目标图片模板
        
        wr=w*1.0/bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    	
}

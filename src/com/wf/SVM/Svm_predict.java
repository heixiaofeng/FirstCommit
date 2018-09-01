package com.wf.SVM;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;


public class Svm_predict {
	public void svm_predict() {
		//The winSize should match the dimensions of your training images. 
		//In my case I used 32x64 images (for training) and so I needed to use a winSize=(32x64). 
		Mat myDetector = new Svm_train().myDetector();
		HOGDescriptor hog = new HOGDescriptor(new Size(64, 128), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);//行人128
		hog.setSVMDetector(myDetector);
		
		//String testtxt = "E:\\biyesheji\\image\\traindata_cc2.txt";//人脸
		String testtxt = "E:\\biyesheji\\image\\traindata_cc.txt";//行人
		ArrayList<String> img_path = new ArrayList<String>();
	
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(testtxt)), "UTF-8"));
			String linetxt = null;
			Integer nline = 0;
			while ((linetxt = br.readLine()) != null) {
				nline++;
				String[] path_label = linetxt.split("\t");
				System.out.println(path_label[0] + ' ');
				img_path.add(path_label[0]);
			}
			br.close();

			// svm test start
			//Svm_predict sp = new Svm_predict();
			//int picNum=0;//子图片名称
			for (Integer i = 0; i < img_path.size(); i++) {
				Mat src_test = Imgcodecs.imread(img_path.get(i));// 读取一张图片
				if (src_test.empty()) {
					throw new Exception("no such picture");
				}
				//用于截图
				//Mat dst = new Mat();
				//src_test.copyTo(dst);
				//获取图片名
				//String fName = img_path.get(i).substring(img_path.get(i).lastIndexOf("\\")+1);
				//截取str中从beginIndex开始至endIndex结束时的字符串
				//fName = fName.substring(0, fName.lastIndexOf("."));
				
				MatOfRect mor = new MatOfRect(); // 检测完毕后会储存在这里  
		        MatOfDouble mod = new MatOfDouble();
		       // System.out.println("正在检测...");
		        /* 参数解读： 
		         * Mat img 待检测的图像，Mat类型，
		         * MatOfRect foundLocations 用于储存检测后的序列（或者叫做矩阵） 
		         * MatOfDouble foundWeights 检测窗口得分，不清楚是什么东西
		         * double hitThreshold 命中阈值，0 
		         * Size winStride 检测步长，网上大多的步长参数为（8,8），用的是（4,4），原因是（8,8）的步长对于INRIA数据集中的图片不能很好的检测，而（4,4）可以 
		         * Size padding 这个参数没有过多了解，理解为：block的大小。大多代码推荐（16,16）,这里用（8,8）
		         * 常见的pad size 有(8, 8), (16, 16), (24, 24), (32, 32).
		         * double scale 比例,1.05,通常scale在1.01-1.5这个区间
		         * double finalThreshold 最终阈值,2 
		         * boolean useMeanshiftGrouping 使用均值移位分组，false的效果比true的好 
		         * winStride和scale都是比较重要的参数，需要合理的设置。一个合适参数能够大大提升检测精确度，同时也不会使检测时间太长。
		         */
		        //hog.detectMultiScale(src_test, mor, mod);
		        hog.detectMultiScale(src_test, mor, mod, 0, new Size(4, 4), new Size(8, 8), 1.05, 2, false); // 调用方法进行检测  
		        //System.out.println("检测完毕！画出矩形...");
		        if(mor.toArray().length > 0){ //判断是否检测到目标对象，如果有就画矩形，没有就执行下一步  
		        	//找出所有没有嵌套的矩形框r,并放入found_filtered中,如果有嵌套的话,则取外面最大的那个矩形框放入found_filtered中  
		        	Rect[] found = mor.toArray();
		        	List<Rect> found_filtered = new ArrayList<Rect>();
		        	//先判断是否有嵌套
		        	for(int m=0;m<found.length;m++)
		        	{
		        		Rect r = found[m];
		        		int area = r.width*r.height;
		        		//System.out.println(r.x+","+r.y+";"+r.width+","+r.height);
		        		int n=0;
		        		for(;n<found.length;n++)
		        		{
		        			if(n!=m && getOverLappingArea(r, found[n])==area)//且found[n]在r内
		        				break;
		        		}
		        		if(n==found.length)
		        		{
		        			found_filtered.add(r);
		        		}
		        	}
		        	
		        	for(int j=0;j<found_filtered.size();j++)
		        	{
		        		Rect r = found_filtered.get(j);
		        		Imgproc.rectangle(src_test, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height),new Scalar(0, 0, 255), 2);
		        	}
		        	//这里是对矩形框的大小和位置进行简单的调整，不要也罢 
		        	/*for(Rect r:mor.toArray()){ // 检测到的目标转成数组形式，方便遍历  
		                r.x += Math.round(r.width*0.1);
		                r.width = (int) Math.round(r.width*0.8);
		                r.y += Math.round(r.height*0.045);
		                r.height = (int) Math.round(r.height*0.85);
		                Imgproc.rectangle(src_test, r.tl(), r.br(), new Scalar(0, 0, 255), 2); // 画出矩形  
		            }*/
		        	//将每个框截取出来作进一步训练
		        	//BufferedImage img;
		        	//for (Rect rect : mor.toArray()){
		        		//先截子图
		        		/*Rect Roi=new Rect(new Point(rect.x,rect.y),new Size(rect.width,rect.height));
		        		Mat image= dst.submat(Roi);//子图
		        		img = sp.Mat2Img(image, ".png");
		        		String picName = fName+picNum+".png";
		        		sp.saveImage(img, picName);
		        		picNum++;*/
		        		//再在原图画框
		        		//Imgproc.rectangle(src_test, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0, 0, 255), 2);
		        	//}
		            System.out.println("矩形绘制完毕！正在输出...");
		        }else{
		            System.out.println("未检测到目标！绘制矩形失败！输出原文件！");
		        }
					// 获取图片名
					String fname = img_path.get(i).trim();
					String fileName = fname
							.substring(fname.lastIndexOf("\\") + 1);
					String filename = "E:\\biyesheji\\image\\detectImage\\"
							+ fileName;//行人
					//String filename = "E:\\biyesheji\\image\\facedetectImage\\"
							//+ fileName;//人脸
					
					System.out.println(String.format("Writing %s", filename));
					Imgcodecs.imwrite(filename, src_test);
			}

		} catch (Exception e) {
			System.err.println("read err:" + e);
		}
	}
	/**
	 * 判断两个矩形的重叠面积
	 * @param a
	 * @param b
	 * @return
	 */
	public  int getOverLappingArea(Rect a,Rect b)  
    {  
        int overLappingArea = 0;  
          
        int startX = Math.min(a.x,b.x);  
        int endX = Math.max(a.x + a.width, b.x + b.width);  
        int overLappingWidth = a.width + b.width - (endX - startX);  
          
        int startY = Math.min(a.y, b.y);  
        int endY = Math.max(a.y + a.height, b.y + b.height);  
        int overLappingHeight = a.height + b.height - (endY - startY);  
          
        if(overLappingWidth <= 0 || overLappingHeight <= 0)  
        {  
            overLappingArea = 0;  
        }  
        else  
        {  
            overLappingArea = overLappingWidth * overLappingHeight;  
        }  
        return overLappingArea;  
          
    }  
	
	
	/**
	 * 判断found是否在r内
	 * @param r (x1,y1)
	 * @param found (x2,y2)
	 * @return
	 * @throws IOException 
	 */
	public boolean rMax(Rect r,Rect found) throws IOException
	{
		//x1<=x2<=x1+w1-w2,y1<=y2<=y1+h1-h2
		if(found.x >= r.x && found.x <= r.x+r.width-found.width && found.y >= r.y && found.y <= r.y+r.height-found.height)
			return true;
		else
			return false;
	}
	
	/**
	 * 保存图片和存储地址文件
	 * @param image
	 */
	public void saveImage(BufferedImage image,String imageName)
	{
		FileOutputStream fos=null;
		String file = "E:\\biyesheji\\image\\detectImage\\HardExample\\"+imageName;
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
	 * Mat转BufferedImage
	 * @param mat
	 * @param fileExtension,should be ".jpg", ".png", etc
	 * @return
	 */
	public  BufferedImage Mat2Img(Mat mat, String fileExtension) {
			MatOfByte mob = new MatOfByte();

			Imgcodecs.imencode(fileExtension, mat, mob);
			// convert the "matrix of bytes" into a byte array
			byte[] byteArray = mob.toArray();
			BufferedImage bufImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
		} catch (Exception e) {
		e.printStackTrace();
		}
			return bufImage;
	}
	
	
	
	
	
	/**
	 * 把对象转变成二进制
	 * @param obj 待转换的对象
	 * @return 返回二进制数组
	 * @throws IOException 
	 */
	public  byte[] writeInto(Object obj) throws IOException {
	    ByteArrayOutputStream bos = null;
	    ObjectOutputStream oos = null;
	    try {
	        bos = new ByteArrayOutputStream();
	        oos = new ObjectOutputStream(bos);
	        //读取对象并转换成二进制数据
	        oos.writeObject(obj);
	        return bos.toByteArray();
	    } catch (IOException e) {
	       throw e;
	    } finally {
	        if(oos != null) {
	            try {
	                oos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if(bos != null) {
	            try {
	                bos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	} 
}

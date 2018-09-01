package com.wf.Utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PictureName {
	public static void main(String[] args) throws Exception {
		File[] files = new File("E:\\biyesheji\\originalPics\\Neg").listFiles();
		//File[] files = new File("E:\\biyesheji\\originalPics\\2002\\07\\26\\big").listFiles();
		//File[] files1 = new File("E:\\biyesheji\\INRIAPerson\\Voc DataSet\\VOC DataSet\\JPEGImages").listFiles();
		//File[] files2 = new File("E:\\biyesheji\\INRIAPerson\\Voc DataSet\\VOC DataSet\\SegmentationClass").listFiles();
		//File[] files = new File("E:\\biyesheji\\INRIAPerson\\INRIAPerson\\OrignalHardExample").listFiles();
		//File[] files = new File("E:\\biyesheji\\INRIAPerson\\INRIAPerson\\HardExample").listFiles();
		//File[] files = new File("E:\\biyesheji\\INRIAPerson\\INRIAPerson\\96X160H96\\Train\\MIT_persons_jpg").listFiles();
		//File[] files = new File("E:\\biyesheji\\INRIAPerson\\INRIAPerson\\NewNegImage").listFiles();
		  //File[] files = new File("E:\\biyesheji\\INRIAPerson\\INRIAPerson\\96X160H96\\Train\\pos").listFiles();
		  //File[] files = new File("E:\\biyesheji\\INRIAPerson\\INRIAPerson\\negphoto").listFiles();
		//下次使用记得改值
		/*for (int i = 0; i < files2.length; i++)
        {
           
		  String content2 =files2[i].getName();//标签
		  String prefix = content2.substring(content2.lastIndexOf("."));
		  int num = prefix.length();
		  String content2s = content2.substring(0,content2.length()-num);
		  System.out.println("file2:"+content2s);
		  
		  for(int j = 0;j < files1.length;j++)
		  {
			  String content1 =files1[j].getName();//图片
			  String prefix1 = content1.substring(content1.lastIndexOf("."));
			  int num1 = prefix1.length();
			  String content1s = content1.substring(0,content1.length()-num1);
			  System.out.println("file1:"+content1s);
			  if(content1s.equals(content2s))
			  {
				  String f1 = "E:\\biyesheji\\INRIAPerson\\Voc DataSet\\VOC DataSet\\JPEGImages\\"+content1;
				  BufferedImage img=ImageIO.read(new File(f1));
				  saveImage(img, content1);
				  System.out.println("saving...");
			  }
		  }
		  
        }*/
		for(int i=0;i<files.length;i++)
		{
			String content ="E:\\biyesheji\\originalPics\\Neg\\"+files[i].getName()+"	"+0;
			//String content =files[i].getName();
			saveFile(content);
		}
        
		

	}
	
	 public static void saveFile(String newStr) throws Exception
	    {
		 	//String file = "E:\\biyesheji\\originalPics\\FaceNegExample.txt";
		 	String file = "E:\\biyesheji\\image\\traindata2.txt";
		 	//String file = "E:\\biyesheji\\INRIAPerson\\INRIAPerson\\OrignalHardExample.txt";
	    	//String file = "D:\\算法设计\\SVM_HOG\\NoPersonFromINRIAList.txt";
	    	//String file = "D:\\算法设计\\SVM_HOG\\INRIAPerson96X160PosList.txt";
		 	//String file = "D:\\算法设计\\SVM_HOG\\INRIANegativeImageList.txt";
		 	//String file = "D:\\算法设计\\SVM_HOG\\INRIAPersonHardNegList.txt";
		 			
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
		 * 保存图片和存储地址文件
		 * @param image
		 */
		public static void saveImage(BufferedImage image,String imageName)
		{
			FileOutputStream fos=null;
			String file = "E:\\biyesheji\\INRIAPerson\\Voc DataSet\\VOC DataSet\\ResultImage\\"+imageName;
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
}

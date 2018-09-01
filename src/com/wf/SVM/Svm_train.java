package com.wf.SVM;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;


public class Svm_train {
	/**
	 * SVM训练
	 */
	public void svm_train(){
        Integer ITERATION_NUM = 3000;
        String traintxt = "E:\\biyesheji\\image\\traindata.txt";//行人
        //String traintxt = "E:\\biyesheji\\image\\traindata2.txt";//人脸
        ArrayList<String> img_path = new ArrayList<String>();
        ArrayList<Float> img_label = new ArrayList<Float>();
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("E:\\biyesheji\\opencv\\build\\java\\x64\\opencv_java330.dll");
        System.out.println("类库加载成功·");
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(traintxt)),
                    "UTF-8"));
            //System.out.println("读取文件");
            String linetxt = null;
            Integer nline = 0;
            while((linetxt = br.readLine()) !=null){
                nline++;
                String []path_label = linetxt.split("\t");
                //System.out.println(path_label[0] + ' ' + path_label[1]);
                img_path.add(path_label[0]);
                img_label.add(Float.valueOf(path_label[1]).floatValue());
            }
            br.close();
            //一个block内有4个cell，每个cell含9维特征向量，故每个block就由4x9=36维特征向量来表征
            //128x64（高128，宽64），即有(128÷8)x(64÷8)=16x8个cell，也即有15x7个block
            //在提取每个窗口的HOG特征，则可得到105x36=3780维HOG特征向量
            Integer SAMPLE_COUNT = nline; //样本数
            Integer PICTURE_FEATURE_DIM = 3780;//图片特征维数,64*64(1764),64*128(3780),128*128(8100)
            //Integer PICTURE_FEATURE_DIM = 1764;//人脸
            Mat data_mat = new Mat(SAMPLE_COUNT, PICTURE_FEATURE_DIM, CvType.CV_32FC1);//行，列，类型
            Mat res_mat = new Mat(SAMPLE_COUNT, 1, CvType.CV_32SC1);

            //svm descriptors
            ArrayList<float[]> descriptors = new ArrayList<float[]>();
            for (Integer i=0;i<img_path.size();i++){
                System.out.println("GetHog:"+img_path.get(i));
                Mat src = Imgcodecs.imread(img_path.get(i));
                
                if(src.empty()){
                    System.out.println(img_path.get(i));
                    throw new Exception("no such picture");
                }
                if(img_label.get(i)==1)
                {
                	if(src.cols()>64 || src.rows()>128)//行人128
                	{
                		//去掉上下左右16个像素
                		Rect Roi=new Rect(new Point(16,16),new Size(64,128));//行人128
                		Mat image= src.submat(Roi);//子图
                		HOGDescriptor hog = new HOGDescriptor(new Size(64, 128), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);//行人128
                		MatOfFloat descriptorsOfMat = new MatOfFloat();
                		hog.compute(image, descriptorsOfMat);//调用计算函数
                		float[] descriptor = descriptorsOfMat.toArray();//一列
                		descriptors.add(descriptor);
                	}else{
                		HOGDescriptor hog = new HOGDescriptor(new Size(64, 128), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);//行人
                		//HOGDescriptor hog = new HOGDescriptor(new Size(64, 64), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);//人脸
                		MatOfFloat descriptorsOfMat = new MatOfFloat();
                		hog.compute(src, descriptorsOfMat);//调用计算函数
                		float[] descriptor = descriptorsOfMat.toArray();//一列
                		descriptors.add(descriptor);
                	}
                	
               }else if(img_label.get(i)==0){
                	/*
                	Mat dst = new Mat();
                	//灰度化
                	Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
                	Mat trainimg = dst.clone();
                	//归一化
                	Imgproc.resize(dst, trainimg, new Size(64, 64));*/
                	/*if(src.cols()!=64 || src.rows()!=128)
                	{
                		Imgproc.resize(src, src, new Size(64,128));
                	}*/
                	//Hog特征，窗口大小(宽，高)，块大小，块滑动增量，胞元大小，梯度方向数
                	HOGDescriptor hog = new HOGDescriptor(new Size(64, 128), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);//行人128
                	MatOfFloat descriptorsOfMat = new MatOfFloat();
                	hog.compute(src, descriptorsOfMat);//调用计算函数
                	float[] descriptor = descriptorsOfMat.toArray();//一列
                	descriptors.add(descriptor);
                }
            }

            for (Integer m = 0; m < descriptors.size(); m++) {
                for (int n = 0; n < descriptors.get(m).length; n++) {
                    data_mat.put(m, n, descriptors.get(m)[n]);//按行存储
                }
                res_mat.put(m, 0, img_label.get(m));//一列向量
            }
            
            System.out.println("开始训练。。。。。");
            SVM svm = SVM.create();
            svm.setType(SVM.C_SVC);
            svm.setKernel(SVM.LINEAR);//线性,HogDescriptor检测函数只支持线性检测
            svm.setC(0.01);
            svm.setGamma(0.5);
            svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, ITERATION_NUM, 1e-6));
            svm.train(data_mat, Ml.ROW_SAMPLE, res_mat);

            svm.save("E:\\biyesheji\\image\\svm_java");//行人
            //svm.save("E:\\biyesheji\\image\\svm_java2");//人脸
            
            System.out.println("保存模型。。");
        }catch (Exception e){
            System.err.println("read err:" + e);
        }
    }
	
	
	//自定义检测器
	public Mat myDetector()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//SVM svm = SVM.load("E:\\biyesheji\\image\\svm_java2");//人脸
		SVM svm = SVM.load("E:\\biyesheji\\image\\svm_java");//行人
		//获取支持向量
		Mat svecsmat = svm.getSupportVectors();
		int numofsv = svecsmat.rows();//支持向量个数
		System.out.println("支持向量个数："+numofsv);
		int svdim = svm.getVarCount();//特征向量维数,即HOG描述子的维数
		System.out.println("特征向量维数："+svdim);
		
		//初始化alphamat和svindex
		Mat alphaMat = Mat.zeros(1, numofsv, CvType.CV_32F);
		Mat supportVectorMat = Mat.zeros(numofsv, svdim, CvType.CV_32FC1);
		Mat resultMat = Mat.zeros(1, svdim, CvType.CV_32FC1);
		Mat svidx = Mat.zeros(1, numofsv, CvType.CV_32F);
		//获得模型中的rho
		double rho = svm.getDecisionFunction(0, alphaMat, svidx);
		System.out.println("rho:"+rho);
		alphaMat.convertTo(alphaMat, CvType.CV_32F);
		//System.out.println(alphaMat.rows()+","+alphaMat.cols());
		//将支持向量和alpha复制到对应Mat中
		supportVectorMat = svecsmat;
		//alpha*src1*src2 + beta*src3,-1 * alphamat * supportVectorMat,点乘
		Core.gemm(alphaMat, supportVectorMat, -1, new Mat(), 0, resultMat);
		//resultMat = alphaMat*supportVectorMat;
		//resultMat = alphaMat.mul(supportVectorMat);//貌似不对
		//resultMat = mutrixMulti(alphaMat, supportVectorMat);
		
		/*System.out.println(resultMat.get(0, svdim-1)[0]);
		for (int i=0; i<svdim; i++)
	    {
	        double[] value = resultMat.get(0, i);
	        value[0] *= -1;
	        resultMat.put(0, i, value[0]);
	    }
		System.out.println(resultMat.get(0, svdim-1)[0]);*/
		//定义一个大一维的向量，便于后面添加rho
		Mat myDetector = new Mat(1, svdim+1, CvType.CV_32FC1);
		for(int j=0;j<svdim;j++)
		{
			double[] value2 = resultMat.get(0, j);
			myDetector.put(0, j, value2[0]);
		}
		//int myDetectorCol = resultMat.cols();
		//添加rho
		myDetector.put(0, svdim, rho);
		System.out.println("rho:"+myDetector.get(0, svdim)[0]);
		//System.out.println("cols:"+myDetector.cols());
		//HOGDescriptor hog = new HOGDescriptor(new Size(128, 128), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);
		//hog.setSVMDetector(myDetector);
		return myDetector;
		//开始检测
		/*String testtxt = "E:\\biyesheji\\image\\traindata_cc.txt";
		ArrayList<String> img_path = new ArrayList<String>();
		//ArrayList<Float> img_label = new ArrayList<Float>();
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
					//img_label.add(Float.valueOf(path_label[1]).floatValue());
				}
				br.close();
				for (Integer i = 0; i < img_path.size(); i++) {
					Mat src_test = Imgcodecs.imread(img_path.get(i));// 读取一张图片
					if (src_test.empty()) {
						throw new Exception("no such picture");
					}
					MatOfRect mor = new MatOfRect(); // 检测完毕后会储存在这里  
			        MatOfDouble mod = new MatOfDouble();
			       // System.out.println("正在检测...");
			         参数解读： 
			         * Mat img 待检测的图像，Mat类型，
			         * MatOfRect foundLocations 用于储存检测后的序列（或者叫做矩阵） 
			         * MatOfDouble foundWeights 不清楚是什么东西
			         * double hitThreshold 命中阈值，0 
			         * Size winStride 检测步长，网上大多的步长参数为（8,8），用的是（4,4），原因是（8,8）的步长对于INRIA数据集中的图片不能很好的检测，而（4,4）可以 
			         * Size padding 这个参数没有过多了解，理解为：block的大小。大多代码推荐（16，,16）
			         * double scale 比例,1.05 
			         * double finalThreshold 最终阈值,2 
			         * boolean useMeanshiftGrouping 使用均值移位分组，false的效果比true的好 
			         
			        //hog.detectMultiScale(src_test, mor, mod);
			        hog.detectMultiScale(src_test, mor, mod, 0, new Size(4, 4), new Size(8, 8), 1.05, 2, false); // 调用方法进行检测  
			        //System.out.println("检测完毕！画出矩形...");
			        if(mor.toArray().length > 0){ // 判断是否检测到目标对象，如果有就画矩形，没有就执行下一步  
			            for(Rect r:mor.toArray()){ // 检测到的目标转成数组形式，方便遍历  
			                r.x += Math.round(r.width*0.1);
			                r.width = (int) Math.round(r.width*0.8);
			                r.y += Math.round(r.height*0.045);
			                r.height = (int) Math.round(r.height*0.85);
			                Imgproc.rectangle(src_test, r.tl(), r.br(), new Scalar(0, 0, 255), 2); // 画出矩形  
			            }
			        	for (Rect rect : mor.toArray()){
			        		Imgproc.rectangle(src_test, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0, 0, 255), 2); 
			        	}
			            System.out.println("矩形绘制完毕！正在输出...");
			        }else{
			            System.out.println("未检测到目标！绘制矩形失败！输出原文件！");
			        }
			        //获取图片名
					String fname = img_path.get(i).trim();
					String fileName = fname
							.substring(fname.lastIndexOf("\\") + 1);
					String filename = "E:\\biyesheji\\image\\detectImage\\"
							+ fileName;
					System.out.println(String.format("Writing %s", filename));
					
					Imgcodecs.imwrite(filename, src_test);
				}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
		//自定义矩阵相乘,还是有点问题
		public Mat mutrixMulti(Mat alMat,Mat supveMat)
		{
			int alMatRows = alMat.rows();
			int alMatCols = alMat.cols();
			int supveMatRows = supveMat.rows();
			int supveMatCols = supveMat.cols();
			Mat resMat=Mat.zeros(alMatRows, supveMatCols, CvType.CV_32FC1);
			
			double[][] matrix1 = new double[alMatRows][alMatCols];
			double[][] matrix2 = new double[supveMatRows][supveMatCols];
			double[][] result = new double[alMatRows][supveMatCols];
			//Mat转成矩阵
			for(int hang=0;hang<alMatRows;hang++)
			{
				for(int lie=0;lie<alMatCols;lie++)
				{
					double[] value = alMat.get(hang, lie);
					matrix1[hang][lie] = value[0];
				}
			}
			for(int h=0;h<supveMatRows;h++)
			{
				for(int l=0;l<supveMatCols;l++)
				{
					double[] value = supveMat.get(h, l);
					matrix2[h][l] = value[0];
				}
			}
			//System.out.println(matrix1[0].length+","+matrix2[0].length);
			//矩阵相乘
			for(int i=0;i<alMatRows;i++)
			{
				for(int j=0;j<supveMatCols;j++)
				{
					for(int k=0;k<alMatCols;k++)
					{
						result[i][j] += matrix1[i][k]*matrix2[k][j];
					}
				}
			}
			//System.out.println(result.length+","+result[0].length);
			//矩阵转Mat
			for(int i=0;i<alMatRows;i++)
			{
				for(int j=0;j<supveMatCols;j++)
				{
					resMat.put(i, j, result[i][j]);
				}
			}
			//System.out.println("resMat:"+resMat.rows()+","+resMat.cols());
			return resMat;
		}
		
	
	
	
	
	
//未成功实现
	public void svm_classfiler(){
        //读图测试
        String testtxt = "E:\\biyesheji\\image\\traindata_cc.txt";
		ArrayList<String> img_path = new ArrayList<String>();
		ArrayList<Float> img_label = new ArrayList<Float>();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		SVM svm = SVM.create();
		@SuppressWarnings("static-access")
		SVM model = svm.load("E:\\biyesheji\\image\\svm_java");
		
		try{
		
			BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(testtxt)), "UTF-8"));
			String linetxt = null;
			Integer nline = 0;
			while ((linetxt = br.readLine()) != null) {
				nline++;
				String[] path_label = linetxt.split("\t");
				System.out.println(path_label[0] + ' ' + path_label[1]);
				img_path.add(path_label[0]);
				img_label.add(Float.valueOf(path_label[1]).floatValue());
			}
			br.close();
			for (Integer i = 0; i < img_path.size(); i++) {
				Mat src_test = Imgcodecs.imread(img_path.get(i));// 读取一张图片
				if (src_test.empty()) {
					throw new Exception("no such picture");
				}
				Mat dst_test = new Mat();
				Imgproc.cvtColor(src_test, dst_test, Imgproc.COLOR_BGR2GRAY);
			
				//System.out.println("gray...");
				final int PICTURE_WIDTH = 150;
				final int PICTURE_HEIGHT= 200;
				int height = dst_test.height();//灰度化图片的宽高
				int width = dst_test.width();
				Svm_train st = new Svm_train();
				BufferedImage img,img2,img3;
				BufferedImage bimage = st.Mat2Img(dst_test, ".jpg");
				//System.out.println("mat2img....");
				for(int h=0;h<=height-PICTURE_HEIGHT-PICTURE_HEIGHT;h+=PICTURE_HEIGHT)
				{
					for(int w=0;w<=width-PICTURE_WIDTH-PICTURE_WIDTH;w+=PICTURE_WIDTH)
					{
						//横向截取64*64图片
						img = bimage.getSubimage(w, h, PICTURE_WIDTH, PICTURE_HEIGHT);
						Mat testimg = st.bufferedImage2Mat(img);
						//使用hog保证mat数据大小一致
						HOGDescriptor hog = new HOGDescriptor(new Size(64, 64),
								new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);
						MatOfFloat descriptorsOfMat = new MatOfFloat();
						hog.compute(testimg, descriptorsOfMat);

						float[] descriptor = descriptorsOfMat.toArray();
						Mat testmat = new Mat(1, 1764, CvType.CV_32FC1);// 一行1764列,一维向量
						for (int j = 0; j < descriptor.length; j++) {
							testmat.put(0, j, descriptor[j]);// 一行向量
						}
						//System.out.println("predict....");
						float p = model.predict(testmat);
						//System.out.println("p:"+p);
						//右侧加长框
						img2 = bimage.getSubimage(w+PICTURE_WIDTH, h, PICTURE_WIDTH, PICTURE_HEIGHT);
						Mat testimg2 = st.bufferedImage2Mat(img2);
						//使用hog保证mat数据大小一致
						HOGDescriptor hog2 = new HOGDescriptor(new Size(64, 64),
								new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);
						MatOfFloat descriptorsOfMat2 = new MatOfFloat();
						hog2.compute(testimg2, descriptorsOfMat2);

						float[] descriptor2 = descriptorsOfMat2.toArray();
						Mat testmat2 = new Mat(1, 1764, CvType.CV_32FC1);// 一行1764列,一维向量
						for (int j = 0; j < descriptor2.length; j++) {
							testmat2.put(0, j, descriptor2[j]);// 一行向量
						}
						//System.out.println("predict....");
						float p2 = model.predict(testmat2);
						
						//下侧加长框
						img3 = bimage.getSubimage(w, h+PICTURE_HEIGHT, PICTURE_WIDTH, PICTURE_HEIGHT);
						Mat testimg3 = st.bufferedImage2Mat(img3);
						//使用hog保证mat数据大小一致
						HOGDescriptor hog3 = new HOGDescriptor(new Size(64, 64),
								new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);
						MatOfFloat descriptorsOfMat3 = new MatOfFloat();
						hog3.compute(testimg3, descriptorsOfMat3);

						float[] descriptor3 = descriptorsOfMat3.toArray();
						Mat testmat3 = new Mat(1, 1764, CvType.CV_32FC1);// 一行1764列,一维向量
						for (int j = 0; j < descriptor3.length; j++) {
							testmat3.put(0, j, descriptor3[j]);// 一行向量
						}
						//System.out.println("predict....");
						float p3 = model.predict(testmat3);
						 
						//判断两个框是否是同一类别
						if(p==img_label.get(i))
						{
							if(p2==p && p3!=p)
							{
								Imgproc.rectangle(src_test, new Point(w, h), new Point(w+PICTURE_WIDTH+PICTURE_WIDTH, h+PICTURE_HEIGHT), new Scalar(0, 0, 255));
							}
							else if(p3==p && p2!=p)
							{
								Imgproc.rectangle(src_test, new Point(w, h), new Point(w+PICTURE_WIDTH, h+PICTURE_HEIGHT+PICTURE_HEIGHT), new Scalar(0, 0, 255));
							}else if(p2==p && p3==p)
							{
								Imgproc.rectangle(src_test, new Point(w, h), new Point(w+PICTURE_WIDTH+PICTURE_WIDTH, h+PICTURE_HEIGHT+PICTURE_HEIGHT), new Scalar(0, 0, 255));
							}
							// 获取图片名
							String fname = img_path.get(i).trim();
							String fileName = fname
									.substring(fname.lastIndexOf("\\") + 1);
							String filename = "E:\\biyesheji\\image\\detectImage\\"
									+ fileName;
							System.out.println(String.format("Writing %s", filename));
							
							Imgcodecs.imwrite(filename, src_test);
						}
					}
				}
				
		}
	}catch (Exception e) {
		System.err.println("read err:" + e);
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
	 * img2mat
	 * @param in
	 * @return
	 */
	public  Mat bufferedImage2Mat(BufferedImage in) {  
        BufferedImage image = new BufferedImage(in.getWidth(), in.getHeight(),  
                BufferedImage.TYPE_3BYTE_BGR);  
  
        // Draw the image onto the new buffer  
        Graphics2D g = image.createGraphics();  
        try {  
            g.setComposite(AlphaComposite.Src);  
            g.drawImage(in, 0, 0, null);  
        } finally {  
            g.dispose();  
        }  
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer())  
                .getData();  
        Mat mat = Mat.eye(image.getHeight(), image.getWidth(), CvType.CV_8UC3);  
        mat.put(0, 0, pixels);  
        return mat;  
    }  
	
	
	/*
	public  Mat BufferedImage2Mat(BufferedImage image) throws IOException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(image, "jpg", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	*/
	
	
}

package com.wf.Test;

import com.wf.SVM.Svm_predict;
import com.wf.SVM.Svm_train;

public class TestSVM {
	public static void main(String[] args) {
		 Svm_train st = new Svm_train();
		 Svm_predict sp = new Svm_predict();
	        st.svm_train();
	        //st.svm_classfiler();
	        //st.myDetector();
	        sp.svm_predict();
	}
}

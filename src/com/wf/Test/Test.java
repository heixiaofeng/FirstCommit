package com.wf.Test;

import com.wf.UI.UIDesign;

public class Test {
	public static void main(String[] args) throws Exception {
		UIDesign ui = new UIDesign();
		ui.UIDesignMethod();
		while(true)
		{
			ui.controlChose();
		}
	}
}

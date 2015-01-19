package com.damy.Utils;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.util.DisplayMetrics;

public class ResolutionSet1 {

	public static float fXpro = 1;
	public static float fYpro = 1;
	public static float fPro  = 1;
	public static int nWidth = 720;
	public static int nHeight = 1280; 
	
	public static ResolutionSet1 _instance = new ResolutionSet1(); 
	
	public ResolutionSet1() {
		
	}
	
	public void setResolution(int x, int y)
	{
		nWidth = x; nHeight = y;
		fXpro = (float)x / 720;
		fYpro = (float)y / 1280;
		fPro = Math.min(fXpro, fYpro);
	}
	

	public void iterateChild(View view) {
		if (view instanceof ViewGroup)
		{
			ViewGroup container = (ViewGroup)view;
			int nCount = container.getChildCount();
			for (int i=0; i<nCount; i++)
			{
				iterateChild(container.getChildAt(i));
			}
		}
		UpdateLayout(view);
	}
	
	void UpdateLayout(View view)
	{
		LayoutParams lp;
		lp = (LayoutParams) view.getLayoutParams();
		if ( lp == null )
			return;
		if(lp.width > 0)
			lp.width = (int)(lp.width * fXpro + 0.50001);
		if(lp.height > 0)
			lp.height = (int)(lp.height * fYpro + 0.50001);

		int leftPadding = (int)( fXpro * view.getPaddingLeft() );
		int rightPadding = (int)(fXpro * view.getPaddingRight());
		int bottomPadding = (int)(fYpro * view.getPaddingBottom());
		int topPadding = (int)(fYpro * view.getPaddingTop());
		
		view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
		
		if(lp instanceof ViewGroup.MarginLayoutParams)
		{
			ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;

            mlp.leftMargin = (int)(mlp.leftMargin * fXpro + 0.50001 );
            mlp.rightMargin = (int)(mlp.rightMargin * fXpro+ 0.50001);
            mlp.topMargin = (int)(mlp.topMargin * fYpro+ 0.50001);
            mlp.bottomMargin = (int)(mlp.bottomMargin * fYpro+ 0.50001);
		}
		
		if(view instanceof TextView)
		{
			TextView lblView = (TextView)view;
			float txtSize = (float) (fPro * lblView.getTextSize());
			lblView.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);
		}
	}
}

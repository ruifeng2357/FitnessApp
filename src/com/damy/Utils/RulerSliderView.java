package com.damy.Utils;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by KimHM on 2014-10-04.
 */
public class RulerSliderView extends View
{
	/* constant variables */
	private final int MAX_SCREEN_SCALE_COUNT = 51;
	private final double longerRate = 1.5;          // Long scale item is 1.5 times longer than small one
	private int intervalPixel = 10;             // Scale interval:10px
	private int scrScaleCount = -1;

//	private final double totalHeightRate = 100;
	private final double topMarginRate = 0;
	private final double bottomMarginRate = 0;
	private final double curValHeightRate = 20;
	private final double indicatorHeightRate = 10;
	private final double rulerHeightRate = 45;
	private final double textHeightRate = 25;

	/* variables */
	private String unitStr = "K";
	private int minLimit = 0, maxLimit = 1000;
	private int scale = 5;
	private int curValue = 0;
	private int viewWidth = 0, viewHeight = 0;
	private int midIndex = -1;

	public RulerSliderView(Context context) {
		super(context);
	}

	public RulerSliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RulerSliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setInfo(int minLimit, int maxLimit, int scale, String unitStr)
	{
		this.minLimit = minLimit;
		this.curValue = minLimit;
		this.maxLimit = maxLimit;
		this.scale = scale;
		this.unitStr = unitStr;
	}

	public void setUnitStr(String strUnit)
	{
		this.unitStr = strUnit;
	}
	
	public void setCurValue(int newVal)
	{
		if (newVal < minLimit)
			curValue = minLimit;
		else if (newVal > maxLimit)
			curValue = maxLimit;
		else
			curValue = newVal;
	}

	public int getCurValue()
	{
		if (curValue < minLimit)
			return minLimit;

		if (curValue > maxLimit)
			return maxLimit;

		return curValue;
	}


	// Handle refresh event
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		decideVariables();

		double totalRate = topMarginRate
				+ bottomMarginRate
				+ curValHeightRate
				+ indicatorHeightRate
				+ rulerHeightRate
				+ textHeightRate;

		double topMargin = viewHeight * topMarginRate / totalRate;
		double bottomMargin = viewHeight * bottomMarginRate / totalRate;
		double curValHeight = viewHeight * curValHeightRate / totalRate;
		double indicatorHeight = viewHeight * indicatorHeightRate / totalRate;
		double rulerHeight = viewHeight * rulerHeightRate / totalRate;
		double scaleTextHeight = viewHeight * textHeightRate / totalRate;

		// Draw current value text
		String szCurVal = "" + curValue + unitStr;

		Paint curTxtPaint = new Paint();
		curTxtPaint.setColor(Color.rgb(0, 160, 134));
		curTxtPaint.setTextSize((int) (curValHeight * 3 / 4));
		curTxtPaint.setTextAlign(Paint.Align.CENTER);


		Point ptCurValCenter = new Point(viewWidth / 2, (int)(topMargin + curValHeight / 2));
		canvas.drawText(szCurVal,
				ptCurValCenter.x,
				ptCurValCenter.y + curTxtPaint.getTextSize() / 2,
				curTxtPaint);

		// Draw indicator
		int nIndicatorSize = (int)(indicatorHeight * 2 / 3);
		RectF rcIndicator = new RectF(viewWidth / 2 - nIndicatorSize / 2,
				(float)(topMargin + curValHeight + indicatorHeight / 2 - nIndicatorSize / 2),
				viewWidth / 2 + nIndicatorSize / 2,
				(float)(topMargin + curValHeight + indicatorHeight / 2 + nIndicatorSize / 2));
		canvas.drawOval(rcIndicator, curTxtPaint);


		// Draw rulers
		int scaleNormalColor = Color.rgb(110, 110, 110);
		int scaleItemColor = Color.rgb(51, 51, 51);

		Paint scaleNormalPaint = new Paint();
		Paint scaleItemPaint = new Paint();

		scaleNormalPaint.setColor(scaleNormalColor);
		scaleItemPaint.setColor(scaleItemColor);
		scaleItemPaint.setTextSize((float)scaleTextHeight / 2);
		scaleItemPaint.setTextAlign(Paint.Align.CENTER);

		// Draw left scale items
		int yScalePos = (int)(topMargin + curValHeight + indicatorHeight);
		int yScaleTextPos = (int)(topMargin + curValHeight + indicatorHeight + rulerHeight);
		int nLongLen = (int)rulerHeight;
		int nNormalLen = (int)(nLongLen / longerRate);

		Point ptScaleItemCenter = new Point(0, 0);

		for (int i = Math.max(curValue - scrScaleCount / 2 - 2, minLimit);
		     i <= Math.min(curValue + scrScaleCount / 2 + 2, maxLimit);
		     i++)
		{
			boolean isLong = this.isLongItem(i);
			int xPos = getXPosFromValue(i);
			if (isLong)
			{
				canvas.drawLine(xPos, yScalePos, xPos, yScalePos + nLongLen, scaleItemPaint);

				// Draw item text
				String szItem = "" + i;
				ptScaleItemCenter = new Point(xPos, yScaleTextPos + (int)scaleTextHeight / 2);

				canvas.drawText(szItem,
						ptScaleItemCenter.x,
						ptScaleItemCenter.y + scaleItemPaint.getTextSize() / 2,
						scaleItemPaint);
			}
			else
			{
				canvas.drawLine(xPos, yScalePos, xPos, yScalePos + nNormalLen, scaleNormalPaint);
			}
		}
	}


	private int getXPosFromValue(int value)
	{
		return viewWidth / 2 + (value - curValue) * intervalPixel;
	}


	private boolean isLongItem(int value)
	{
		int nInterval = value - minLimit;
		return nInterval % scale == 0;
	}


	private void decideVariables()
	{
		// View width and height decision
		viewWidth = getWidth();
		viewHeight = getHeight();

		// Screen scale item count decision
		scrScaleCount = viewWidth / intervalPixel + 1;
		if (scrScaleCount > MAX_SCREEN_SCALE_COUNT) {
			scrScaleCount = MAX_SCREEN_SCALE_COUNT;
			intervalPixel = viewWidth / (scrScaleCount - 1);
		}

		// Middle scale item index decision
		midIndex = scrScaleCount / 2;
	}



	private int lastX = 0, lastY = 0;
	private boolean isDown = false;
	private int nDownValue = 0;

	// Handle touch event
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int x = (int)event.getX();
		int y = (int)event.getY();

		boolean isCalcNewValue = false;

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				isDown = true;
				nDownValue = curValue;
				lastX = x;
				lastY = y;
				RulerSliderView.this.getParent().requestDisallowInterceptTouchEvent(true);
				break;
			case MotionEvent.ACTION_UP:
				if (isDown)
				{
					isCalcNewValue = true;
					isDown = false;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (isDown) {
					isCalcNewValue = true;
					RulerSliderView.this.getParent().requestDisallowInterceptTouchEvent(false);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_HOVER_EXIT:
				if (isDown) {
					curValue = nDownValue;
					invalidate();
					isDown = false;
				}
				break;
			default:
				break;
		}

		if (isCalcNewValue) {
			int nXDiff = x - lastX;
			int nAbsDiff = Math.abs(nXDiff);
			int nMod = nAbsDiff % intervalPixel;
			int nScaleDiff = nAbsDiff / intervalPixel;
			if (nMod > intervalPixel / 2)
				nScaleDiff++;

			if (nXDiff > 0) {           // Left dragging
				curValue -= nScaleDiff;
			} else {
				curValue += nScaleDiff;
			}

			if (curValue < minLimit)
				curValue = minLimit;

			if (curValue > maxLimit)
				curValue = maxLimit;

			lastX = x;
			lastY = y;

			invalidate();
		}


		return true;
	}
}

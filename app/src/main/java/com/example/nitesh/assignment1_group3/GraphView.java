package com.example.nitesh.assignment1_group3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.AutoCompleteTextView;

/**
 * Created by Nitesh Gupta on 01-10-2017.
 */
public class GraphView extends View {

	public static boolean BAR = false;
	public static boolean LINE = true;

	private Paint paint;
	private float[] Xvalues;
	private float[] Yvalues;
	private float[] Zvalues;
	private String[] HAxis;
	private String[] VAxis;
	private String title;
	private boolean type;

	public GraphView(Context context, float[] Xvalues, float[] Yvalues, float[] Zvalues, String title, String[] HAxis, String[] VAxis, boolean type) {
		super(context);
		if (Xvalues == null)
			Xvalues = new float[0];
		else
			this.Xvalues = Xvalues;

		if (Yvalues == null)
			Yvalues = new float[0];
		else
			this.Yvalues = Yvalues;

		if (Zvalues == null)
			Zvalues = new float[0];
		else
			this.Zvalues = Zvalues;

		if (title == null)
			title = "";
		else
			this.title = title;

		if ( HAxis== null)
			this.HAxis = new String[0];
		else
			this.HAxis = HAxis;

		if (VAxis == null)
			this.VAxis = new String[0];
		else
			this.VAxis = VAxis;

		this.type = type;
		paint = new Paint();
	}

	public void setValues(float[] XnewValues, float[] YnewValues, float[] ZnewValues) {
		this.Xvalues = XnewValues;
		this.Yvalues = YnewValues;
		this.Zvalues = ZnewValues;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
		float hstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;

		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);


		paint.setTextAlign(Paint.Align.LEFT);
		int vers = VAxis.length - 1;
		for (int i = 0; i < VAxis.length; i++) {
			paint.setColor(Color.DKGRAY);
			float y = ((graphheight / vers) * i) + border;
			canvas.drawLine(hstart, y, width, y, paint);
			paint.setColor(Color.BLACK);
			paint.setTextSize(25.0f);
			canvas.drawText(VAxis[i], 0, y, paint);
		}
		int hors = HAxis.length - 1;
		for (int i = 0; i < HAxis.length; i++) {
			paint.setColor(Color.DKGRAY);
			float x = ((graphwidth / hors) * i) + hstart;
			canvas.drawLine(x, height - border, x, border, paint);
			paint.setTextAlign(Paint.Align.CENTER);
			if (i == HAxis.length - 1)
				paint.setTextAlign(Paint.Align.RIGHT);
			if (i == 0)
				paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(Color.WHITE);
			canvas.drawText(HAxis[i], x, height - 4, paint);
		}

		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + hstart, border - 4, paint);

		if (XMax() != XMin()) {
			//Plot X Values
			float maxX = XMax();
			float minX = XMin();
			float diffX = maxX - minX;
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = Xvalues.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < Xvalues.length; i++) {
					float val = Xvalues[i] - minX;
					float rat = val / diffX;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + hstart, (border - h) + graphheight, ((i * colwidth) + hstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = Xvalues.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < Xvalues.length; i++) {
					float val = Xvalues[i] - minX;
					float rat = val / diffX;
					float h = graphheight * rat;
					if (i > 0)
						paint.setColor(Color.GREEN);
					paint.setStrokeWidth(2.0f);

					canvas.drawLine(((i - 1) * colwidth) + (hstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (hstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}

		if (YMax() != YMin()) {
			//Plot Y Values
			float maxY = YMax();
			float minY = YMin();
			float diffY = maxY - minY;
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = Yvalues.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < Yvalues.length; i++) {
					float val = Yvalues[i] - minY;
					float rat = val / diffY;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + hstart, (border - h) + graphheight, ((i * colwidth) + hstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = Yvalues.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < Yvalues.length; i++) {
					float val = Yvalues[i] - minY;
					float rat = val / diffY;
					float h = graphheight * rat;
					if (i > 0)
						paint.setColor(Color.RED);
					paint.setStrokeWidth(2.0f);

					canvas.drawLine(((i - 1) * colwidth) + (hstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (hstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}

		if (ZMax() != ZMin()) {
			//Plot Z Values
			float Zmax = ZMax();
			float Zmin = ZMin();
			float Zdiff = Zmax - Zmin;
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = Zvalues.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < Zvalues.length; i++) {
					float val = Zvalues[i] - Zmin;
					float rat = val / Zdiff;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + hstart, (border - h) + graphheight, ((i * colwidth) + hstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = Zvalues.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < Zvalues.length; i++) {
					float val = Zvalues[i] - Zmin;
					float rat = val / Zdiff;
					float h = graphheight * rat;
					if (i > 0)
						paint.setColor(Color.WHITE);
					paint.setStrokeWidth(2.0f);

					canvas.drawLine(((i - 1) * colwidth) + (hstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (hstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}
	}

	private float XMax() {
		float high = Integer.MIN_VALUE;
		for (int i = 0; i < Xvalues.length; i++)
			if (Xvalues[i] > high)
				high = Xvalues[i];
		return high;
	}

	private float XMin() {
		float low = Integer.MAX_VALUE;
		for (int i = 0; i < Xvalues.length; i++)
			if (Xvalues[i] < low)
				low = Xvalues[i];
		return low;
	}

	private float YMax() {
		float high = Integer.MIN_VALUE;
		for (int i = 0; i < Yvalues.length; i++)
			if (Yvalues[i] > high)
				high = Yvalues[i];
		return high;
	}

	private float YMin() {
		float low = Integer.MAX_VALUE;
		for (int i = 0; i < Yvalues.length; i++)
			if (Yvalues[i] < low)
				low = Yvalues[i];
		return low;
	}

	private float ZMax() {
		float high = Integer.MIN_VALUE;
		for (int i = 0; i < Zvalues.length; i++)
			if (Zvalues[i] > high)
				high = Zvalues[i];
		return high;
	}

	private float ZMin() {
		float low = Integer.MAX_VALUE;
		for (int i = 0; i < Zvalues.length; i++)
			if (Zvalues[i] < low)
				low = Zvalues[i];
		return low;
	}

}
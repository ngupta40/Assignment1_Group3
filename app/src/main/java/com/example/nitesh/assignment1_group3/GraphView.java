package com.example.nitesh.assignment1_group3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Nitesh Gupta on 01-10-2017.
 */
public class GraphView extends View {

	public static boolean BAR = false;
	public static boolean LINE = true;

	private Paint paint;
	private float[] valuesX;
	private float[] valuesY;
	private float[] valuesZ;
	private String[] horlabels;
	private String[] verlabels;
	private String title;
	private boolean type;

	public GraphView(Context context, float[] valuesX, float[] valuesY, float[] valuesZ, String title, String[] horlabels, String[] verlabels, boolean type) {
		super(context);
		if (valuesX == null)
			valuesX = new float[0];
		else
			this.valuesX = valuesX;

		if (valuesY == null)
			valuesY = new float[0];
		else
			this.valuesY = valuesY;

		if (valuesZ == null)
			valuesZ = new float[0];
		else
			this.valuesZ = valuesZ;

		if (title == null)
			title = "";
		else
			this.title = title;

		if (horlabels == null)
			this.horlabels = new String[0];
		else
			this.horlabels = horlabels;

		if (verlabels == null)
			this.verlabels = new String[0];
		else
			this.verlabels = verlabels;

		this.type = type;
		paint = new Paint();
	}

	public void setValues(float[] newValuesX, float[] newValuesY, float[] newValuesZ) {
//       this.values = newValues;
//      System.arraycopy(newValuesX, 0, this.valuesX, 9, 1);
//      System.arraycopy(newValuesY, 0, this.valuesY, 9, 1);
//      System.arraycopy(newValuesZ, 0, this.valuesZ, 9, 1);
		this.valuesX = newValuesX;
		this.valuesY = newValuesY;
		this.valuesZ = newValuesZ;
		System.out.println(this.valuesX);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
		float horstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;
//        float max = getMax();
//        float min = getMin();
//        float diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);


		paint.setTextAlign(Paint.Align.LEFT);
		int vers = verlabels.length - 1;
		for (int i = 0; i < verlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float y = ((graphheight / vers) * i) + border;
			canvas.drawLine(horstart, y, width, y, paint);
			paint.setColor(Color.WHITE);
			paint.setTextSize(25.0f);
			canvas.drawText(verlabels[i], 0, y, paint);
		}
		int hors = horlabels.length - 1;
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float x = ((graphwidth / hors) * i) + horstart;
			canvas.drawLine(x, height - border, x, border, paint);
			paint.setTextAlign(Paint.Align.CENTER);
			if (i == horlabels.length - 1)
				paint.setTextAlign(Paint.Align.RIGHT);
			if (i == 0)
				paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(Color.WHITE);
			canvas.drawText(horlabels[i], x, height - 4, paint);
		}

		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

		if (getMaxX() != getMinX()) {
			//Plot X Values
			float maxX = getMaxX();
			float minX = getMinX();
			float diffX = maxX - minX;
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = valuesX.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < valuesX.length; i++) {
					float val = valuesX[i] - minX;
					float rat = val / diffX;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = valuesX.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < valuesX.length; i++) {
					float val = valuesX[i] - minX;
					float rat = val / diffX;
					float h = graphheight * rat;
					if (i > 0)
						paint.setColor(Color.GREEN);
					paint.setStrokeWidth(2.0f);

					canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}

		if (getMaxY() != getMinY()) {
			//Plot Y Values
			float maxY = getMaxY();
			float minY = getMinY();
			float diffY = maxY - minY;
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = valuesY.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < valuesY.length; i++) {
					float val = valuesY[i] - minY;
					float rat = val / diffY;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = valuesY.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < valuesY.length; i++) {
					float val = valuesY[i] - minY;
					float rat = val / diffY;
					float h = graphheight * rat;
					if (i > 0)
						paint.setColor(Color.RED);
					paint.setStrokeWidth(2.0f);

					canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}

		if (getMaxZ() != getMinZ()) {
			//Plot Z Values
			float maxZ = getMaxZ();
			float minZ = getMinZ();
			float diffZ = maxZ - minZ;
			paint.setColor(Color.LTGRAY);
			if (type == BAR) {
				float datalength = valuesZ.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < valuesZ.length; i++) {
					float val = valuesZ[i] - minZ;
					float rat = val / diffZ;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = valuesZ.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < valuesZ.length; i++) {
					float val = valuesZ[i] - minZ;
					float rat = val / diffZ;
					float h = graphheight * rat;
					if (i > 0)
						paint.setColor(Color.WHITE);
					paint.setStrokeWidth(2.0f);

					canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}
	}

	private float getMaxX() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < valuesX.length; i++)
			if (valuesX[i] > largest)
				largest = valuesX[i];
		return largest;
	}

	private float getMinX() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < valuesX.length; i++)
			if (valuesX[i] < smallest)
				smallest = valuesX[i];
		return smallest;
	}

	private float getMaxY() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < valuesY.length; i++)
			if (valuesY[i] > largest)
				largest = valuesY[i];
		return largest;
	}

	private float getMinY() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < valuesY.length; i++)
			if (valuesY[i] < smallest)
				smallest = valuesY[i];
		return smallest;
	}

	private float getMaxZ() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < valuesZ.length; i++)
			if (valuesZ[i] > largest)
				largest = valuesZ[i];
		return largest;
	}

	private float getMinZ() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < valuesZ.length; i++)
			if (valuesZ[i] < smallest)
				smallest = valuesZ[i];
		return smallest;
	}

}
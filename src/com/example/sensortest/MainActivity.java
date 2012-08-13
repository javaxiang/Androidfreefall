package com.example.sensortest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint({ "ParserError", "ParserError", "ParserError" })
public class MainActivity extends Activity implements
		GestureDetector.OnGestureListener {

	private SensorManager sensorMgr;

	private float x, y, z;

	ImageView iv;

	Bitmap bm;

	Handler handler;

	private float interval_time = 0.5f;

	private RoundedView rv;

	boolean ontouched = false;

	static final int PADDING_TOP = 0;

	private float window_height;

	private float window_width;

	private List<RoundedView> rvs = new ArrayList<MainActivity.RoundedView>();

	private Random rand = new Random();

	private GestureDetector gd = new GestureDetector(this);
	RelativeLayout rl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		window_height = displaymetrics.heightPixels;
		window_width = displaymetrics.widthPixels;

		rl = new RelativeLayout(this);
		rl.setBackgroundColor(Color.GRAY);

		SensorEventListener lsn = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				x = event.values[SensorManager.DATA_X];
				y = event.values[SensorManager.DATA_Y];
				z = event.values[SensorManager.DATA_Z];
				if (!ontouched && rvs.size() > 0) {
					for (int i = 0; i < rvs.size(); i++) {

						rvs.get(i).xx = rvs.get(i).getR_x();
						rvs.get(i).yy = rvs.get(i).getR_y();
						if (rvs.get(i).isTop() || rvs.get(i).isBottom()) {
							rvs.get(i).speed_y = 0;
							rvs.get(i).yy = rvs.get(i).getR_y();
						}
						if (rvs.get(i).isLeft() || rvs.get(i).isRight()) {
							rvs.get(i).speed_x = 0;
							rvs.get(i).xx = rvs.get(i).getR_x();
						}
						rvs.get(i).speed_y += interval_time * y;
						rvs.get(i).yy += rvs.get(i).speed_y * interval_time + y
								* Math.pow(interval_time, 2) / 2;

						rvs.get(i).speed_x += interval_time * (-x);
						rvs.get(i).xx += rvs.get(i).speed_x * interval_time
								+ (-x) * Math.pow(interval_time, 2) / 2;
						rvs.get(i).setR_xy(rvs.get(i).xx, rvs.get(i).yy);
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}
		};
		setContentView(rl);
		sensorMgr
				.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);

	}

	// @Override
	public boolean onTouchEvent(MotionEvent event) {
		return gd.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class RoundedView extends View {
		private float yy;

		private float xx;

		private float speed_y = 0;

		private float speed_x = 0;

		public float R_R = 30;

		private int max_r = 50;

		private int min_r = 20;

		int ball_color = Color.WHITE;

		public RoundedView(Context context) {
			super(context);
			setR_xy(0, 0);
			R_R = rand.nextInt((max_r - 1) - min_r) + min_r;
			ball_color = getRandomColor();
		}

		private float r_x, r_y;

		public void setR_xy(float r_x, float r_y) {

			if (r_x + R_R > window_width) {
				r_x = window_width - R_R;
			}
			if (r_y + R_R > window_height - PADDING_TOP) {
				r_y = window_height - PADDING_TOP - R_R;
			}
			if (r_x <= R_R) {
				r_x = R_R;
			}
			if (r_y <= R_R) {
				r_y = R_R;
			}
			this.r_x = r_x;
			this.r_y = r_y;

		}

		public float getR_x() {
			return r_x;
		}

		public float getR_y() {
			return r_y;
		}

		Paint paint = new Paint();

		public boolean isTop() {
			return r_y <= R_R;
		}

		public boolean isBottom() {
			return r_y >= window_height - PADDING_TOP - R_R;
		}

		public boolean isLeft() {
			return r_x <= R_R;
		}

		public boolean isRight() {
			return r_x >= window_width - R_R;
		}

		@Override
		protected void onDraw(Canvas canvas) {

			paint.setColor(ball_color);
			paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			canvas.drawCircle(r_x, r_y, R_R, paint);
			invalidate();
		}

		public boolean isTouchMove(float t_x, float t_y) {
			if (t_x < 0 || t_y < 0)
				return false;
			if (t_x >= r_x - R_R && t_x <= r_x + R_R) {
				if (t_y >= r_y - R_R && t_y <= r_y + R_R) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.i("onSingleTapUp", "X: " + e.getX() + " Y: " + e.getY());
		rv = new RoundedView(this);
		// rv.setBackgroundColor(Color.GRAY);
		rv.setR_xy(e.getX(), e.getY());
		if (rvs.size() < 1) {
			rvs.add(rv);
		} else {
			addAndSortView();
		}
		rl.addView(rv);
		return false;
	}

	public void addAndSortView() {
		if (rv.R_R >= rvs.get(rvs.size() - 1).R_R) {
			rvs.add(rv);
			return;
		}
		for (int i = 0; i < rvs.size(); i++) {
			if (rv.R_R <= rvs.get(i).R_R) {
				rvs.add(i, rv);
				break;
			}
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		rl.removeAllViewsInLayout();
		for (int i = rvs.size() - 1; i >= 0; i--) {
			rl.addView(rvs.get(i));
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	private int getRandomColor() {
		Random random = new Random();
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		return Color.rgb(r, g, b);
	}
}


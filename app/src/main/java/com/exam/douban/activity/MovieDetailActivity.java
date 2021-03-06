package com.exam.douban.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exam.douban.entity.MovieData;
import com.exam.douban.loader.DetailLoader;
import com.exam.douban.loader.ImgLoader;
import com.exam.douban.util.Util;
import com.exam.douban_movie_get.R;

/**
 * 
 * @author 用于详细信息显示的页面
 * 
 */

public class MovieDetailActivity extends Activity {

	private TextView mInfo;// 显示 名称 导演等信息的 文本控件
	private ImageView mImg;// 显示图片的图片控件
	private LinearLayout lin_director;// 导演的四个显示位
	private LinearLayout lin_cast;// 演员的四个显示位
	private Button btn_back;
	private Button btn_home;
	private TextView tv_dir;
	private TextView tv_cast;
	private ProgressDialog proDialog;
	private String url;// 电影的具体url
	private Util util = new Util();
	private DetailLoader detailLoader;
	private ImgLoader imgLoader;
	private MovieData movie;// 电影信息的数据实体

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		initView();
		initData();
		util.backClick(btn_back, btn_home, MovieDetailActivity.this);

		new Thread(new LoadData()).start();
		proDialog.show();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message message) {

			switch (message.arg1) {
			case 0:
				proDialog.dismiss();
				Toast.makeText(getApplicationContext(), "加载失败",
						Toast.LENGTH_SHORT).show();
				// ((Activity) getApplicationContext()).finish();
				finish();
				break;

			case 1:
				mInfo.setText(movie.getTitle() + "\n" + movie.getRating()
						+ "\n" + movie.getYear() + "\n\n" + movie.getTag());
				// 加载出图片的预设图片
				mImg.setImageResource(R.drawable.img_medium);
				// 异步加载海报
				// imgLoader.displayImg(movie.getImgUrl(),mImg);
				imgLoader = new ImgLoader(MovieDetailActivity.this,
						PersonDetailActivity.class, movie.getDirList());
				imgLoader.displayImg(movie.getImgUrl(), mImg);
				// 动态加载LinearLayout布局，显示人物头像和姓名
				imgLoader.loadLayout(lin_director);

				imgLoader = new ImgLoader(MovieDetailActivity.this,
						PersonDetailActivity.class, movie.getCastList());
				imgLoader.loadLayout(lin_cast);

				tv_cast.setVisibility(View.VISIBLE);
				tv_dir.setVisibility(View.VISIBLE);
				proDialog.dismiss();
				break;
			}

		}
	};

	// Handler h = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// Bitmap bm = (Bitmap) msg.obj;
	// int id = msg.arg1;
	// ImageView iv = (ImageView) findViewById(id);
	// iv.setImageBitmap(bm);
	// }
	// };

	/**
	 * 初始化view
	 */
	private void initView() {
		mInfo = (TextView) findViewById(R.id.tv_m);
		mImg = (ImageView) findViewById(R.id.img_m);
		lin_director = (LinearLayout) findViewById(R.id.lin_director);
		lin_cast = (LinearLayout) findViewById(R.id.lin_cast);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_home = (Button) findViewById(R.id.btn_home);
		tv_cast = (TextView) findViewById(R.id.tv_cast);
		tv_dir = (TextView) findViewById(R.id.tv_dir);

		proDialog = new ProgressDialog(this);
		// proDialog.setMessage("Loading...");
	}

	/**
	 * 初始化数据(得到url)
	 */
	private void initData() {
		Bundle extra = getIntent().getExtras();
		String id = extra.getString("id");
		url = "https://api.douban.com/v2/movie/subject/" + id;
		// url = "https://api.douban.com/v2/movie/subject/1764796";
		// System.out.println("id----"+id);
		detailLoader = new DetailLoader();

	}

	/**
	 * @author 加载数据（下载）
	 */
	private class LoadData implements Runnable {

		@Override
		public void run() {
			try {
				movie = null;
				movie = detailLoader.loadDetailInfo(url);
				Message msg = new Message();
				if (movie == null) {
					msg.arg1 = 0;
					handler.sendMessage(msg);
				}else{
				msg.arg1 = 1;
				handler.sendMessage(msg);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

package com.exam.douban.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.exam.douban.activity.MovieDetailActivity;
import com.exam.douban.activity.HistoryActivity;
import com.exam.douban.activity.MainActivity;
import com.exam.douban.activity.PersonDetailActivity;
import com.exam.douban.cache.ImgFileCache;
import com.exam.douban.entity.MovieData;
import com.exam.douban.entity.PersonData;
import com.exam.douban.entity.Properties;
import com.exam.douban.util.Util;
import com.exam.douban_movie_get.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * ֻ��ȡ10�������ͼƬ�������ļ�������
 */
public class ImgLoader {
	private Context context;
	private Util util = new Util();
//	private Handler handl;
	List<MovieData> movieList = null;
	List<PersonData> personList = null;
	ExecutorService executorService;
	Class<?> targetCls;
	private ImgFileCache fileCache= new ImgFileCache() ;

	public ImgLoader(PersonDetailActivity context, Class<?> cls,
			List<MovieData> list) {
		executorService = Executors.newFixedThreadPool(5);
		this.context = context;
//		this.handl = handl;
		this.targetCls = cls;
		movieList = list;
		

	}

	public ImgLoader(MovieDetailActivity context, Class<?> cls,
			List<PersonData> list) {
		executorService = Executors.newFixedThreadPool(5);
		this.context = context;
//		this.handl = handl;
		this.targetCls = cls;
		personList = list;
	}
	
	public ImgLoader(Context context,List<MovieData> list) {
		executorService = Executors.newFixedThreadPool(5);
//		this.handl = handl;
		movieList = list;
	}

	public ImgLoader(List<PersonData> list) {
		executorService = Executors.newFixedThreadPool(5);
//		this.handl = handl;
		personList = list;
	}

	
	/**
	 * ��̬���ز���
	 * @param list
	 * @param lin
	 */
	public void loadLayout(LinearLayout lin) {
		
		if (movieList != null) {
			for (int i = 0; i < movieList.size(); i++) {
				String id = movieList.get(i).getId();
				String title = movieList.get(i).getTitle();
				String imgurl = movieList.get(i).getImgUrl();
				ViewGroup layout = showLayout(id, imgurl, title,Properties.HISTORY_NAME_MOVIE);// ���ز��ֶ���
				lin.addView(layout);
			}
		}
		if (personList != null) {
			for (int i = 0; i < personList.size(); i++) {
				String id = personList.get(i).getId();
				String name = personList.get(i).getName();
				String imgurl = personList.get(i).getImgUrl();
				ViewGroup layout = showLayout(id, imgurl, name,Properties.HISTORY_NAME_PERSON);// ���ز��ֶ���
				lin.addView(layout);
			}
		}

	}

	// public void loadPerson(List<MovieData> works,LinearLayout lin_works) {
	// // TODO Auto-generated method stub
	// for (int i = 0; i < works.size(); i++) {
	// String id = works.get(i).getId();
	// String title = works.get(i).getTitle();
	// String imgurl = works.get(i).getImgUrl();
	// ViewGroup layout = showPerson(id, imgurl, title);// ���ز��ֶ���
	// lin_works.addView(layout);
	// }
	// }

	/**
	 * ��������-ͼƬ��LinearLayout���� ��̬����
	 * 
	 * @param id  ���ͼƬ����ת��url��Id
	 * @param img Ҫ��ʾ��ͼƬ
	 * @param context
	 * @return
	 */
	public ViewGroup showLayout(final String id, String imgurl, final String name,final String histotyType) {

		LinearLayout lin = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lin.setOrientation(LinearLayout.VERTICAL);

		final ImageView iv = new ImageView(context);
		iv.setBackgroundResource(R.drawable.img_medium);
		iv.setId(Integer.parseInt(id));

		displayImg(imgurl, iv);

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, targetCls);
				intent.putExtra("id", id);
				util.saveHistory(context, histotyType, id);
				context.startActivity(intent);
			}
		});
		lin.addView(iv, lp);// addView(view,params)params��Ӧview

		TextView tv = new TextView(context);
		tv.setTextAppearance(context, android.R.attr.textAppearanceLarge);
		tv.setText(name);
		tv.setWidth(155);
		lin.addView(tv, lp);
		Log.i("OUTPUT", "�������");
		return lin;
	}

	
	/**
	 * ����ͼƬ����Ҫ���� ���ַ�ʽ����UI��handler��RunOnUiThread()
	 * @param imgUrl
	 * @param handler
	 * @param viewId
	 */
	public void displayImg(String imgurl, ImageView iv) {
		PhotoToLoad p = new PhotoToLoad(imgurl, iv);
		executorService.submit(new PhotosLoader(p));

	}
	public void displayImg(String imgUrl, int viewId) {
		PhotoToLoad p = new PhotoToLoad(imgUrl, viewId);
		executorService.submit(new PhotosLoader(p));
	}
	/**
	 * ����ͼƬ�Ķ���
	 */
	class PhotosLoader extends Thread{
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			Bitmap bitmap = downloadImg(photoToLoad.imgUrl);
			// UI���·���һ
//			Message msg = new Message();
//			msg.obj = bitmap;
//			msg.arg1 = photoToLoad.viewId;
//			handl.sendMessage(msg);
			// ������
			// ���ַ�ʽ������ռ����ײ�����,����ˢ����ͼƬ
			 BitmapDisplayer bd = new BitmapDisplayer(bitmap, photoToLoad);
			 Activity a = (Activity) photoToLoad.imageView.getContext();
			 Log.i("bit","");
			 a.runOnUiThread(bd);

		}
	}
	/**
	 * ����ͼƬurl������bitmap
	 * @param imgurl
	 */
	public  Bitmap downloadImg(String imgUrl) {
		
		File file = fileCache.getFile(imgUrl);
//		System.out.println("file path---"+file.getPath());
		Bitmap b = null;
		
		if(file != null && file.exists()){
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				b = BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Log.e("FileNotFoundException ---",e.getMessage());
			}
		}
		if(b != null){
			return b;
		}
		
		try {
			Bitmap bitMap = null;
			URL url = new URL(imgUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(true);//�ض�����ת
			
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			CopyStream(is,fos);
//			bitMap = BitmapFactory.decodeStream(bis);
			
			FileInputStream fis = new FileInputStream(file);
			bitMap = BitmapFactory.decodeStream(fis);
			fos.close();
			return bitMap;
		} catch (IOException e) {
			Log.e("getBitmap catch Exception...", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ������UI�߳��и��½��� ���ͨ��a.runOnUiThread()����������UI�߳�
	 * Activity.runOnUiThread(Runnable)�Ǹ���UI�������һ�ַ�ʽ �Ѹ���ui�Ĵ��봴����Runnable�У�
	 * Ȼ������Ҫ����uiʱ�������Runnable���󴫸�Activity.runOnUiThread(Runnable)��
	 * ����Runnable���������ui�����б����á������ǰ�߳���UI�߳�,��ô�ж�������ִ�С�
	 * �����ǰ�̲߳���UI�߳�,�����Ƿ������¼����е�UI�߳�
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (bitmap != null){
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
			else
				System.out.println("bitmap is null");

		}
	}

	/**
	 * ����һ���򵥵Ĵ����ص�ͼƬ�����ݽṹ
	 */
	class PhotoToLoad {
		public String imgUrl;
		public int viewId;
		public ImageView imageView;

		public PhotoToLoad(String imgUrl, int viewId) {
			this.imgUrl = imgUrl;
			this.viewId = viewId;
		}

		public PhotoToLoad(String imgUrl, ImageView iv) {
			this.imgUrl = imgUrl;
			imageView = iv;
		}
	}
	public void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
			int count = 1;;
			while(count != -1){
				count = is.read(bytes,0,buffer_size);//count��ʵ�ʶ������������û������ʱ����-1
				os.write(bytes,0,count);			 //�������һ�β���1024���ֽڵ����ݣ�ʵ��������count������������count��
			}
		} catch (Exception ex) {
			Log.e("CopyStream catch Exception...", "");
		}
	}

}

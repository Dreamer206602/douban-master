package com.exam.douban.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.exam.douban.activity.MovieDetailActivity;
import com.exam.douban.activity.MainActivity;
import com.exam.douban.activity.HistoryActivity;
import com.exam.douban.activity.PersonDetailActivity;
import com.exam.douban.entity.MovieData;
import com.exam.douban.entity.PersonData;
import com.exam.douban.loader.ImgLoader;
import com.exam.douban.util.Util;
import com.exam.douban_movie_get.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieAdapter extends BaseAdapter {
	private Context context;
	private List<MovieData> ml = null;
	private List<PersonData> pl = null;
//	private boolean busy = false;
	private String info;
	private ImgLoader imgLoader;
	
	public MovieAdapter(Context context,List<MovieData> movieList) {
		this.context = context;
		ml = movieList;
	}

	public MovieAdapter(HistoryActivity context, List<PersonData> personList) {
		this.context = context;
		pl = personList;
	}

	@Override
	public int getCount() {
		if(ml!=null)
		return ml.size();
		else if(pl!=null)
			return pl.size();
		return 0;
	}
	/**
	 * ���ּ������flag
	 * @param busy
	 */
//	public void setFlagBusy(boolean busy) {
//		this.busy = busy;
//	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
//	ImageView cover;
//	Handler h = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			Bitmap bm = (Bitmap) msg.obj;
//			int id = msg.arg1;
//			View view = LayoutInflater.from(context).inflate(R.layout.search_row,null);
//			ImageView cover = (ImageView) view.findViewById(R.id.img_row_search);
//			cover.setImageBitmap(bm);
//		}};
	@Override
	/**
	 * ��������õ�����<MovieData>��ʽ���б�������search_row.xml�ķ�ʽ������activity_main.xml
	 * �õ������ļ�����search_row.xml������һ��id����������ҵ�
	 * ���ﲻ����setContentView(R.layout.activity_main)�õ�����
	 * ��inflate�����е�һ�������ж�Ӧ���࣬�ڶ���������Ҫ��null����Ȼ�޷���ʾ��
	 * @vonvertView ������ʾ�����view��
	 * @position ������Ԫ�ص�λ��
	 * ����ͼƬ��û�м�⻺�棬ÿ�ζ�����������
	 */
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.search_row,null);
		TextView title = (TextView) convertView.findViewById(R.id.tv_row_search);
		ImageView cover = (ImageView) convertView.findViewById(R.id.img_row_search);
		
		if(ml!=null){
			imgLoader = new ImgLoader(context, ml);
			MovieData movie = ml.get(position);
			title.setText(movie.getTitle()+"\n"+"���֣�"+movie.getRating()+"\n"+"��ӳʱ�䣺"+movie.getYear());
			String url = movie.getImgUrl();
			imgLoader.displayImg(url,cover);
//			imgLoader.displayImg(ml.get(position).getImgUrl());
			
			
		}
		if(pl!=null){
			imgLoader = new ImgLoader(pl);
			PersonData person = pl.get(position);
			String url = person.getImgUrl();
			imgLoader.displayImg(url,cover);
			title.setText(person.getName()+"\n"+person.getName_en()+"\n"+"�����أ�"+person.getBorn_place());
//			cover.setImageBitmap(pl.get(position).getImg());
		}
		
		
		
		return convertView;
	}
	
	public String getInfo(){
		return info;
	}
	public void setInfo(String s){
		info = s;
	}
	
}

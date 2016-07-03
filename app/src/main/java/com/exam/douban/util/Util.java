package com.exam.douban.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.exam.douban.activity.MainActivity;
import com.exam.douban.entity.MovieData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * ������
 */
public class Util {

	/**
	 * ���������������Ŀ
	 * 
	 * @param context
	 * @param type
	 * @param id
	 */
	public void saveHistory(Context context, String type, String id) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				type, Activity.MODE_APPEND);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(id, id);
		Log.i("movieInfo", id);

		if (editor.commit())
		Log.i("", "��ʷ��¼�ɹ�");

		// ��ȡ��ʷ��¼
	}

	/**
	 * �������ذ�ť�ļ�������
	 * 
	 * @param back
	 * @param home
	 * @param context
	 */
	public void backClick(Button back, Button home, final Context context) {
		// TODO Auto-generated method stub
		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				((Activity) context).finish();
				context.startActivity(new Intent(context, MainActivity.class));
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((Activity) context).finish();
			}
		});

	}

	/**
	 * �򶹰귢�����󣬷����ַ�������
	 * 
	 * @param urlstr
	 * @return
	 * @throws IOException
	 */
	public String download(String urlstr) {

		try {
			URL url = new URL(urlstr);
			System.out.println(urlstr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setReadTimeout(3000);
			connection.setRequestMethod("GET");

			String line;
			InputStreamReader isr;

			isr = new InputStreamReader(connection.getInputStream(), "UTF-8");

			if (isr != null) {
				BufferedReader buffer = new BufferedReader(isr);
				Log.i("Response Code", connection.getResponseCode() + "");
				StringBuffer sBuffer = new StringBuffer();
				while ((line = buffer.readLine()) != null) {
					sBuffer.append(line);
				}
				return sBuffer.toString();
			} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "ERROR";
	}

	/**
	 * �����������صĵ�Ӱ��Ŀ��Ϣ����棩
	 * ���������Ʒ��Ϣ��ʽ����
	 * @param s
	 * @param str
	 * @param imgType
	 *            ���صĵ�Ӱ�ߴ�
	 * @return
	 */
	public List<MovieData> parseMovieData(JSONObject s, String str,
			String imgType) {

		List<MovieData> list = new ArrayList<MovieData>();

		try {
			JSONArray total = s.getJSONArray(str);
			for (int i = 0; i < total.length(); i++) {
				MovieData movie = new MovieData();
				JSONObject m;
				if (str.equals("works")) {
					JSONObject mov = total.getJSONObject(i);
					m = mov.getJSONObject("subject");
				} else
					m = total.getJSONObject(i);
				movie.setTitle(m.getString("title"));
				movie.setId(m.getString("id"));
				movie.setYear(m.getString("year"));

				JSONObject rating = m.getJSONObject("rating");
				movie.setRating(rating.getString("average"));// ��ʾ��������

				JSONObject images = m.getJSONObject("images");
				movie.setImgUrl(images.getString(imgType));
				list.add(movie);
				movie.print();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}

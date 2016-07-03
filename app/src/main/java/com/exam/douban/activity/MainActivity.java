package com.exam.douban.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.exam.douban.adapter.MovieAdapter;
import com.exam.douban.entity.MovieData;
import com.exam.douban.entity.Properties;
import com.exam.douban.util.Util;
import com.exam.douban_movie_get.R;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends Activity {

    private Button btn_search;
    private Button btn_history;
    private EditText edt_search;
    private Handler handler;
    private ProgressDialog mpd;
    private MovieAdapter ma;
    public List<MovieData> movieList;
    private ListView lv;
	private Util util = new Util();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_search = (EditText) findViewById(R.id.et_search);
        btn_search=(Button)findViewById(R.id.btn_search);
        btn_history=(Button)findViewById(R.id.btn_history);
        lv = (ListView) findViewById(R.id.lv_show);
        
        Listener();
        
        handler=	new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                ma = new MovieAdapter(MainActivity.this,movieList);
                lv.setAdapter(ma);
                //��������ʧ
                mpd.dismiss();
            }
        };
    }
//    public void onActivityResult(int requestCode,int resultCode,Intent data)
//    {
//        mpd=new ProgressDialog(this);
//        mpd.setMessage("���Ժ����ڶ�ȡ��Ϣ...");
//        mpd.show();
//
//        String urlstr="https://api.douban.com/v2/movie/search?q="+data.getDataString()+"&count=10";
//        Log.i("OUTPUT",urlstr);
////        ���������߳����ص�Ӱ��Ϣ
//        new DownloadThread(urlstr).start();
//    }

    private void Listener() {
    	 btn_history.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View arg0) {
 				// TODO Auto-generated method stub
 				Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
 				startActivity(i);
 			}
 		});
    	btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	loading();
            	InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
            	new DownloadThread(edt_search.getText().toString()).start();
            	
            }

        });
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				MovieData m = movieList.get(position);
				
				util.saveHistory(getApplicationContext(), Properties.HISTORY_NAME_MOVIE, m.getId());
//				System.out.println("ma.getInfo()----"+m.getId());
				
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MovieDetailActivity.class);
				intent.putExtra("id", m.getId());
				startActivity(intent);
				
			}
		});
		
        
	}

	protected void loading() {
    	mpd=new ProgressDialog(this);
    	mpd.setMessage("Loading...");
    	mpd.show();
		
	}

	private class DownloadThread extends Thread
    {
        String title = null ;
        
        public DownloadThread(String title) 
        {
				this.title = title;
        }
        public void run()
        {
        	String path;
			try {
				path = URLEncoder.encode(title, "utf-8");
				String url = "http://api.douban.com/v2/movie/search?q=" + path+"&count=10";
				movieList = downloadMain(url,Properties.SEARCH_NAME_MOVIE);
				Log.i("OUTPUT", "parse completly");
				//�����߳�UI���淢��Ϣ������������Ϣ��������Ϣ���
				Message msg=new Message();
//            msg.obj=movie;
				handler.sendMessage(msg);
				Log.i("OUTPUT","msg send completly");
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
	/**
	 * ������������ı���Ϣ��ͼƬ��url
	 * @param urlstr
	 * @return ���ش��е�Ӱ����ʵ����б�arrayList��
	 */
	public List<MovieData> downloadMain(String urlstr,String type) {
		
		String sBuffer = null;
		try {
			sBuffer = util.download(urlstr);
			JSONObject json = new JSONObject(sBuffer.toString());
			movieList = util.parseMovieData(json,"subjects","small");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("OUT PUT", "download error");
		}
		Log.i("Download Data",  sBuffer.toString());
//		Log.i("Download MainLoader","success");
		return movieList;
	}
    

}

package com.exam.douban.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtil {  
    private static final String SAVETAG = "list";  
  
    /** 
     *ʹ��SharedPreferences����������͵����� ,������������ͼƬ
     * �Ƚ���������ת��Ϊ���������ݣ�Ȼ�����ض����ַ���������ַ������б��� 
     * @param object Ҫ����Ķ��� 
     * @param context 
     * @param shaPreName ������ļ��� 
     */  
    public static void saveObject(Object object,Context context,String shaPreName){  
        SharedPreferences sharedPreferences =  
                context.getSharedPreferences(shaPreName, Activity.MODE_PRIVATE);  
        SharedPreferences.Editor editor = sharedPreferences.edit();  
        List<Object> list = getObject(context,shaPreName);  
        if(list == null){  
            list = new ArrayList<Object>();  
        }  
        list.add(object);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        try {  
            ObjectOutputStream oos = new ObjectOutputStream(baos);  
            oos.writeObject(list);  
            String strList = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));  
            editor.putString(SAVETAG, strList);  
            editor.commit();  
            oos.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
            try {  
                baos.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /** 
     *�����ļ���ȡ�ô洢�����ݶ��� 
     * �Ƚ�ȡ�õ�����ת���ɶ��������飬Ȼ��ת���ɶ��� 
     * @param context 
     * @param shaPreName    ��ȡ���ݵ��ļ��� 
     * @return 
     */  
    public static List<Object> getObject(Context context,String shaPreName){  
        List<Object> list;  
        SharedPreferences sharedPreferences =  
                context.getSharedPreferences(shaPreName, Activity.MODE_PRIVATE);  
        String message  = sharedPreferences.getString(SAVETAG, "");  
        byte[] buffer = Base64.decode(message.getBytes(), Base64.DEFAULT);  
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);  
        try {  
            ObjectInputStream ois = new ObjectInputStream(bais);  
            list = (List<Object>)ois.readObject();  
            ois.close();  
            return list;  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } finally{  
            try {  
                bais.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
        return null;  
    }

}  

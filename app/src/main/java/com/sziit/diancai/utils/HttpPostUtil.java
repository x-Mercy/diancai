package com.sziit.diancai.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpPostUtil {

    private static String[] sessionId = null;

    // 请求服务器端的url
    public static String PATH = "http://10.86.50.52/Order/Main/";
    public static String IMAGE_PATH = PATH + "Image.php";
    public static String REGISTER_PATH = PATH + "Register.php";
    public static String LOGIN_PATH = PATH + "Login.php";
    public static String MENU_PATH = PATH + "ShowMenu.php";
    public static String ORDER_PATH = PATH + "AddOrder.php";

    /**
     * @param params
     *            填写的url的参数
     * @param encode
     *            字节编码
     * @return
     */
    public static String sendPostMessage(Map<String, String> params,
                                         String encode, String url_path) {
        URL url;
        // 作为StringBuffer初始化的字符串
        StringBuffer buffer = new StringBuffer();
        try {
            url = new URL(url_path);
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    // 完成转码操作
                    buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                }
                buffer.deleteCharAt(buffer.length() - 1);
            }
            // System.out.println(buffer.toString());
            // 删除掉最有一个&

            System.out.println("-->>" + buffer.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);// 表示从服务器获取数据
            urlConnection.setDoOutput(true);// 表示向服务器写数据

            if (sessionId[0] != null) {
                urlConnection.setRequestProperty("cookie",  sessionId[0]);
            }
            // 获得上传信息的字节大小以及长度
            byte[] mydata = buffer.toString().getBytes();
            // 表示设置请求体的类型是文本类型
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(mydata.length));
            // 获得输出流,向服务器输出数据
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(mydata, 0, mydata.length);
            outputStream.close();
            // 获得服务器响应的结果和状态码
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                return changeInputStream(urlConnection.getInputStream(), encode);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将一个输入流转换成指定编码的字符串
     *
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputStream(InputStream inputStream,
                                            String encode) {
        // TODO Auto-generated method stub
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = new String(outputStream.toByteArray(), encode);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取网落图片资源
     * @param url
     * @return Bitmap
     */
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //记住session
            String session_value = conn.getHeaderField("Set-Cookie" );
            sessionId = session_value.split(";");

            conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
     * 如果用户登录的时候进行ip和端口号设置，则会调用本方法
     */
    public static void setBaseUrl(String ip,String port){
        // 如果ip和端口号为空
        if(ip.equals("")&&port.equals("")){
            return;
        }
        // 如果端口号为空
        if(port.equals("")){
            PATH = "http://"+ip+"/diancai/Main/";
            // 如果端口号不为空
        }else{
            PATH = "http://"+ip+":"+port+"/diancai/Main/";
        }
        // 更新全部的URL
        IMAGE_PATH = PATH + "Image.php";
        REGISTER_PATH = PATH + "Register.php";
        LOGIN_PATH = PATH + "Login.php";
        MENU_PATH = PATH + "ShowMenu.php";
        ORDER_PATH = PATH + "AddOrder.php";
    }

}

package RetranDownload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RetransmissionDownload extends Thread{
    private String host = null;

    private String fileName = null;

    //文件已经下载的长度
    private long position;

    //文件的总长度
    private long fileSize;

    //需要中断的位置
    private long interruptPosition;

    //数据库连接
    private Connection connection;

    RetransmissionDownload(String host, String fileName, long position, long fileSize, Connection connection){
        this.host = host;
        this.fileName = fileName;
        this.position = position;
        this.fileSize = fileSize;
        this.interruptPosition = interruptPosition;
        this.connection = connection;
    }
    private  long getFileSize() {
        if (fileSize != 0) {
            return fileSize;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)new URL(host).openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("HEAD");
            conn.connect();
            System.out.println("* 连接服务器成功");
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL错误");
        } catch (IOException e) {
            System.err.println("x 连接服务器失败["+ e.getMessage() +"]");
            return -1;
        }
        return conn.getContentLengthLong();
    }
    private void setFileSize(long fileSize){
        this.fileSize = fileSize;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        RandomAccessFile randomAccessFile = null;
        PreparedStatement prepareStatement = null;
        try {

            URL url = new URL(host);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setRequestMethod("GET");
            //设置传输范围
            httpURLConnection.setRequestProperty("Range", "bytes=" + position+"-"+fileSize);

            int code = httpURLConnection.getResponseCode();
            //文件支持断点重传
            if(code==206){
                System.out.println("文件支持断点重传");
                inputStream = httpURLConnection.getInputStream();

                File file = new File(fileName);
                //构造RandomAccessFile对象,"rw"表示以读写方式打开文件
                randomAccessFile = new RandomAccessFile(file, "rw");
                //读写位置移动到上次中断的位置
                randomAccessFile.seek(position);

                byte[] buffer = new byte[1024];
                int len = -1;
                long now = System.currentTimeMillis();
                //用来记录当前下载速度
                long lastSize = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    //每三秒输出一次
                    if((System.currentTimeMillis()-now)>3000){
                        double progress = position/(double)fileSize*100;
                        double speed = (position-lastSize)/1024.0/1024/3;
                        System.out.printf("当前下载进度为%.2f%%,当前下载速度为%.2f",progress,speed);
                        now = System.currentTimeMillis();
                        lastSize = position;
                    }
                    randomAccessFile.write(buffer);
                    position+= len;
                    String sql = "UPDATE file SET wrote=? WHERE url=?";
                    prepareStatement = connection.prepareStatement(sql);
                    prepareStatement.setLong(1,position);
                    prepareStatement.setString(2,host);
                    prepareStatement.execute();

                }

                System.out.println("文件下载完毕");

            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(randomAccessFile!=null){
                    randomAccessFile.close();
                }
                if(httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void download(){
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        RandomAccessFile randomAccessFile = null;
        PreparedStatement prepareStatement = null;
        try {

            URL url = new URL(host);
             httpURLConnection = (HttpURLConnection) url.openConnection();
             httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setRequestProperty("Range", "bytes=" + position+"-"+fileSize);
//            httpURLConnection.connect();

            int code = httpURLConnection.getResponseCode();
            //文件支持断点重传
            if(code==206){
                System.out.println("文件支持断点重传");
                inputStream = httpURLConnection.getInputStream();


                File file = new File(fileName);
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(position);

                byte[] buffer = new byte[1024];
                int len = -1;
                long now = System.currentTimeMillis();
                //用来记录当前下载速度
                long lastSize = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    //每三秒输出一次
                    if((System.currentTimeMillis()-now)>3000){
                        double progress = position/(double)fileSize*100;
                        double speed = (position-lastSize)/1024.0/1024/3;
                        System.out.printf("当前下载进度为%.2f%%,当前下载速度为%.2f",progress,speed);
//                        System.out.println("当前下载进度是%.2f%"+progress+"%"+"当前下载速度为"+speed+"MB/s");
                        now = System.currentTimeMillis();
                        lastSize = position;
                    }
                    randomAccessFile.write(buffer);
                    position+= len;
                    String sql = "UPDATE file SET wrote=? WHERE url=?";
                    prepareStatement = connection.prepareStatement(sql);
                    prepareStatement.setLong(1,position);
                    prepareStatement.setString(2,host);
                    prepareStatement.execute();

                }

                System.out.println("文件下载完毕");

            }



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(randomAccessFile!=null){
                    randomAccessFile.close();
                }
                if(httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

        }

    }


}

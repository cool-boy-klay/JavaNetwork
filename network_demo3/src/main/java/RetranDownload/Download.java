package RetranDownload;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

public class Download {
    public static long getFileSize(long fileSize,String host) {

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



    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Scanner sc  = new Scanner(System.in);

        System.out.println("请输入要下载的网页或者图片的url地址");

        String host = sc.nextLine();

        System.out.println("请输入要保存的文件的名字");

        String fileName = sc.nextLine();

        MyFile myFile = new MyFile(host,fileName,0,0);

        //从数据库读取之前的文件记录，以url为唯一索引

        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;

        try {
            // 加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 获取连接
            String url = "jdbc:mysql://127.0.0.1:3306/downloadfiles";
            String user = "root";
            String password = "a561990s";
            connection = DriverManager.getConnection(url, user, password);
            // 获取statement，preparedStatement
            String sql = "select * from file where url=?";
            // 设置参数
            prepareStatement = connection.prepareStatement(sql);
            prepareStatement.setNString(1,myFile.getUrl());
            // 执行查询
            rs = prepareStatement.executeQuery();
            // 处理结果集
            //标志数据库中是否有该记录
            boolean RetranFlag = false;
            if(rs.next()){
                RetranFlag = true;
            }
            //第一次下载该文件，要在数据库中创建相关记录
            if(!RetranFlag){
                String insertSql = "insert into file(url,filename,wrote,filelength) values(?,?,?,?)";
                // 设置参数
                prepareStatement = connection.prepareStatement(insertSql);

                prepareStatement.setString(1,myFile.getUrl());
                prepareStatement.setString(2,myFile.getFileName());
                prepareStatement.setLong(3,myFile.getWrote());

                //获取要下载的文件的长度
                myFile.setFileLength(getFileSize(0,myFile.getUrl()));

                prepareStatement.setLong(4,myFile.getFileLength());

                //插入应该用execute
                if(!prepareStatement.execute()){
                    System.out.println("第一次下载该文件，向数据库成功插入了记录");
                }

            }
            //数据库中有之前的记录，设置已写记录
            else {

                myFile.setWrote(Long.parseLong(rs.getString("wrote")));
                myFile.setFileLength(Long.parseLong(rs.getString("filelength")));
                System.out.println("继续上次下载的进度:"
                        +myFile.getWrote()/(double)myFile.getFileLength()*100+"%");
            }

            RetransmissionDownload retransmissionDownload=  new RetransmissionDownload(myFile.getUrl(),myFile.getFileName(),myFile.getWrote(),myFile.getFileLength(),connection);
            retransmissionDownload.start();
            retransmissionDownload.join();



        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接，释放资源
            if (rs != null) {
                rs.close();
            }
            if (prepareStatement != null) {
                prepareStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }




    }

}

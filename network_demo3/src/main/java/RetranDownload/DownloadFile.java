package RetranDownload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadFile {
    private RandomAccessFile file;
    private FileChannel channel;
    private AtomicLong wroteSize;

    DownloadFile(String fileName ,long fileSize) throws IOException {
        //初始化文件，已写长度为0
        this.wroteSize = new AtomicLong(0);
        this.file = new RandomAccessFile(fileName,"rw");
        file.setLength(fileSize);
        channel = file.getChannel();

    }





    public static void main(String[] args) throws Exception {
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
            String sql = "select * from file";
            prepareStatement = connection.prepareStatement(sql);
            // 设置参数
//            prepareStatement.setLong(1, 1l);
            // 执行查询
            rs = prepareStatement.executeQuery();
            // 处理结果集
            while (rs.next()) {
                System.out.println(rs.getString("filename"));
//                System.out.println(rs.getString("name"));
//                System.out.println(rs.getInt("age"));
//                System.out.println(rs.getDate("birthday"));
            }
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

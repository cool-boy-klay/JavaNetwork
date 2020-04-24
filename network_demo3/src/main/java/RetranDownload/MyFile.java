package RetranDownload;

public class MyFile {
    //链接地址
    private String url;
    //文件名
    private String fileName;
    //已下载位置
    private long wrote;
    //文件总长度
    private long fileLength;

    public MyFile(String url, String fileName, long wrote, long fileLength) {
        this.url = url;
        this.fileName = fileName;
        this.wrote = wrote;
        this.fileLength = fileLength;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getWrote() {
        return wrote;
    }

    public void setWrote(long wrote) {
        this.wrote = wrote;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
}

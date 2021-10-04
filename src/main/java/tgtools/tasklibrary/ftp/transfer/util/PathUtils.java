package tgtools.tasklibrary.ftp.transfer.util;

/**
 * @author 田径
 * @date 2020-03-16 10:55
 * @desc
 **/
public class PathUtils {
    /**
     * 把盘符 和 文件路径拼接起来 得到完整的文件地址，自动判断拼接的时候前面是不是有  斜杠
     *
     * @param driverOrLpath windows系统下的盘符，或者是linux系统下的路径
     * @param filename      文件的路径 如： 二次合成\2011\IPTV\上海文广\电影\123456_变形金刚.ts
     */
    public static String joinPath(String driverOrLpath, String filename) {
        String vPath = driverOrLpath.replaceAll("[\\\\/]*$", "");
        // 把开头的 斜杠都去掉，后面统一加
        filename = filename.replaceAll("^[\\\\/]*", "");

        return vPath + "/" + filename;
    }

}

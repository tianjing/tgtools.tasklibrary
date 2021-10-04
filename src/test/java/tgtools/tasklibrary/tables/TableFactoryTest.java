package tgtools.tasklibrary.tables;

import org.junit.Test;
import tgtools.util.XmlSerialize;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class TableFactoryTest {

    @Test
    public void main() {
        //        String path ="C:/Works/DQ/javademos/binfo.config/config/config.xml";
//        String dd= FileUtil.getFileEncode(path);
//        String xmlstr= FileUtil.readFile(path,dd);
//        try {
//            Object obj = XmlSerialize.deserialize(xmlstr, "Config", ConfigInfo.class);
//            if (null != obj && obj instanceof ConfigInfo) {
//               System.out.println(obj);
//            }
//
//        } catch (Exception e) {
//            LogHelper.error("解析配置文件出错:"+xmlstr, e);
//        }


        System.out.println(System.getProperty("user.dir"));
        TableFactory.loadData("C:\\tianjing\\github\\tgtools.tasklibrary\\src\\main\\resources\\config\\demo\\");
        ByteArrayOutputStream vOutputStream = new ByteArrayOutputStream();
        try {
            XmlSerialize.serialize(vOutputStream, TableFactory.getTables().get("C:\\tianjing\\github\\tgtools.tasklibrary\\src\\main\\resources\\config\\demo\\PVC.config"));
            System.out.println(vOutputStream.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("loadData end");
    }
}
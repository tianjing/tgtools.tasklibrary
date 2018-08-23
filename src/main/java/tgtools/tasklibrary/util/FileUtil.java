package tgtools.tasklibrary.util;

import tgtools.util.StringUtil;

/**
 * Created by tian_ on 2016-08-23.
 */
public class FileUtil {
    public static String getFileExt(String p_FileName)
    {
        if(!StringUtil.isNullOrEmpty(p_FileName))
        {
           int index= p_FileName.lastIndexOf(".");
            if(p_FileName.length()>index+1)
            {
                return p_FileName.substring(index+1);
            }
        }
        return StringUtil.EMPTY_STRING;
    }

}

package tgtools.tasklibrary.tasks;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.FileUtil;
import tgtools.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 压缩文件任务
 */
public class ZipFilesTask extends Task {

    public ZipFilesTask(String p_Source, String p_Target)
    {
        m_Source =p_Source;
        m_Target=p_Target;
    }


    private String m_Source;
    private String m_Target;


    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {
        if(StringUtil.isNullOrEmpty(m_Source)|| StringUtil.isNullOrEmpty(m_Target))
        {
            LogHelper.error("无效的输入目录",new APPErrorException("source:"+m_Source+"; target:"+m_Target));
           return;
        }

        File newFile = new File(m_Target
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".zip");

        try {
            LogHelper.info("压缩文件任务开始");
            validFile(newFile);
            zipFiles(m_Source,newFile);
            LogHelper.info("压缩文件任务完成");
        } catch (APPErrorException e) {
            LogHelper.error("压缩文件任务出错",e);
        }

    }

    private void zipFiles(String files, File p_OutFile) throws APPErrorException {
        FileOutputStream out =null;
        try {
             out = new FileOutputStream(p_OutFile);
             tgtools.util.ZipCompress.writeByApacheZipOutputStream(files,out, StringUtil.EMPTY_STRING,false);
        } catch (FileNotFoundException e) {
           throw new APPErrorException("无法找到文件",e);
        } catch (IOException e) {
            throw new APPErrorException("文件错误",e);
        }
        finally {
            if(null!=out)
            {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out=null;
            }
        }
    }
    private  void validFile(File p_File) throws APPErrorException {
        if (!new File(p_File.getParent()).exists()) {
            new File(p_File.getParent()).mkdirs();
        }

        if (p_File.exists()) {
            p_File.delete();
        }

        if (!p_File.exists()) {
            try {
                p_File.createNewFile();
            } catch (IOException e) {
              throw  new APPErrorException("创建文件失败",e);
            }
        }
    }
}

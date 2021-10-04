package tgtools.tasklibrary.tasks;


import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 压缩文件任务
 *
 * @author tianjing
 */
public class ZipFilesTask extends Task {

    private String source;
    private String target;

    public ZipFilesTask(String pSource, String pTarget) {
        source = pSource;
        target = pTarget;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {
        if (StringUtil.isNullOrEmpty(source) || StringUtil.isNullOrEmpty(target)) {
            LogHelper.error("无效的输入目录", new APPErrorException("source:" + source + "; target:" + target));
            return;
        }

        File newFile = new File(target
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip");

        try {
            LogHelper.info("压缩文件任务开始");
            validFile(newFile);
            zipFiles(source, newFile);
            LogHelper.info("压缩文件任务完成");
        } catch (APPErrorException e) {
            LogHelper.error("压缩文件任务出错", e);
        }

    }

    private void zipFiles(String files, File pOutFile) throws APPErrorException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pOutFile);
            tgtools.util.ZipCompress.writeByApacheZipOutputStream(files, out, StringUtil.EMPTY_STRING, false);
        } catch (FileNotFoundException e) {
            throw new APPErrorException("无法找到文件", e);
        } catch (IOException e) {
            throw new APPErrorException("文件错误", e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
    }

    private void validFile(File pFile) throws APPErrorException {
        if (!new File(pFile.getParent()).exists()) {
            new File(pFile.getParent()).mkdirs();
        }

        if (pFile.exists()) {
            pFile.delete();
        }

        if (!pFile.exists()) {
            try {
                pFile.createNewFile();
            } catch (IOException e) {
                throw new APPErrorException("创建文件失败", e);
            }
        }
    }
}

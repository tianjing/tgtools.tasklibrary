package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.DateUtil;
import tgtools.util.StringUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 移动文件
 *
 * @author TianJing
 */
public class MoveBackFileTask extends Task {

    private String m_File;
    private String m_BackDir;
    public MoveBackFileTask(String pFile, String pBackDir) {
        m_File = pFile;
        m_BackDir = pBackDir;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext pParam) {

        if (StringUtil.isNullOrEmpty(m_File)
                || StringUtil.isNullOrEmpty(m_BackDir)) {
            return;
        }
        if (pParam.containsKey("error")) {
            m_BackDir += DateUtil.formatShortTime(new Date()) + "/error/";
        } else {
            m_BackDir += DateUtil.formatShortTime(new Date()) + "/";
        }
        File file = new File(m_File);
        File backdir = new File(m_BackDir);
        if (file.exists()) {
            if (!backdir.exists()) {
                try {
                    backdir.mkdirs();

                } catch (Exception ex) {
                    LogHelper.error("创建文件夹出错：" + m_BackDir, ex);

                }
            }
            if (backdir.exists()) {
                File newFile = new File(m_BackDir + file.getName()
                        + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                if (newFile.exists()) {
                    newFile.delete();
                }
                boolean res = false;
                for (int i = 0; i < 5; i++) {
                    if (file.renameTo(newFile)) {
                        res = true;
                        file.delete();
                        break;
                    }
                }
                if (res) {
                    LogHelper.info("移动文件完成：" + m_File);
                } else {
                    LogHelper.info("移动文件失败：" + m_File);
                }
            }


        }

    }

}

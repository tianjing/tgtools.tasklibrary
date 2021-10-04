package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.FileUtil;

import java.io.File;

/**
 * @author tianjing
 * 删除目录
 */
public class DelDirFilesTask extends Task {

    private static int ERROR_RETRY_TIMES = 3;
    private String dir;
    public DelDirFilesTask(String pDir) {
        dir = pDir;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {
        try {
            String[] strs = FileUtil.listFiles(dir, null);
            if (null != strs && strs.length > 0) {
                for (int i = 0; i < strs.length; i++) {
                    for (int j = 0; j < ERROR_RETRY_TIMES; j++) {
                        if (new File(strs[i]).delete()) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogHelper.error("删除目录所有文件出错", e);
        }
    }
}

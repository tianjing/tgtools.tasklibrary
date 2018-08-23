package tgtools.tasklibrary.tasks;

import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.FileUtil;

import java.io.File;

/**
 * 删除目录
 */
public class DelDirFilesTask extends Task {

    public DelDirFilesTask(String p_Dir)
    {
        dir=p_Dir;
    }

    private String dir;
    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {
        try{
            String[] strs= FileUtil.listFiles(dir,null);
            if(null!=strs&&strs.length>0)
            {
                for(int i=0;i<strs.length;i++)
                {
                    for(int j=0;j<3;j++) {
                        if(new File(strs[i]).delete())
                        {
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogHelper.error("删除目录所有文件出错",e);
        }
    }
}

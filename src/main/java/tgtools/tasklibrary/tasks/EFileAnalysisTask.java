package tgtools.tasklibrary.tasks;

import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.entity.EfileSection;
import tgtools.tasklibrary.entity.TableInfo;
import tgtools.tasklibrary.util.LogHelper;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析1个E文件任务
 */
public class EFileAnalysisTask extends Task {
    protected String file;
    protected TableInfo table;
    protected String split = "  ";
    protected EFileAnalysisSqlTask eFileAnalysisSqlTask = new EFileAnalysisSqlTask();

    public EFileAnalysisTask() {
    }

    public EFileAnalysisTask(String p_File, TableInfo p_Table) {
        file = p_File;
        table = p_Table;
        split = table.getSplit();
    }

    /**
     * @param source
     * @param pattern
     * @return
     */
    private static String regex(String source, String pattern) {
        Pattern p = Pattern.compile(pattern);
        //进行匹配，并将匹配结果放在Matcher对象中
        Matcher m = p.matcher(source);
        if (m.find()) {
            return m.group(1);
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String pFile) {
        file = pFile;
    }

    public TableInfo getTable() {
        return table;
    }

    public void setTable(TableInfo pTable) {
        table = pTable;
        if (null != table) {
            split = table.getSplit();
        }
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String pSplit) {
        split = pSplit;
    }

    public EFileAnalysisSqlTask geteFileAnalysisSqlTask() {
        return eFileAnalysisSqlTask;
    }

    public void seteFileAnalysisSqlTask(EFileAnalysisSqlTask peFileAnalysisSqlTask) {
        eFileAnalysisSqlTask = peFileAnalysisSqlTask;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext taskContext) {

        if (null == taskContext) {
            taskContext = new TaskContext();
        }

        try {
            LogHelper.info("开始解析E文件：" + file);
            List<EfileSection> list = null;
            if (!taskContext.containsKey("EfileSection") || !(taskContext.get("EfileSection") instanceof List)) {
                list = parseEFile();
                taskContext.put("EfileSection", list);
            }

            if (null == eFileAnalysisSqlTask) {
                eFileAnalysisSqlTask = new EFileAnalysisSqlTask();
            }

            eFileAnalysisSqlTask.setTable(table);
            eFileAnalysisSqlTask.run(taskContext);

            LogHelper.info("结束解析E文件:" + file);
        } catch (APPErrorException e) {
            LogHelper.error("解析E文件出错", e);
        }

    }

    /**
     * 解析E文件的每个标签
     *
     * @return
     * @throws APPErrorException
     */
    protected List<EfileSection> parseEFile() throws APPErrorException {

        List<EfileSection> list = new ArrayList<EfileSection>();
        String time = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringUtil.isNullOrEmpty(table.getCharset()) ? "gb2312" : table.getCharset()));

            String res = null;
            EfileSection section = null;
            while (null != (res = reader.readLine())) {
                if (res.startsWith("</")) {

                    String tag = regex(res, "^</?(.*)>$");
                    if (tag.equals(section.getTag())) {
                        if (StringUtil.isNullOrEmpty(time)) {
                            time = parseSystemTime(section);
                        }
                        LogHelper.info("解析完成节点：" + section.getTag());
                        list.add(section);
                    }
                } else if (res.startsWith("<")) {
                    section = new EfileSection();
                    String tag = regex(res, "^<?(.*)>$");
                    if (!StringUtil.isNullOrEmpty(tag)) {
                        section.setTag(tag);
                        section.setTableName(regex(tag, "table= ?(.*)"));
                    }

                } else if (res.startsWith("$")) {

                    String count = regex(res, "\\$\\s*<" + section.getTableName() + "记录数目>='(.*)'");
                    if (!StringUtil.isNullOrEmpty(count)) {
                        section.setCount(count);
                    }

                } else if (res.startsWith("@")) {
                    section.setColumnName(parseAttr(res, time));
                } else if (res.startsWith("//")) {

                } else if (res.startsWith("#")) {
                    section.addData(parseData(res, time));
                }
            }

        } catch (FileNotFoundException e) {
            throw new APPErrorException("文件未找到：" + file, e);
        } catch (IOException e) {
            throw new APPErrorException("文件读取错误：" + file, e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return list;
    }

    /**
     * @param attr
     * @param time
     * @return
     */
    protected String[] parseAttr(String attr, String time) {

        String[] attrs = attr.split(split);
        if (!StringUtil.isNullOrEmpty(time)) {
            attrs = Arrays.copyOf(attrs, attrs.length + 1);
            String name = null;
            if (table.isAlisaDataColumn()) {
                name = table.getColumnsByAlisa(table.getSystem()).getAlisa();
            } else {
                name = table.getColumnsByName(table.getSystem()).getName();
            }

            attrs[attrs.length - 1] = name;
        }

        if (null != attrs && attrs.length > 1) {
            return Arrays.copyOfRange(attrs, 1, attrs.length);
        }
        return new String[0];
    }

    /**
     * 解析E 文件数据
     *
     * @param data
     * @param time
     * @return
     */
    protected String[] parseData(String data, String time) {
        String[] datas = data.split(split);
        if (!StringUtil.isNullOrEmpty(time)) {
            datas = Arrays.copyOf(datas, datas.length + 1);
            datas[datas.length - 1] = time;
        }
        if (null != datas && datas.length > 1) {
            return Arrays.copyOfRange(datas, 1, datas.length);
        }
        return new String[0];
    }

    /**
     * 获取时间戳列所在列
     *
     * @param p_EfileSection
     * @return
     */
    protected int getTimeIndex(EfileSection p_EfileSection) {
        String[] columns = p_EfileSection.getColumnName();
        for (int i = 0; i < columns.length; i++) {
            if ("可读时间".equals(columns[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取文件生成的时间
     *
     * @param p_EfileSection
     * @return
     */
    protected String parseSystemTime(EfileSection p_EfileSection) {
        if ("system".equals(p_EfileSection.getTag())) {
            int timeindex = getTimeIndex(p_EfileSection);
            if (timeindex < 0) {
                return StringUtil.EMPTY_STRING;
            }
            List<String[]> list = p_EfileSection.getData();
            for (int i = 0; i < list.size(); i++) {

                String time = list.get(i)[timeindex];
                if (tgtools.util.StringUtil.isNullOrEmpty(time)) {
                    continue;
                }
                if (time.indexOf("_") >= 0) {
                    time = tgtools.util.StringUtil.replace(time, "_", "");
                    time = time.substring(0, 4) + "/" + time.substring(4, 6) + "/" + time.substring(6, 8) + " " + time.substring(8, 10) + ":" + time.substring(10, 12) + ":00";
                    System.out.println("111:" + time);
                    return time;
                } else if (time.indexOf("T") >= 0) {
                    time = tgtools.util.StringUtil.replace(time, "T", " ");
                    time = tgtools.util.StringUtil.replace(time, "-", "/");
                    return time;
                }

            }
        }
        return tgtools.util.StringUtil.EMPTY_STRING;
    }

}

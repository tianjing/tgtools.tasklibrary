package tgtools.tasklibrary.ftp.transfer;

/**
 * @author 田径
 * @date 2020-03-17 9:14
 * @desc
 **/
public enum FtpModelEnum {
    /**
     * 主动模式
     */
    ACTIVE ("ACTIVE"),
    /**
     * 被动模式
     */
    PASV("PASV");
    private String value;

    FtpModelEnum(String pValue) {
        value = pValue;
    }

    public String getValie() {
        return value;
    }

}

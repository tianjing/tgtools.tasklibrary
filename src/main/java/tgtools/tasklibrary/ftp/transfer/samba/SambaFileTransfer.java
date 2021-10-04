package tgtools.tasklibrary.ftp.transfer.samba;


import jcifs.CIFSException;
import jcifs.CloseableIterator;
import jcifs.SmbResource;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import tgtools.exceptions.APPErrorException;
import tgtools.tasklibrary.ftp.FtpFileInfo;
import tgtools.tasklibrary.ftp.IFTPClient;
import tgtools.tasklibrary.ftp.transfer.FileServerInfo;
import tgtools.tasklibrary.ftp.transfer.FileTransfer;
import tgtools.tasklibrary.ftp.transfer.util.PathUtils;
import tgtools.tasklibrary.ftp.transfer.util.StreamUtils;
import tgtools.util.FileUtil;
import tgtools.util.StringUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 田径
 * @date 2020-03-16 10:16
 * @desc
 **/
public class SambaFileTransfer implements FileTransfer, IFTPClient {
    private String encoding = "GBK";
    private String ip = "";
    private int port = 21;
    private String userName = "";
    private String password = "";

    public SambaFileTransfer() {
    }

    public SambaFileTransfer(FileServerInfo pFileServerInfo) {
        setIp(pFileServerInfo.getIp());
        setPort(pFileServerInfo.getPort());
        setUserName(pFileServerInfo.getUserName());
        setPassword(pFileServerInfo.getPassword());
        setEncoding(pFileServerInfo.getEncoding());

    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(String pEncoding) {
        encoding = pEncoding;
    }

    @Override
    public byte[] get(String file) throws APPErrorException {
        try (SmbFile vSmbFile = buildSmbFile(file)) {
            return StreamUtils.toByte(vSmbFile.openInputStream());
        } catch (SmbException e) {
            throw new APPErrorException("获取文件内容出错！file:" + file, e);
        }
    }

    @Override
    public void get(String pRemoteFile, String pLocalFile) throws APPErrorException {
        try (SmbFile vSmbFile = buildSmbFile(pRemoteFile)) {
            FileUtil.writeFile(pLocalFile, vSmbFile.getInputStream());
        } catch (Exception e) {
            throw new APPErrorException("获取文件内容出错！remoteFile:" + pRemoteFile + ";;localFile:" + pLocalFile, e);
        }
    }

    @Override
    public void get(String remoteFile, OutputStream outputStream) throws APPErrorException {
        throw new UnsupportedOperationException("未实现该方法");
    }

    @Override
    public void upload(String sourcefile, String targefile) throws APPErrorException {
        try {
            upload(new FileInputStream(sourcefile), targefile);
        } catch (FileNotFoundException e) {
            throw new APPErrorException("找不到文件! file:" + sourcefile, e);
        }
    }

    @Override
    public void upload(InputStream pSourcefile, String pTargefile) throws APPErrorException {
        byte[] vData = new byte[10240];
        try (SmbFile vSmbFile = buildSmbFile(pTargefile)) {
            try (InputStream vInputStream = pSourcefile) {
                try (SmbFileOutputStream vSmbFileOutputStream = vSmbFile.openOutputStream()) {
                    int vLength = 0;
                    while ((vLength = vInputStream.read(vData)) > 0) {
                        vSmbFileOutputStream.write(vData, 0, vLength);
                    }
                    vSmbFileOutputStream.flush();
                }
            }
        } catch (Exception e) {
            throw new APPErrorException("获取文件内容出错！remoteFile:" + pTargefile, e);
        }
    }

    @Override
    public void delete(String file) throws APPErrorException {
        try (SmbFile vSmbFile = buildSmbFile(file)) {
            vSmbFile.delete();
        } catch (SmbException e) {
            throw new APPErrorException("获取文件内容出错！file:" + file, e);
        }
    }

    @Override
    public void setFtpModel(String pFtpModel) {

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String pIp) {
        ip = pIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int pPort) {
        port = pPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String pUserName) {
        userName = pUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pPassword) {
        password = pPassword;
    }

    @Override
    public List<FtpFileInfo> ls(String pPath, boolean pTreeDeep) throws APPErrorException {
        if (!pTreeDeep) {
            return lsDetails(pPath);
        } else {
            List<FtpFileInfo> vResult = new ArrayList<>();
            lsAll(pPath, vResult);
            return vResult;
        }
    }

    @Override
    public String[] listFiles(String pDirName, String[] pExtName) throws APPErrorException {
        ArrayList<String> vResult = new ArrayList<>();
        List<FtpFileInfo> vFiles = lsDetails(pDirName);
        for (FtpFileInfo vFile : vFiles) {
            for (String extName : pExtName) {
                if (vFile.getIsFile() && vFile.getName().toLowerCase().endsWith(extName.toLowerCase())) {
                    vResult.add(vFile.getName());
                    break;
                }
            }
        }
        return vResult.toArray(new String[vResult.size()]);
    }

    @Override
    public void ftpLogin(String ftpIp, int ftpPort, String ftpModel, String ftpUsername, String ftpPassword) throws APPErrorException {
    }

    @Override
    public void ftpLogin() throws APPErrorException {
    }

    @Override
    public void closeFtp() throws APPErrorException {

    }

    @Override
    public boolean createFileToLocal(String pFilepath, byte[] pByteArr) throws APPErrorException {
        boolean flag = true;
        File file = new File(pFilepath);
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            flag = false;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(pByteArr, 0, pByteArr.length);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                flag = false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    @Override
    public String[] dirDetails(String pPath) throws APPErrorException {
        return listFiles(pPath, new String[0]);
    }

    @Override
    public List<FtpFileInfo> lsDetails(String pPath) throws APPErrorException {
        List<FtpFileInfo> vResult = new ArrayList<>();

        try (SmbFile vSmbFile = buildSmbFile(pPath)) {
            try (CloseableIterator<SmbResource> vFiles = vSmbFile.children()) {
                while (vFiles.hasNext()) {
                    SmbResource vSmbResource = vFiles.next();
                    try {
                        FtpFileInfo vFtpFileInfo = new FtpFileInfo();
                        String vUrl = StringUtil.removeLast(((SmbFile) vSmbResource).getURL().getFile(), '/');
                        String vName = vUrl.substring(vUrl.lastIndexOf("/") + 1);
                        vFtpFileInfo.setName(vName);
                        try {
                            vFtpFileInfo.setGroup(vSmbResource.getOwnerGroup().toDisplayString());
                            vFtpFileInfo.setOwner(vSmbResource.getOwnerUser().toDisplayString());
                        } catch (Exception e) {
                        }
                        vFtpFileInfo.setIsFile(vSmbResource.isFile());
                        vFtpFileInfo.setPath(pPath);
                        vFtpFileInfo.setPermissions((vSmbResource.canRead() ? "r" : StringUtil.EMPTY_STRING) + (vSmbResource.canWrite() ? "w" : StringUtil.EMPTY_STRING));
                        vFtpFileInfo.setSize(vSmbResource.length());
                        vFtpFileInfo.setLastModified(new Date(((SmbFile) vSmbResource).getLastModified()));
                        vResult.add(vFtpFileInfo);
                    } catch (Exception e) {
                        throw new APPErrorException("获取信息出错！name:" + vSmbResource.getName(), e);
                    }
                }
            } catch (CIFSException e) {
                throw new APPErrorException("获取 目录结构 children 出错！pPath:" + pPath, e);
            }
        }

        return vResult;
    }

    protected void lsAll(String pPath, List<FtpFileInfo> pResult) throws APPErrorException {
        List<FtpFileInfo> vFiles = lsDetails(pPath);
        pResult.addAll(vFiles);
        for (FtpFileInfo vFile : vFiles) {
            if (!vFile.getIsFile()) {
                try {
                    lsAll(PathUtils.joinPath(vFile.getPath(), vFile.getName()).toString(), pResult);
                } catch (Exception e) {
                    //忽略递归错误，忽略权限问题无法读取。
                    System.out.println("");
                }
            }
        }
    }

    protected SmbFile buildSmbFile(String pPath) throws APPErrorException {
        try {
            return new SmbFile(getSmbUrl(pPath)
                    , SingletonContext.getInstance().withCredentials(new NtlmPasswordAuthenticator(getUserName(), getPassword())));
        } catch (MalformedURLException e) {
            throw new APPErrorException("Smb File 创建失败！path:" + pPath, e);
        }
    }

    protected String getSmbUrl(String pPath) {
        return "smb://" + getIp() + pPath;
    }

    @Override
    public void close() throws IOException {

    }

}

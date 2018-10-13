package cn.joylau.code;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @goal make-win-service
 * @phase package
 */
public class WindowsServiceMojo extends AbstractMojo {
    /**
     * @parameter property="project.build.directory"
     * @required
     */
    private File targetDir;

    /**
     * @parameter property="project.basedir"
     * @required
     * @readonly
     */
    private File baseDir;
    /**
     * @parameter property="project.build.sourceDirectory"
     * @required
     * @readonly
     */
    private File sourceDir;
    /**
     * @parameter property="project.build.testSourceDirectory"
     * @required
     * @readonly
     */
    private File testSourceDir;

    /** @parameter property="project.groupId"
     *  @required
     */
    private String groupId;

    /** @parameter property="project.artifactId"
     *  @required
     */
    private String artifactId;

    /** @parameter property="project.version"
     *  @required
     */
    private String version;

    /** @parameter property="project.description"
     */
    private String description;

    /**
     * @parameter property="arguments"
     */
    private String[] arguments;

    /**
     * @parameter property="vmOptions"
     */
    private String vmOptions;

    /**
     * @parameter property="programArguments"
     */
    private String programArguments;


    private static final String EXE_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/service.exe";
    private static final String XML_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/service.xml";
    private static final String CONFIG_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/service.exe.config";
    private static final String README_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/reamdme.txt";

    public void execute() {
        getLog().info("开始生成 Windows Service 必要的文件");
        try {
            /*创建文件夹*/
            File distDir = new File(targetDir, File.separator + "dist");
            if (distDir.exists()) {
                try {
                    FileUtils.deleteDirectory(distDir);
                } catch (IOException e) {
                    getLog().error("删除目录失败！请检查文件是否在使用");
                    e.printStackTrace();
                }
            }
            FileUtils.mkdir(distDir.getPath());
            File logDir = new File(distDir,File.separator + "logs");
            FileUtils.mkdir(logDir.getPath());

            /*下载文件*/
            FileUtils.copyURLToFile(new URL(README_FILE_URL), new File(distDir, File.separator + "readme.txt"));
            FileUtils.copyURLToFile(new URL(XML_FILE_URL), new File(distDir,File.separator+getJarPrefixName()+".xml"));
            FileUtils.copyURLToFile(new URL(EXE_FILE_URL), new File(distDir,File.separator+getJarPrefixName()+".exe"));
            FileUtils.copyURLToFile(new URL(CONFIG_FILE_URL), new File(distDir,File.separator+getJarPrefixName()+".exe.config"));
            FileUtils.copyFile(new File(targetDir.getPath() + File.separator + getJarName()), new File(distDir, File
                    .separator + getJarName()));

            convert(new File(distDir.getPath()+File.separator+getJarPrefixName()+".xml"));
            createBat(distDir, "intsall.bat", "install");
            createBat(distDir, "unintall.bat", "uninstall");
            createBat(distDir, "start.bat", "start");
            createBat(distDir, "stop.bat", "stop");
            createBat(distDir, "restart.bat", "restart");

            getLog().info("正在制作压缩包....");
            String zipDir = targetDir.getPath() + File.separator + getJarPrefixName() + ".zip";
            ZipUtils.zip(distDir.getPath(), zipDir);

            getLog().info("正在清除临时文件....");
            FileUtils.deleteDirectory(distDir);
            getLog().info("制作完成，文件:" + zipDir);
        } catch (Exception e) {
            getLog().error("制作Windows Service 失败：",e);
        }
    }


    /**
     * 属性转化
     * @param xmlFile xml文件
     */
    private void convert(File xmlFile){
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(xmlFile);
            Element root = document.getRootElement();
            root.element("id").setText(artifactId);
            root.element("name").setText(getJarPrefixName());
            root.element("description").setText(null == description ? "暂无描述" : description);
            if (arguments.length > 0) {
                getLog().warn("arguments 参数设置已过期,参数配置可能不会生效,请分别设置 vmOptions 参数 和 programArguments 参数 [https://github.com/JoyLau/joylau-springboot-daemon-windows]");
            }
            String vm_options = StringUtils.isEmpty(vmOptions) ? " " : " " + vmOptions + " ";
            String program_arguments = StringUtils.isEmpty(programArguments) ? "" : " " + programArguments;
            root.element("arguments").setText(vm_options + "-jar " + getJarName() +  program_arguments);
            saveXML(document,xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存 XML 文件
     * @param document 文档
     * @param xmlFile xml文件
     */
    private void saveXML(Document document, File xmlFile){
        try {
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), StandardCharsets.UTF_8));
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param outDri   输出目录
     * @param fileName 文件名
     * @param text     命令文本
     */
    private void createBat(File outDri, String fileName, String text) {
        if (!outDri.exists()) {
            FileUtils.mkdir(outDri.getPath());
        }
        File file = new File(outDri, fileName);
        try (FileWriter w = new FileWriter(file)) {
            w.write("@echo off\n" +
                    "%1 mshta vbscript:CreateObject(\"Shell.Application\").ShellExecute(\"cmd.exe\",\"/c %~s0 ::\",\"\",\"runas\",1)(window.close)&&exit\n" +
                    "%~dp0" + getJarPrefixName() + ".exe " + text + "\n" +
                    "echo The " + getJarPrefixName() + " service current state:\n" +
                    "%~dp0" + getJarPrefixName() + ".exe status\n" +
                    "pause");
        } catch (IOException e) {
//            throw new MojoExecutionException("Error creating file ", e);
            e.printStackTrace();
        }
        // ignore
    }

    /**
     * 获取jar包前缀名
     * @return String
     */
    private String getJarPrefixName() {
        return artifactId + "-" + version;
    }

    /**
     * 获取jar包全名
     * @return String
     */
    private String getJarName() {
        return getJarPrefixName() + ".jar";
    }
}

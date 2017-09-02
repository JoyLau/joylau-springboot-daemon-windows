package cn.joylau.code;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.util.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

/**
 * @goal make-win-service
 * @phase process-sources
 */
public class WindowsServiceMojo extends AbstractMojo {
    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File targetDir;

    /**
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File baseDir;
    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDir;
    /**
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    private File testSourceDir;

    /** @parameter expression="${project.groupId}"
     *  @required
     */
    private String groupId;

    /** @parameter expression="${project.artifactId}"
     *  @required
     */
    private String artifactId;

    /** @parameter expression="${project.version}"
     *  @required
     */
    private String version;

    /** @parameter expression="${project.description}"
     *  @required
     */
    private String description;


    private static final String EXE_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/service.exe";
    private static final String XML_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/service.xml";
    private static final String CONFIG_FILE_URL = "http://image.joylau.cn/plugins/joylau-springboot-daemon-windows/service.exe.config";

    public void execute() {
        getLog().info("开始生成 Windows Service 必要的文件");

        getLog().info(version);
        try {
            /*创建文件夹*/
            File distDir = new File(targetDir, File.separator + "dist");
            if (distDir.exists()) {
                FileUtils.deleteDirectory(distDir);
            }
            FileUtils.mkdir(distDir.getPath());
            File logDir = new File(distDir,File.separator + "logs");
            FileUtils.mkdir(logDir.getPath());

            /*下载文件*/
            FileUtils.copyURLToFile(new URL(XML_FILE_URL), new File(distDir,File.separator+getJarPrefixName()+".xml"));
            FileUtils.copyURLToFile(new URL(EXE_FILE_URL), new File(distDir,File.separator+getJarPrefixName()+".exe"));
            FileUtils.copyURLToFile(new URL(CONFIG_FILE_URL), new File(distDir,File.separator+getJarPrefixName()+".exe.config"));
            FileUtils.copyFile(new File(targetDir.getPath()+getJarName()),new File(distDir,getJarName()));


            convert(new File(distDir.getPath()+File.separator+getJarPrefixName()+".xml"));
        } catch (Exception e) {
            e.printStackTrace();
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
            root.element("id").setText("hehe");
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
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8"));
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJarPrefixName(){
        return File.separator+artifactId+"-"+version;
    }

    private String getJarName(){
        return File.separator+artifactId+"-"+version+".jar";
    }
}

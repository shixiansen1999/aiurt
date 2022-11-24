package com.aiurt.modules.git.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 * @author fgw
 */
@Slf4j
public class GitProperties {

    private static Properties properties;

    private static void initProperties(){
        if(Objects.isNull(properties)) {
            properties = loadProperties("git.properties");
        }
    }

    /**
     * 获取属性
     * @param propertiesName
     * @return
     */
    public static String init(String propertiesName){
        initProperties();
        String property = properties.getProperty(propertiesName);
        if(StringUtils.isNotBlank(property)){
            property = property.trim();
        }
        return property;
    }


    private static Properties loadProperties(String path) {
        Properties props = new Properties();
        InputStreamReader input = null;
        InputStream io = null;
        try {
            io = getResourceAsStream(path);
            if(null!=io){
                input = new InputStreamReader(io,"UTF-8");
                props.load(input);
            }
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(input!=null){
                    input.close();
                }
                if(io!=null){
                    io.close();
                }
            } catch (IOException e) {
                //
            }

        }
        return props;
    }

    /**
     * 获取输入流
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(final String path){
        ClassLoader classLoader = null;
        InputStream input=null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            input = classLoader.getResourceAsStream(path);
        } catch (Exception e) {
        }

        if(input==null){
            classLoader = GitProperties.class.getClassLoader();
            input = classLoader.getResourceAsStream(path);
        }

        if(input==null){
            try {
                input = new FileInputStream(path);
            } catch (Exception e) {
                System.out.println("读取失败:"+e.getMessage());
            }
        }

        if(input==null){
            try {
                input = new URL(path).openStream();
            } catch (Exception e) {
                System.out.println("读取失败:"+e.getMessage());
            }
        }
        return input;
    }

    public static String getVersion() {
        String version = null;
        try {
            String rootPath = System.getProperty("user.dir");
            MavenXpp3Reader reader = new MavenXpp3Reader();

            String myPom = rootPath + File.separator + "pom.xml";

            Model read = reader.read(new FileReader(myPom));

            version = read.getVersion();
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        } finally {
        }
        return version;
    }
}

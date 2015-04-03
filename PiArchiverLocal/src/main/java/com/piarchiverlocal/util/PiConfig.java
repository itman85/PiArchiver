package com.piarchiverlocal.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by phannguyen-pc on 3/23/2014.
 */
public class PiConfig {
    static Logger log4j = Logger.getLogger(PiConfig.class.getName());
    public static final String DOWNLOAD_PAGE_LOCAL_NAME = "\\LocalPage.html";
    public static final String DOWNLOAD_PAGE_ORIGINAL_NAME = "\\OriginalPage.html";
    public static final String EXTRACT_TEXT_FILE_NAME = "\\WebPageText.txt";
    private static PiConfig instance = null;
    private String archiverFolderPath;
    private String embeddeddbPath;
    protected PiConfig() {
        // Exists only to defeat instantiation.
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "piconfig.properties";
            input = PiConfig.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                log4j.error("Unable to find config file:" + filename);
                archiverFolderPath="";
                return;
            }
            log4j.info("Read OK config file:" + filename);
            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out
            archiverFolderPath = prop.getProperty("piarchiverfolder");
            embeddeddbPath = prop.getProperty("embedded_databasepath");
        } catch (IOException ex) {
            log4j.error(ex.getStackTrace());
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    log4j.error(e.getStackTrace());
                }
            }
        }
    }
    public static PiConfig getInstance() {
        if(instance == null) {
            instance = new PiConfig();
        }
        return instance;
    }

    public String getArchiverFolderPath() {
        return archiverFolderPath;
    }

    public void setArchiverFolderPath(String archiverFolderPath) {
        this.archiverFolderPath = archiverFolderPath;
    }

    public String getEmbeddeddbPath() {
        return embeddeddbPath;
    }

    public void setEmbeddeddbPath(String embeddeddbPath) {
        this.embeddeddbPath = embeddeddbPath;
    }
}

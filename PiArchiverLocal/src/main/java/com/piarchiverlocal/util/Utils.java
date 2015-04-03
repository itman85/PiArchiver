package com.piarchiverlocal.util;


import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by phannguyen-pc on 3/22/2014.
 */
public class Utils {
    static Logger log4j = Logger.getLogger(Utils.class.getName());

    public static StringBuilder ReadLargeFile(String filepath) throws IOException {
        File file = new File(filepath);
        FileInputStream fis = new FileInputStream(file);
        StringBuilder strBlder = new StringBuilder();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(fis, Charset.defaultCharset()));
            char[] buffer = new char[1024];
            int readNBytes;
            while ((readNBytes = reader.read(buffer, 0, buffer.length)) > 0) {
                strBlder.append(buffer, 0, readNBytes);
            }

        } catch (IOException e) {
            log4j.error(e.getMessage()+" "+e.getCause()+" "+e.getStackTrace());
        }finally {
            fis.close();
        }
        return  strBlder;
    }
}

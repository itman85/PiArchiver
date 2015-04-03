import com.piarchiverlocal.process.webpage.Crawler;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by phannguyen-pc on 3/23/2014.
 */
public class MainTest {
    private static String rootPath = "C:/Users/phan/AppData/Roaming/testpiarchiver/";
    private static String url = "http://dantri.com.vn/giao-duc-khuyen-hoc.htm";
    public static void main(String args[]) throws IOException {
        UUID uniqueFolder = UUID.randomUUID();
        if(Crawler.createFolder(rootPath + uniqueFolder))   {
            System.out.println("------------START------------------");
            String filename = Crawler.DownloadWebpage(url,rootPath+uniqueFolder);
            Crawler.DownloadResourcesInWebpage(rootPath+uniqueFolder,filename);
            System.out.println("------------END------------------");
        }
    }

    private static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

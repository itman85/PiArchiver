import com.piarchiverlocal.embeddeddb.ArchiverDAL;
import com.piarchiverlocal.pojo.LinkCloud;
import com.piarchiverlocal.process.webpage.Crawler;
import com.piarchiverlocal.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by phannguyen-pc on 3/23/2014.
 */
public class SubTest {
    private static String rootPath = "C:/Users/phan/AppData/Roaming/testpiarchiver/";
    private static String url = "https://www.dantri.com.vn:8080/giao-duc-khuyen-hoc.htm";
    private static String filename = "C:\\Users\\phan\\AppData\\Roaming\\piarchiver\\a1391ac7-0949-42f7-987f-5d68740fc749\\webpagetext.txt";
    static String catejson = "[{\"title\":\"cate2\",\"newid\":0,\"key\":\"2\"},{\"title\":\"my news favourite\",\"children\":[{\"title\":\"book\",\"newid\":0,\"key\":\"8\"},{\"title\":\"newspaper\",\"newid\":0,\"key\":\"9\"}],\"newid\":0,\"key\":\"7\"},{\"title\":\"node01\",\"children\":[{\"title\":\"Item 301 chuyen ve di dong\",\"children\":[{\"title\":\"iOS\",\"newid\":0,\"key\":\"10\"},{\"title\":\"Android\",\"newid\":0,\"key\":\"11\"}],\"newid\":0,\"key\":\"5\"}],\"newid\":0,\"key\":\"12\"},{\"title\":\"node02\",\"children\":[{\"title\":\"Item 101\",\"children\":[{\"title\":\"film\",\"newid\":0,\"key\":\"3\"},{\"title\":\"tech\",\"children\":[{\"title\":\"web\",\"newid\":0,\"key\":\"6\"}],\"newid\":0,\"key\":\"4\"}],\"newid\":0,\"key\":\"1\"}],\"newid\":0,\"key\":\"13\"},{\"title\":\"node03\",\"children\":[{\"title\":\"node03con1\",\"newid\":0,\"key\":\"15\"},{\"title\":\"node04con1\",\"newid\":0,\"key\":\"16\"}],\"newid\":0,\"key\":\"14\"},{\"title\":\"node04\",\"newid\":0,\"key\":\"17\"}]";
    public static void main(String args[]) throws IOException {
        //System.out.println("------------START------------------");
        //ArchiverDAL.instance().InsertOrUpdateCategoryData("{test:testing}");
        LinkCloud obj = new LinkCloud();
        obj.setCloudid(1);
        obj.setUrl("http://dantri.com.vn/");
        obj.setNote("báo dân trí khuyến học tin tức thời sự");
        obj.setCategoryid(10);
        obj.setCreatedon("12/03/2014 10:20:21 +0007");
        ArchiverDAL.instance().UpdateLinkData(obj);
        System.out.println("------------END------------------");
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

    public static String getDomainName(String url) {
        URL urlObj = null;
        try {
            urlObj = new URL(url);
            String domain = urlObj.getHost();
            String protocol = urlObj.getProtocol();
            return protocol+"://"+domain+(urlObj.getPort()!=-1?":"+urlObj.getPort():"");
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }
}

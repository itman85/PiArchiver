package com.piarchiverlocal.process.webpage;

import com.piarchiverlocal.util.PiConfig;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by phannguyen-pc on 3/22/2014.
 */
public class Crawler {
    static Logger log = Logger.getLogger(Crawler.class.getName());


    public static boolean createFolder(String folderPath){
        File files = new File(folderPath);
        if (!files.exists()) {
            if (files.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static String DownloadWebpage(String webpageurl,String webpageFolder) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String filename = webpageFolder + PiConfig.DOWNLOAD_PAGE_ORIGINAL_NAME;
        String tempFilename = webpageFolder + "\\temp.html";
        try {

            url = new URL(webpageurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Cast shouldn't fail


            HttpURLConnection.setFollowRedirects(true);
            // allow both GZip and Deflate (ZLib) encodings
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            //  To also set the user-agent add the following code:
            //  conn.setRequestProperty ( "User-agent", "my agent name");

            String encoding = conn.getContentEncoding();


            // create the appropriate stream wrapper based on
            // the encoding type
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(conn.getInputStream());
            } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
                is = new InflaterInputStream(conn.getInputStream(),
                        new Inflater(true));
            } else {
                is = conn.getInputStream();
            }

            ConvertInputStreamToFile(is,filename);
            //pre refine before download resource
            changeRelativePathInHtmlPage(filename,getDomainName(webpageurl),tempFilename);

        } catch (MalformedURLException mue) {
            log.error(webpageurl +" : "+mue.getMessage() +" "+ mue.getCause()+" "+mue.getStackTrace());
        } catch (IOException ioe) {
            log.error(webpageurl + " : " +ioe.getMessage() +" "+ ioe.getCause()+" "+ioe.getStackTrace());
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                log.error(ioe.getStackTrace());
            }
        }
        return tempFilename;
    }

    private static  void changeRelativePathInHtmlPage(String htmlFile,String urlDomain,String tempFilename) throws IOException {
        File inputhtml = new File(htmlFile);
        Document htmlDoc = Jsoup.parse(inputhtml, "UTF-8");

        //for tag A
        Elements tagAs = htmlDoc.select("a");
        for (Element el : tagAs) {
            String href = el.attr("href");
            System.out.println("href attribute is : "+href);
            if(href.trim().startsWith("/")){//this is relative path and need to change
                el.attr("href",urlDomain + href.trim());
            }
        }
        //for links
        Elements links = htmlDoc.select("link");
        for (Element el : links) {
            String href = el.attr("href");
            System.out.println("href attribute is : "+href);
            if(href.trim().startsWith("/")){//this is relative path and need to change
                el.attr("href",urlDomain + href.trim());
            }
        }

        //for scripts
        Elements scripts = htmlDoc.select("script");
        for (Element el : scripts) {
            String src = el.attr("src");
            System.out.println("src attribute is : "+src);
            if(src.trim().startsWith("/")){//this is relative path and need to change
                el.attr("src",urlDomain + src.trim());
            }
        }

        //for images
        Elements imgs = htmlDoc.getElementsByTag("img");
        for (Element el : imgs) {
            String src = el.attr("src");
            System.out.println("src attribute is : "+src);
            if(src.trim().startsWith("/")){//this is relative path and need to change
                el.attr("src",urlDomain + src.trim());
            }
        }
        writeStringToFile(tempFilename,htmlDoc.html());
    }
    public static void DownloadResourcesInWebpage(String webpageFolder,String webpageFilePath) throws IOException     {
        File inputhtml = new File(webpageFilePath);//this temp file so it will be deleted after finishing process
        Document doc = Jsoup.parse(inputhtml, "UTF-8");

        writeStringToFile(webpageFolder + PiConfig.EXTRACT_TEXT_FILE_NAME,doc.select("body").text());
        if(!createFolder(webpageFolder+"\\Resources\\"))
        {
            log.error("Fail to create folder "+webpageFolder+"\\Resources\\");
            return ;
        }
        Elements img = doc.getElementsByTag("img");

        for (Element el : img) {

            //for each element get the srs url
            String src = el.attr("src");
            System.out.println("src attribute is : "+src);
            String filenameImg = getFileFromUrl(src,webpageFolder+"\\Resources\\");
            if(filenameImg !=null && filenameImg!="")
            {
                el.attr("src","./Resources/"+filenameImg);
            }
        }

        Elements links = doc.select("link");
        Elements scripts = doc.select("script");
        for (Element el : links) {
            String src = el.attr("href");
            // System.out.println("href attribute is : "+src);
            String filenameImg = getFileFromUrl(src,webpageFolder+"\\Resources\\",".css");
            if(filenameImg !=null && filenameImg!="")
            {
               el.attr("href","./Resources/"+filenameImg);
            }
        }
        for (Element el : scripts) {
            String src = el.attr("src");
            // System.out.println("src attribute is : "+src);
            String filenameImg = getFileFromUrl(src,webpageFolder+"\\Resources\\",".js");
            if(filenameImg !=null && filenameImg!="")
            {
                el.attr("src","./Resources/"+filenameImg);
            }
        }
        writeStringToFile(webpageFolder + PiConfig.DOWNLOAD_PAGE_LOCAL_NAME,doc.html());
        inputhtml.delete();

    }

    private static  String getFileFromUrl(String srcUrl,String SubResfolderPath,String ... ext)
    {
        String name = null;
        try{
            int whitespaceIdx = srcUrl.trim().indexOf(' ');
            if(whitespaceIdx>0)
                srcUrl = srcUrl.trim().substring(0,whitespaceIdx);
            int indexname = srcUrl.lastIndexOf("/");

            if (indexname == srcUrl.length()) {
                srcUrl = srcUrl.substring(1, indexname);
            }

            indexname = srcUrl.lastIndexOf("/");
            name = srcUrl.substring(indexname+1, srcUrl.length());


            if(!isFilenameValid(name))
            {
                if(ext!=null && ext.length>0 && !ext[0].equals(""))
                {
                    name = UUID.randomUUID().toString()+ext[0];
                }
                else
                    name = null;
            }
            if(name!=null && name!=""){
                 System.out.println(name);
                downloadFileFromUrl(srcUrl,SubResfolderPath+ name);
            }
        }catch (MalformedURLException ex)
        {
            log.error(SubResfolderPath+" : "+ex.getMessage()+" " +ex.getCause() +" "+ ex.getStackTrace());
            name = null;
        }catch (IOException ex)
        {
            log.error(SubResfolderPath+" : "+ex.getMessage()+" "+ex.getMessage()+" "+ ex.getStackTrace());
            name = null;
        }
        finally {

        }
        return name;
    }

    public static void downloadFileFromUrl(String webpageurl, String localFilename) throws IOException {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String filename = localFilename;

        try {

            url = new URL(webpageurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Cast shouldn't fail


            HttpURLConnection.setFollowRedirects(true);
            // allow both GZip and Deflate (ZLib) encodings
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("accept-charset", "UTF-8");
            //  To also set the user-agent add the following code:
            //  conn.setRequestProperty ( "User-agent", "my agent name");
            //urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=utf-8");
            String encoding = conn.getContentEncoding();


            // create the appropriate stream wrapper based on
            // the encoding type
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(conn.getInputStream());
            } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
                is = new InflaterInputStream(conn.getInputStream(),
                        new Inflater(true));
            } else {
                is = conn.getInputStream();
            }
            ConvertInputStreamToFile(is,filename);

        } catch (MalformedURLException mue) {
            log.error(localFilename+" : "+ mue.getMessage()+" "+ mue.getCause()+ " " + mue.getStackTrace());
        } catch (IOException ioe) {
            log.error(localFilename+" : "+ ioe.getMessage()+" "+ ioe.getCause()+ " " + ioe.getStackTrace());
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        //  return filename;
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

    public static void ConvertInputStreamToFile(InputStream inputStream,String filePath) {

        OutputStream outputStream = null;

        try {

            // write the inputStream to a FileOutputStream
            outputStream =
                    new FileOutputStream(new File(filePath));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            // System.out.println("Done!");

        } catch (IOException e) {
            log.error(filePath+ " : "+e.getMessage()+" "+e.getCause()+" "+e.getStackTrace());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getStackTrace());
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private static void writeStringToFile(String filePath,String filecontent) {
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter( filePath));
            writer.write( filecontent);

        }
        catch ( IOException e)
        {
            log.error(filePath+ " : "+e.getMessage()+" "+e.getCause()+" "+e.getStackTrace());
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
                log.error(e.getStackTrace());
            }
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
            log.error(url + " " + e.getMessage());
        }
        return "";
    }
}

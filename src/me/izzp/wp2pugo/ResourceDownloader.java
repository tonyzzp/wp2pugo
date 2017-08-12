package me.izzp.wp2pugo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzp on 2017-08-12.
 */

class ResourceDownloader {
    public static void downloadResource(String surl) throws IOException {
        URL url = new URL(surl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("文件下载失败:" + status);
        }
        String path = url.getPath();
        File file = new File("media" + path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        InputStream in = conn.getInputStream();
        FileOutputStream out = new FileOutputStream(file);
        byte[] buff = new byte[1024 * 4];
        int len = 0;
        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
        out.close();
        in.close();
        conn.disconnect();
    }

    private static boolean isFile(String url) {
        boolean flag = !url.endsWith(".htm") && !url.endsWith(".html");
        if (url.startsWith("http://") || url.startsWith("https://")) {
            int index = url.lastIndexOf("/");
            int index2 = url.indexOf(".", index);
            flag = flag && index2 > -1;
        }
        return flag;
    }

    public static List<String> resolveLinks(Article article) {
        List<String> list = new ArrayList<>();
        Document doc = Jsoup.parse(article.content);
        if (doc != null) {
            Elements elements = doc.select("img");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String src = element.attr("src");
                if (!Util.isEmpty(src) && isFile(src)) {
                    list.add(src);
                }
            }
            elements = doc.select("a");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String src = element.attr("href");
                if (!Util.isEmpty(src) && isFile(src)) {
                    list.add(src);
                }
            }
        }
        return list;
    }
}

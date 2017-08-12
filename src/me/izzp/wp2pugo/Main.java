package me.izzp.wp2pugo;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, WrongFormatException, SAXException, IOException {
        String path = checkArgs(args);
        if (path == null) {
            return;
        }
        File file = new File(path);
        if (!file.isFile()) {
            System.err.println("文件不存在 : " + file.getAbsolutePath());
            return;
        }
        Parser parser = new Parser();
        parser.parse(file);
        System.out.println("共解析出文章数量:" + parser.getArticles().size());
        System.out.println("其实解析出错的文章数:" + parser.getArticleErrors());
        Author author = parser.getAuthor();
        if (author != null) {
            System.out.println("博客作者:" + author.name + "  " + author.email);
        }
        System.out.println("正在生成md文件...");
        for (Article article : parser.getArticles()) {
            System.out.println("生成>" + article.title);
            MdCreator.create(article, author);
        }
        System.out.println("生成md完成");
        System.out.println("开始寻找需要下载的资源文件");
        List<String> all = new ArrayList<>();
        for (Article article : parser.getArticles()) {
            List<String> list = ResourceDownloader.resolveLinks(article);
            all.addAll(list);
        }
        String baseUrl = parser.getBaseUrl();
        if (baseUrl != null) {
            baseUrl = baseUrl.replace("http://", "").replace("https://", "");
        }
        Iterator<String> it = all.iterator();
        while (it.hasNext()) {
            String url = it.next();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                if (!url.contains(baseUrl)) {
                    it.remove();
                }
            }
        }
        System.out.println("共找到需要下载的资源个数 : " + all.size());
        System.out.println("开始下载");
        for (String url : all) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = parser.getBaseUrl() + url;
            }
            System.out.print("下载 : " + url);
            int retry = 3;
            boolean success = false;
            while (!success && retry > 0) {
                try {
                    ResourceDownloader.downloadResource(url);
                    success = true;
                    System.out.print("  成功");
                } catch (Exception e) {
                    retry--;
                    System.out.print("  失败，开始第 " + (3 - retry) + " 次重试");
                }
            }
            System.out.println();
        }
        System.out.println("全部完成！");
    }

    private static String checkArgs(String[] args) {
        if (args == null) {
            showHelp();
            return null;
        }
        if (args.length != 1) {
            showHelp();
            return null;
        }
        String path = args[0];
        if (path.length() == 0) {
            showHelp();
            return null;
        }
        return path;
    }

    private static void showHelp() {
        System.out.println("请传入xml文件路径，如: java -jar wp2pugo.jar wp.xml");
    }
}

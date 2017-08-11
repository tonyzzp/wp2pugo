package me.izzp.wp2pugo;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

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
        System.out.println("生成完成");
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

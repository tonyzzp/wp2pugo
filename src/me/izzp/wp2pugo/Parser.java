package me.izzp.wp2pugo;

import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Parser {

    private Author author;
    private List<Article> articles = new ArrayList<>();
    private int articleErrors = 0;
    private String baseSiteUrl;
    private String link;

    public void parse(File file) throws IOException, ParserConfigurationException, SAXException, WrongFormatException {
        InputStream in = new FileInputStream(file);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(in);
        parse(doc);
    }

    private void parse(Document doc) throws WrongFormatException {
        Element root = doc.getDocumentElement();
        if (!"rss".equals(root.getTagName())) {
            throw new WrongFormatException();
        }
        root = XmlHelper.getFirstElement(root, "channel");
        if (root == null) {
            throw new WrongFormatException();
        }
        link = XmlHelper.getElementText(root, "link");
        baseSiteUrl = XmlHelper.getElementText(root, "wp:base_site_url");
        author = findAuthor(root);
        NodeList nodeList = root.getElementsByTagName("item");
        if (nodeList == null || nodeList.getLength() == 0) {
            throw new WrongFormatException("没有找到文章");
        }
        int count = nodeList.getLength();
        for (int i = 0; i < count; i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                try {
                    Article article = resolveArticle(element);
                    articles.add(article);
                } catch (WrongArticleException e) {
                    articleErrors++;
                }
            }
        }
    }

    private Author findAuthor(Element root) {
        Element element = XmlHelper.getFirstElement(root, "wp:author");
        if (element == null) {
            return null;
        }
        Author author = new Author();
        author.account = XmlHelper.getElementText(element, "wp:author_login");
        author.name = XmlHelper.getElementText(element, "wp:author_display_name");
        author.email = XmlHelper.getElementText(element, "wp:author_email");
        return author;
    }

    private Article resolveArticle(Element element) throws WrongArticleException {
        String title = XmlHelper.getElementText(element, "title");
        if (Util.isEmpty(title)) {
            throw new WrongArticleException("没有标题");
        }
        Date postDate = Util.parseDate(XmlHelper.getElementText(element, "wp:post_date"));
        String author = XmlHelper.getElementText(element, "dc:creator");
        String desc = XmlHelper.getElementText(element, "description");
        String content = XmlHelper.getElementText(element, "content:encoded");
        if (Util.isEmpty(content)) {
            throw new WrongArticleException("没有内容");
        }
        String excerpt = XmlHelper.getElementText(element, "excerpt:encoded");
        String postName = XmlHelper.getElementText(element, "wp:post_name");
        String category = "";
        List<String> tags = new ArrayList<>();
        NodeList list = element.getElementsByTagName("category");
        if (list != null && list.getLength() > 0) {
            int count = list.getLength();
            for (int i = 0; i < count; i++) {
                Node node = list.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    String domain = ele.getAttribute("domain");
                    String text = ele.getTextContent();
                    if ("post_tag".equals(domain)) {
                        tags.add(text);
                    } else if ("category".equals(domain)) {
                        category = text;
                    }
                }
            }
        }
        Article article = new Article();
        article.title = title;
        article.desc = desc;
        article.content = content;
        article.auth = author;
        article.date = postDate;
        article.slug = postName;
        article.category = category;
        article.excerpt = excerpt;
        article.tags = tags;
        return article;
    }

    public Author getAuthor() {
        return author;
    }

    public int getArticleErrors() {
        return articleErrors;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public String getBaseUrl() {
        if (!Util.isEmpty(baseSiteUrl)) {
            return baseSiteUrl;
        } else {
            return link;
        }
    }
}

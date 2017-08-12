package me.izzp.wp2pugo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MdCreator {
    public static void create(Article article, Author author) throws IOException {
        File dir = new File("post");
        File file = null;
        if (article.date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(article.date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            dir = new File(dir, String.format("%04d/%02d", year, month + 1));
        }
        if (!Util.isEmpty(article.slug)) {
            file = new File(dir, article.slug + ".md");
        } else {
            if (article.date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                file = new File(dir, sdf.format(article.date) + ".md");
            } else {
                file = new File(dir, System.currentTimeMillis() + ".md");
            }
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "utf-8"));
        writer.write("```toml\r\n");
        writeAttr(writer, "title", article.title);
        writeAttr(writer, "slug", article.slug);
        writeAttr(writer, "desc", article.desc);
        writeAttr(writer, "date", sdf.format(article.date));
        writeAttr(writer, "author", article.auth != null ? article.auth : author != null ? author.name : "");
        if (article.tags != null && article.tags.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (String tag : article.tags) {
                sb.append("\"").append(tag).append("\"").append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("]");
            writer.write("tags = ");
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.write("```\r\n\r\n");
        if (!Util.isEmpty(article.excerpt)) {
            writer.write(article.excerpt);
            writer.write("\r\n\r\n<!--more-->\r\n\r\n");
        }
        writer.write(article.content);
        writer.flush();
        writer.close();
    }

    private static void writeAttr(BufferedWriter writer, String key, String value) throws IOException {
        value = value.replace("\"", "'").replace("\\", "/");
        writer.write(key);
        writer.write(" = \"");
        writer.write(value);
        writer.write("\"\r\n");
    }
}

package me.izzp.wp2pugo;

class WrongArticleException extends Exception {
    public WrongArticleException(String message) {
        super("文章格式不正确：" + message);
    }
}
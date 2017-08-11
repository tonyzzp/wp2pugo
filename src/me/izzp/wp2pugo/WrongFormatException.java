package me.izzp.wp2pugo;

class WrongFormatException extends Exception {

    public WrongFormatException() {
        super("不是合法的WordPress导出文件");
    }

    public WrongFormatException(String message) {
        super(message);
    }
}

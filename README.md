## wp2pugo
a tool conver wordpress export xml file to markdown format for [pugo](http://pugo.io/)


## 使用方法
```
java -jar wp2pugo.jar wp.xml
```

会自动将文章转换为pugo可以识别的md格式，放到post目录内。
同时如果文章里链接的有博客域名下的其他资源(图片、文件等)，也会自动下载下来，放到media目录内

## 下载
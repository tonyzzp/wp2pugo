package me.izzp.wp2pugo;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XmlHelper {
    public static Element getFirstElement(Element root, String tagName) {
        NodeList list = root.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            Node node = list.item(0);
            if (node instanceof Element) {
                Element element = (Element) node;
                return element;
            }
        }
        return null;
    }

    public static String getElementText(Element parent, String tagName) {
        Element element = getFirstElement(parent, tagName);
        if (element != null) {
            return element.getTextContent();
        }
        return null;
    }
}

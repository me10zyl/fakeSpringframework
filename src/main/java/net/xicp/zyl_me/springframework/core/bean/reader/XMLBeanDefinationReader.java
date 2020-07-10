package net.xicp.zyl_me.springframework.core.bean.reader;

import net.xicp.zyl_me.springframework.core.bean.BeanDefination;
import net.xicp.zyl_me.springframework.core.context.Environment;
import org.dom4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLBeanDefinationReader implements BeanDefinationReader {

    private String configLocation;
    private Environment environment;

    public XMLBeanDefinationReader(String configLocation, Environment environment) {
        this.configLocation = configLocation;
        this.environment = environment;
    }

    @Override
    public List<BeanDefination> readBeanDefinations() {
        List<BeanDefination> list = null;
        File file = new File(configLocation);
        String line = null;
        StringBuilder xml = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            while ((line = br.readLine()) != null) {
                xml.append(line);
            }
            Document document = DocumentHelper.parseText(xml.toString());
            environment.setXmlDocument(document);
            list = handleBeanDefination(document);
        } catch (IOException | DocumentException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return list;
    }

    private List<BeanDefination> handleBeanDefination(Document document) {
        List<BeanDefination> list = new ArrayList<>();
        Element root = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Node> beans = root.selectNodes("//*[name()='bean']");
        Iterator<Node> iterator = beans.iterator();
        while (iterator.hasNext()) {
            Element bean = (Element) iterator.next();
            Attribute id = bean.attribute("id");
            Attribute _class = bean.attribute("class");
            BeanDefination beanDefination = new BeanDefination();
            beanDefination.setBeanName(id.getText());
            beanDefination.setClazz(_class.getText());
            list.add(beanDefination);
        }
        return list;
    }

}

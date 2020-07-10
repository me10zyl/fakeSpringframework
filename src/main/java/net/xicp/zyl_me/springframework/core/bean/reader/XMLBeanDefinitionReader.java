package net.xicp.zyl_me.springframework.core.bean.reader;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.context.Environment;
import org.dom4j.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {

    private String configLocation;
    private Environment environment;
    private Document document;

    public XMLBeanDefinitionReader(String configLocation, Environment environment) {
        this.configLocation = configLocation;
        this.environment = environment;
    }

    public List<String> getComponentScans(){
        List<String> result = new ArrayList<>();
        List<Element> list = document.selectNodes("//*[name()='context:component-scan']");
        for (Element ele : list) {
            Attribute attribute = ele.attribute("base-package");
            result.add(attribute.getText());
        }
        return result;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> list = null;
        File file = new File(configLocation);
        String line = null;
        StringBuilder xml = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            while ((line = br.readLine()) != null) {
                xml.append(line);
            }
            Document document = DocumentHelper.parseText(xml.toString());
            this.document = document;
            environment.setXmlDocument(document);
            list = handleBeanDefinition(document);
        } catch (IOException | DocumentException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return list;
    }

    private List<BeanDefinition> handleBeanDefinition(Document document) {
        List<BeanDefinition> list = new ArrayList<>();
        Element root = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Node> beans = root.selectNodes("//*[name()='bean']");
        Iterator<Node> iterator = beans.iterator();
        while (iterator.hasNext()) {
            Element bean = (Element) iterator.next();
            Attribute id = bean.attribute("id");
            Attribute _class = bean.attribute("class");
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanName(id.getText());
            beanDefinition.setClazz(_class.getText());
            list.add(beanDefinition);
        }
        return list;
    }

}

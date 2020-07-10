package net.xicp.zyl_me.springframework.util;

import net.xicp.zyl_me.springframework.core.bean.BeanDefinition;
import net.xicp.zyl_me.springframework.core.bean.annotation.Autowired;
import net.xicp.zyl_me.springframework.core.bean.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {

    public List<BeanDefinition> doScan(String packagePath) {
        List<BeanDefinition> list = new ArrayList<>();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String dirPath = packagePath.replace('.', '\\');
        URL resource = contextClassLoader.getResource(dirPath);
        try {
            Path path = Paths.get(resource.toURI());
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File classFile = file.toFile();
                    Path relative = path.relativize(file);
                    BeanDefinition bean = new BeanDefinition();
                    bean.setClazz(packagePath + "." + relative.toString()
                            .replaceAll("\\\\", ".")
                            .replaceAll("\\.class", ""));
                    try {
                        Class<?> aClass = Class.forName(bean.getClazz());
                        if(!aClass.isInterface() && aClass.isAnnotationPresent(Component.class)){
                            bean.setBeanName(generateBeanName(classFile));
                            list.add(bean);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    return super.visitFile(file, attrs);
                }
            });
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String generateBeanName(File classFile) {
        return classFile.getName().replaceAll("\\.java", "");
    }
}

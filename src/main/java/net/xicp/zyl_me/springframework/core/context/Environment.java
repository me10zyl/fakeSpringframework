package net.xicp.zyl_me.springframework.core.context;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Environment {
    @Getter
    private Map<String, Object> container = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private Document xmlDocument;
}

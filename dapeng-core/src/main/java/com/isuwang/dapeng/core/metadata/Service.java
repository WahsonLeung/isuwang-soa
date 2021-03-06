package com.isuwang.dapeng.core.metadata;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Service {

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ServiceMeta {
        public String version;
        public int timeout;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    @XmlAttribute
    public String namespace;
    @XmlAttribute
    public String name;
    public String label;
    public String doc;

    public ServiceMeta meta;

    @XmlElementWrapper(name = "methods")
    @XmlElement(name = "method")
    public List<Method> methods;

    @XmlElementWrapper(name = "structs")
    @XmlElement(name = "struct")
    public List<Struct> structDefinitions;

    @XmlElementWrapper(name = "enums")
    @XmlElement(name = "tenum")
    public List<TEnum> enumDefinitions;

    private transient boolean initialized = false;
    private transient Map<String, Struct> structByName = new java.util.HashMap<String, Struct>();

    public synchronized void postInit() {
        if (initialized) return;
        for (Struct it : structDefinitions) {
            structByName.put(it.namespace + "." + it.name, it);
        }
        initialized = true;
    }

    public Struct lookupStruct(String qualifiedName) {
        postInit();
        return structByName.get(qualifiedName);
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public ServiceMeta getMeta() {
        return meta;
    }

    public void setMeta(ServiceMeta meta) {
        this.meta = meta;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public List<Struct> getStructDefinitions() {
        return structDefinitions;
    }

    public void setStructDefinitions(List<Struct> structDefinitions) {
        this.structDefinitions = structDefinitions;
    }

    public List<TEnum> getEnumDefinitions() {
        return enumDefinitions;
    }

    public void setEnumDefinitions(List<TEnum> enumDefinitions) {
        this.enumDefinitions = enumDefinitions;
    }

}

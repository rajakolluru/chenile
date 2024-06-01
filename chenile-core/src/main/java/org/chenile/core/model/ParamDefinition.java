package org.chenile.core.model;

import java.io.Serializable;

/**
 * Defines an individual parameter that is accepted by an operation of a sevice.
 */
public class ParamDefinition implements Serializable {

     private static final long serialVersionUID = 1L;

    String name;
    HttpBindingType type;
    String description;
    Class<?> paramClass;
    
    public Class<?> getParamClass() {
		return paramClass;
	}

	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}

	boolean cacheKey = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HttpBindingType getType() {
        return type;
    }

    public void setType(HttpBindingType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(boolean cacheKey) {
        this.cacheKey = cacheKey;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t<param>\n");
        sb.append("\t\t\t<name>" + name + "</name>\n");
        sb.append("\t\t\t<type>" + type + "</type>\n");
        sb.append("\t\t\t<description>" + description + "</description>\n");
        sb.append("\t\t</param>\n");
        return sb.toString();
    }
}


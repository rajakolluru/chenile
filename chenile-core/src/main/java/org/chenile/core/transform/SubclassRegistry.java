package org.chenile.core.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * This is a registry of mapping base classes to the appropriate subclasses.<br/>
 * For example, if there is a base class {@code Vehicle} which is subclassed by Car. The base
 * car may have a type parameter based on which the subclass exists. So if {@code vehicle.type == 'CAR'}
 * then it might belong to a subclass Car. On the other hand, if {@code vehicle.type == 'BUS'}
 * then it might belong the Bus subclass. <br/>
 * This registry maintains these relationships.<br/>
 * It maintains a map of maps. The first map points to the second map and is indexed by the
 * baseclass. The second map links the string type to the subclass. In the example below, you will find
 * the JSON representation of the maps as follows
 * {@code
 *  {
 *      "Vehicle.class": {
 *          "CAR" : "Car.class",
 *          "BUS" : "Bus.class"
 *      }
 *  }
 * }<br/>
 * This is used by the {@link TransformationClassSelector} for determining the appropriate
 * subtypes  for a given type<br/>
 * Subclass registration is done in the modules that own the subclass to preserve modularity.
 */
public class SubclassRegistry {
    Logger logger = LoggerFactory.getLogger(SubclassRegistry.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Class<?>,Map<String, Class<?>>> registry = new HashMap<>();
    public <T> void  addSubclass(Class<?> superClass, String type, Class<? extends T> subClass ){
        Map<String, Class<?>> map = registry.computeIfAbsent(superClass, k -> new HashMap<>());
        map.put(type,subClass);
    }

    /**
     * This class looks at a JSON representation of a superclass and based on the type found in
     * the JSON, determines the corresponding subclass. <br/>
     * ASSUMPTION: The class has a type field that is used to determine the subclass.
     * @param jsonString the Input JSON String that has the type.
     * @param superClass the superClass hierarchy to look at to determine the correct subclass.
     * @return the correct subclass if it exists in the registry
     */
    public Class<?> determineSubClass(String jsonString, Class<?> superClass)
            throws Exception {
        if (registry.get(superClass) == null) return null;
        Map<String,Class<?>> map = registry.get(superClass);
        JsonNode node = objectMapper.readTree(jsonString.getBytes());
        JsonNode subNode = node.get("type");
        if (subNode == null) {
            return null;
        }
        String type = subNode.asText();
        if (type == null || type.isEmpty()) return null;
        return (map.get(type) != null)? map.get(type) : null;
    }
}

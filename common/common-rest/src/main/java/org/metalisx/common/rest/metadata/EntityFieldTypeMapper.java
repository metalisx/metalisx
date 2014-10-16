package org.metalisx.common.rest.metadata;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;


@Named
public class EntityFieldTypeMapper {

	private static Map<Class<?>, String> map = new HashMap<Class<?>, String>();
	
	static {
		map.put(Boolean.class, "boolean");
		map.put(Double.class, "integer");
		map.put(Date.class, "date");
		map.put(Float.class, "integer");
		map.put(Integer.class, "integer");
		map.put(Long.class, "integer");
		map.put(String.class, "string");
		map.put(Collection.class, "list");
		map.put(List.class, "list");
		map.put(Set.class, "list");
		map.put(Map.class, "list");
		map.put(byte[].class, "byte[]");
	}
	
	public EntityFieldTypeMapper() {
	}
	
	public String getType(Field field) {
		Class<?> fieldType = field.getType();
		String type = map.get(fieldType);
		if (type == null) {
			type = "string";
		}
		return type;
	}
	
}

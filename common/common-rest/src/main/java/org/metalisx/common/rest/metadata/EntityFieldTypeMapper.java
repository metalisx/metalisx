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
		map.put(String.class, "string");
		map.put(Collection.class, "list");
		map.put(List.class, "list");
		map.put(Set.class, "list");
		map.put(Map.class, "list");
	}
	
	public EntityFieldTypeMapper() {
	}
	
	public String getGuiType(Field field) {
		Class<?> type = field.getType();
		String guiType = map.get(type);
		if (guiType == null) {
			guiType = "string";
		}
		return guiType;
	}
	
}

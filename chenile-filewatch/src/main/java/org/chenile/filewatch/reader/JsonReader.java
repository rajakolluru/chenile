package org.chenile.filewatch.reader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Read a JSON file. Treat the file as a giant array of multiple records.
 * Parse it and return an iterator
 * @author Raja Shankar Kolluru
 *
 */
public class JsonReader implements Iterable<Object>{

	ObjectMapper objectMapper = new ObjectMapper();
	private List<Object> list;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JsonReader(Path path, Class<?> recordClass) throws Exception{
		TypeFactory tf = objectMapper.getTypeFactory();
		CollectionType type = tf.constructCollectionType(List.class, recordClass);		

		this.list = (List)objectMapper.readValue(Files.newInputStream(path), type);
	}

	@Override
	public Iterator<Object> iterator() {		
		return list.iterator();
	}

}

import org.apache.jena.rdf.model.ResourceFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import org.apache.jena.rdf.model.Resource;



public class JenaResourceConverter {
    public static class Serializer implements JsonSerializer<Resource> {
        public Serializer() {
            super();
        }
        public JsonElement serialize(Resource t, Type type,
                JsonSerializationContext jsonSerializationContext) {
            Resource resource = (Resource) t;
            return new JsonPrimitive(resource.toString());
        }
		
		
		
    }
    public static class Deserializer implements JsonDeserializer<Resource> {
    	public Resource  deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext jsonDeserializationContext) {
            try {
                return ResourceFactory.createResource(jsonElement.getAsString());
            } catch (Exception e) {
                return null;
            }
        }

		
    }
}
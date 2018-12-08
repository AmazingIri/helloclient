import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class JsonCodec<T> extends TypeCodec<T> {

    ObjectMapper objectMapper = new ObjectMapper();
    //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    public JsonCodec(Class<T> javaClass) {
        super(DataType.varchar(), javaClass);
    }

    @Override
    public ByteBuffer serialize(T value, ProtocolVersion protocolVersion) throws InvalidTypeException {
        if(value == null) {
            return null;
        }
        try {
            return ByteBuffer.wrap(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
    }

    @Override
    public T deserialize(ByteBuffer bytes, ProtocolVersion protocolVersion) throws InvalidTypeException {
        if(bytes == null) {
            return null;
        }
        try {
            byte[] b = new byte[bytes.remaining()];
            System.out.println(b);
            bytes.duplicate().get(b);
            return (T) objectMapper.readValue(b, DataObject.class);
        } catch (IOException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
    }

    @Override
    public T parse(String value) throws InvalidTypeException {
        if (value == null || value.equalsIgnoreCase("NULL"))
            return null;
        if (value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\'')
            throw new InvalidTypeException("JSON strings must be enclosed by single quotes");
        String json = value.substring(1, value.length() - 1).replace("''", "'");
        try {
            return (T) objectMapper.readValue(json, toJavaType());
        } catch (IOException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
    }

    @Override
    public String format(T value) throws InvalidTypeException {
        if (value == null)
            return "NULL";
        String json;
        try {
            json = objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
        return '\'' + json.replace("\'", "''") + '\'';
    }

    protected JavaType toJavaType() {
        return TypeFactory.defaultInstance().constructType(getJavaType().getType());
    }

}

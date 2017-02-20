package concurrent.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

public class KryoSerializableUtils {

	static Kryo kryo = new Kryo();
	
	public static String serialize2(Object obj) {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(obj.getClass(), new JavaSerializer());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output = new Output(baos);
		kryo.writeClassAndObject(output, obj);
		output.flush();
		output.close();
		
		byte[] b = baos.toByteArray();
		try {
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(new Base64().encode(b));
	}
	public static Object unserialize2(String obj) {
		if(obj == null) {
			return null;
		}
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(Serializable.class, new JavaSerializer());
		
		ByteArrayInputStream bais = new ByteArrayInputStream(new Base64().decode(obj));
		Input input = new Input(bais);
		return kryo.readClassAndObject(input);
	}
	
	/**
	 * 序列化
	 * @param obj
	 * @return
	 */
	public static byte[] serialize(Object obj) {
		byte[] buffer = new byte[2048];
		Output output = new Output(buffer);
		
		kryo.writeClassAndObject(output, obj);
		byte[] bytes = output.toBytes();
		output.close();
		return bytes;
	}
	
	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		Input input = new Input(bytes);
		Object object = kryo.readClassAndObject(input);
		input.close();
		return object;
	}
}

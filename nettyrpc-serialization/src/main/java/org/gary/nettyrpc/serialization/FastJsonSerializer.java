package org.gary.nettyrpc.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonSerializer{
	
	public byte[] serialize(Object object) {
        byte[] bytes = JSON.toJSONBytes(object, SerializerFeature.SortField);
        return bytes;
    }
	
	public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T result = JSON.parseObject(bytes, clazz, Feature.SortFeidFastMatch);
        return result;
    }
}

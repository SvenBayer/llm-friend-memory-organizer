package de.svenbayer.llm_friend_memory_organizer.service.neo4j;

import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;

import java.util.List;

public class FloatArrayConverter implements Neo4jPersistentPropertyConverter<float[]> {

    @Override
    public Value write(float[] source) {
        double[] list = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            list[i] = source[i];
        }
        return org.neo4j.driver.Values.value(list);
    }

    @Override
    public float[] read(Value source) {
        List<Double> list = source.asList(org.neo4j.driver.Value::asDouble);
        float[] result = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).floatValue();
        }
        return result;
    }
}


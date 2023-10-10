package com.aiurt.modules.fault.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.OptionalDouble;

/**
 * @author sb
 */
public class DoubleToIntSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeNumber(Math.round(OptionalDouble.of(value).orElse(0)));
    }
}

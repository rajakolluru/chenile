package org.chenile.utils.enumerations;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public interface Platform {
    static Platform of(String value) {
        try {
            return Platform.parse(value);
        } catch (IllegalArgumentException e) {
            return new Simple(value);
        }
    }

    public static Platform parse(Class<T extends java.lang.Enum> clazz, String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Raw value cannot be null");
        }
        var trimmed = rawValue.toUpperCase().trim();
        for (Enum enumValue : values()) {
            if (enumValue.name().equals(trimmed)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Cannot parse enum from raw value: " + rawValue);
    }


    String value();

    enum Enum implements Platform {
        WEB, MOBILE;

        @Override
        @JsonValue
        public String value() {
            return name();
        }
    }


    class Simple implements Platform {
        String value;

        public Simple(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String value() {
            return value;
        }
    }

    class Deserializer extends StdDeserializer<Platform> {
        protected Deserializer() {
            super(Platform.class);
        }

        @Override
        public Platform deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return Platform.of(p.getValueAsString());
        }
    }
}

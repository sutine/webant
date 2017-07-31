package org.webant.queen.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonUtils {
    private static GsonBuilder builder = new GsonBuilder()
//            .enableComplexMapKeySerialization()
//            .serializeNulls()
//            .setDateFormat(DateFormat.LONG)
//            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//            .setPrettyPrinting()
//            .setVersion(1.0)
            .registerTypeAdapter(Date.class, new DateAsLongAdapter())
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter());

    private static Gson gson = builder.create();


    public static <T> T fromJson(GsonBuilder builder, String json, Class<T> clazz) {
        return builder.create().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object o) {
        if (o == null) return "";
        return gson.toJson(o);
    }
}

class DateAsLongAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getTime());
    }

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return new Date(Long.valueOf(json.getAsString()));
    }
}

class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    public JsonElement serialize(Timestamp src, Type arg1, JsonSerializationContext arg2) {
        DateFormat format = new SimpleDateFormat(DateFormatUtils.DATE_TIME_MILLI_FORMAT);
        String dateFormatAsString = format.format(new Date(src.getTime()));
        return new JsonPrimitive(dateFormatAsString);
    }

    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        try {
            DateFormat format = new SimpleDateFormat(DateFormatUtils.DATE_TIME_MILLI_FORMAT);
            Date date = format.parse(json.getAsString());
            return new Timestamp(date.getTime());
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}

class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    public JsonElement serialize(Date src, Type arg1, JsonSerializationContext arg2) {
        DateFormat format = new SimpleDateFormat(DateFormatUtils.DATE_TIME_MILLI_FORMAT);
        String dateFormatAsString = format.format(new Date(src.getTime()));
        return new JsonPrimitive(dateFormatAsString);
    }

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        try {
            DateFormat format = new SimpleDateFormat(DateFormatUtils.DATE_TIME_MILLI_FORMAT);
            return format.parse(json.getAsString());
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
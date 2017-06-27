package org.webant.commons.utils

object SJsonUtils extends JsonUtils {

//  val builder = new GsonBuilder();
//  builder.registerTypeAdapter(classOf[Date], new JsonDeserializer[Date]() {
//    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date = {
//      return new Date(json.getAsJsonPrimitive().getAsLong());
//    }
//  })
//
//  val gson = builder.create()

//  def fromJson[T](json: String, clazz: Type): T = {
//    gson.fromJson(json, clazz)
//  }
//
//  def toJson(o: AnyRef): String = {
//    if (o == null) return ""
//    gson.toJson(o)
//  }

//  class TimestampTypeAdapter extends JsonSerializer[Timestamp] with JsonDeserializer[Timestamp] {
//    def serialize(src: Timestamp, arg1: Type, arg2: JsonSerializationContext): JsonElement = {
//      val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//      val dateFormatAsString = format.format(new Date(src.getTime()));
//      return new JsonPrimitive(dateFormatAsString);
//    }
//
//    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Timestamp = {
//      if (!(json.isInstanceOf[JsonPrimitive])) {
//        throw new JsonParseException("The date should be a string value");
//      }
//
//      try {
//        val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//        val date = format.parse(json.getAsString());
//        return new Timestamp(date.getTime());
//      } catch {
//        case e: Exception =>
//          throw new JsonParseException(e);
//      }
//    }
//
//  }
}

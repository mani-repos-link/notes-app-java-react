package It.RIS.DemoTest.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;


public final class Json {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    Instant.class,
                    (JsonSerializer<Instant>) (instant, type, ctx) ->
                            new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(instant))
            )
            .registerTypeAdapter(Instant.class,
                    (JsonDeserializer<Instant>) (json, type, ctx) ->
                            Instant.parse(json.getAsString()))
            .disableHtmlEscaping()
            .create();

    public static String write(Object value) {
        return GSON.toJson(value);
    }

    public static <T> T read(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
}

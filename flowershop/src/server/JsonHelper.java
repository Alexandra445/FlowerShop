package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Вспомогательный класс для работы с JSON на сервере
 */
public class JsonHelper {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    /**
     * Преобразует объект в JSON строку
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Преобразует JSON строку в объект указанного класса
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }

    /**
     * Читает JSON из тела запроса
     */
    public static String readJsonFromRequest(BufferedReader reader) throws IOException {
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }
        return json.toString();
    }

    /**
     * Создает JSON ответ с сообщением об ошибке
     */
    public static String errorJson(String message) {
        return "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}";
    }

    /**
     * Создает JSON ответ с сообщением об успехе
     */
    public static String successJson(String message) {
        return "{\"success\":\"" + message.replace("\"", "\\\"") + "\"}";
    }
}


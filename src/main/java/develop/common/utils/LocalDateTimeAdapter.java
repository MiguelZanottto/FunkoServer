package develop.common.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Clase que adapta los LocalDateTime a JSON y viceversa utilizando la libreria GSON.
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    /**
     * Convierte una cadena JSON en un objeto LocalDateTime.
     *
     * @param jsonReader El lector JSON que proporciona la cadena JSON.
     * @return Un objeto LocalDateTime obtenido a partir de la cadena JSON.
     * @throws IOException Si ocurre un error de entrada/salida al leer la cadena JSON.
     */
    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString());
    }

    /**
     * Escribe un objeto LocalDateTime como una cadena JSON.
     *
     * @param jsonWriter    El escritor JSON en el que se escribira la cadena JSON.
     * @param localDateTime El objeto LocalDateTime que se va a convertir en cadena JSON.
     * @throws IOException Si ocurre un error de entrada/salida al escribir la cadena JSON.
     */
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.toString());
    }
}

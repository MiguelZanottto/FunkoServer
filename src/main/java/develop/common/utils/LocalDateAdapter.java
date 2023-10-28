package develop.common.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Clase del cual adapta los LocalDate a JSON y viceversa utilizando la libreria GSON.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    /**
     * Convierte una cadena JSON en un objeto LocalDate.
     *
     * @param jsonReader El lector JSON que proporciona la cadena JSON.
     * @return Un objeto LocalDate obtenido a partir de la cadena JSON.
     * @throws IOException Si ocurre un error de entrada/salida al leer la cadena JSON.
     */
    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString());
    }

    /**
     * Escribe un objeto LocalDate como una cadena JSON.
     *
     * @param jsonWriter El escritor JSON en el que se escribira la cadena JSON.
     * @param localDate  El objeto LocalDate que se va a convertir en cadena JSON.
     * @throws IOException Si ocurre un error de entrada/salida al escribir la cadena JSON.
     */
    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.toString());
    }
}

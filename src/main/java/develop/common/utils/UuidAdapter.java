package develop.common.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;
/**
 * Clase que proporciona una adaptacion personalizada para la serializacion y deserializacion de objetos UUID
 * en formato JSON utilizando la biblioteca GSON.
 */
public class UuidAdapter extends TypeAdapter<UUID> {

    /**
     * Lee un UUID desde un JsonReader y lo convierte en un UUID.
     *
     * @param jsonReader JsonReader que proporciona los datos JSON de entrada.
     * @return Un UUID leido desde el JsonReader.
     * @throws IOException Si ocurre un error durante la lectura o el formato del UUID es incorrecto.
     */
    @Override
    public UUID read(final JsonReader jsonReader) throws IOException {
        return UUID.fromString(jsonReader.nextString());
    }

    /**
     * Escribe un objeto UUID en un objeto JsonWriter en formato de cadena.
     *
     * @param jsonWriter JsonWriter en el que se escribira el UUID como una cadena.
     * @param uuid UUID que se escribira en el JsonWriter.
     * @throws IOException Si ocurre un error durante la escritura.
     */
    @Override
    public void write(JsonWriter jsonWriter, UUID uuid) throws IOException {
        jsonWriter.value(uuid.toString());
    }
}
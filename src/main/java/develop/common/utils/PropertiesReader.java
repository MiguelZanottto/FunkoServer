package develop.common.utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase que permite leer propiedades desde un archivo (.properties).
 */
public class PropertiesReader {
    private final String fileName;
    private final Properties properties;

    /**
     * Constructor de la clase PropertiesReader.
     *
     * @param fileName Nombre del archivo de propiedades que se desea cargar.
     * @throws IOException Si ocurre un error de lectura del archivo o el archivo no se encuentra.
     */
    public PropertiesReader(String fileName) throws IOException {
        this.fileName = fileName;
        properties = new Properties();

        InputStream file = getClass().getClassLoader().getResourceAsStream(fileName);
        if (file != null) {
            properties.load(file);
        } else {
            throw new FileNotFoundException("No se encuentra el fichero " + fileName);
        }
    }

    /**
     * Obtiene el valor de una propiedad especifica a partir de su clave.
     *
     * @param key Clave de la propiedad que se desea obtener.
     * @return El valor de la propiedad correspondiente a la clave.
     * @throws FileNotFoundException Si no se encuentra la propiedad con la clave especificada en el archivo.
     */
    public String getProperty(String key) throws FileNotFoundException {
        String value = properties.getProperty(key);
        if (value != null) {
            return value;
        } else {
            throw new FileNotFoundException("No se encuentra la propiedad " + key + " en el fichero " + fileName);
        }
    }
}

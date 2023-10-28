package develop.server.services.services.database;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.Duration;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Clase que gestiona la conexion a la base de datos y proporciona metodos para inicializar las tablas,
 * ejecutando scripts SQL y obteniendo el pool de conexiones.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final ConnectionFactory connectionFactory;
    private final ConnectionPool pool;
    private boolean databaseInitTables;
    private String databaseUrl;

    private DatabaseManager() {
        loadProperties();

        connectionFactory = ConnectionFactories.get(databaseUrl);


        // Configuramos el pool de conexiones
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration
                .builder(connectionFactory)
                .maxIdleTime(Duration.ofMillis(1000)) // Tiempo máximo de espera
                .maxSize(20) // Tamaño máximo del pool
                .build();

        pool = new ConnectionPool(configuration);

        // Por si hay que inicializar las tablas
        if (databaseInitTables) {
            initTables();
        }
    }
    /**
     * Obtiene una instancia de la clase DatabaseManager.
     *
     * @return Instancia unica de DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private synchronized void loadProperties() {
        logger.debug("Cargando fichero de configuración de la base de datos");
        try {
            var props = new Properties();
            props.load(DatabaseManager.class.getClassLoader().getResourceAsStream(("database.properties")));
            databaseUrl = props.getProperty("database.url", "r2dbc:h2:file:///./Funkos?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            databaseInitTables = Boolean.parseBoolean(props.getProperty("database.initTables", "false"));
            logger.debug("La url de la base de datos es: " + databaseUrl);
        } catch (IOException e) {
            logger.error("Error al leer el fichero de configuración de la base de datos " + e.getMessage());
        }
    }

    /**
     * Inicializa las tablas de la base de datos SQL.
     */
    public synchronized void initTables() {
        // Debes hacer un script por accion
        logger.debug("Borrando tablas de la base de datos");
        executeScript("remove.sql").block(); // Bloqueamos hasta que se ejecute (no nos interesa seguir hasta que se ejecute)
        logger.debug("Inicializando tablas de la base de datos");
        executeScript("init.sql").block(); // Bloqueamos hasta que se ejecute (no nos interesa seguir hasta que se ejecute)
        logger.debug("Tabla de la base de datos inicializada");
    }

    /**
     * Ejecuta un script SQL en la base de datos.
     *
     * @param scriptSqlFile Nombre del archivo que contiene el script SQL.
     * @return Un Mono que se completa cuando el script se ha ejecutado.
     */
    public Mono<Void> executeScript(String scriptSqlFile) {
        logger.debug("Ejecutando script de inicialización de la base de datos: " + scriptSqlFile);
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> {
                    logger.debug("Creando conexión con la base de datos");
                    String scriptContent = null;
                    try {
                        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(scriptSqlFile)) {
                            if (inputStream == null) {
                                return Mono.error(new IOException("No se ha encontrado el fichero de script de inicialización de la base de datos"));
                            } else {
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                                    scriptContent = reader.lines().collect(Collectors.joining("\n"));
                                }
                            }
                        }
                        Statement statement = connection.createStatement(scriptContent);
                        return Mono.from(statement.execute());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                },
                Connection::close
        ).then();
    }
    /**
     * Obtiene el pool de conexiones de la base de datos.
     *
     * @return Pool de conexiones de la base de datos.
     */
    public ConnectionPool getConnectionPool() {
        return this.pool;
    }
}
package develop.server.repositories.funkos;

import develop.common.models.Funko;
import develop.common.models.IdGenerator;
import develop.common.models.Model;
import develop.server.services.services.database.DatabaseManager;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Implementacion de la interfaz FunkosRepository para operaciones CRUD de Funkos.
 */
public class FunkosRepositoryImpl implements FunkosRepository {
    private static FunkosRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkosRepositoryImpl.class);
    private final IdGenerator idGenerator;
    private final ConnectionPool connectionFactory;

    private FunkosRepositoryImpl(DatabaseManager databaseManager, IdGenerator idGenerator) {
        this.connectionFactory = databaseManager.getConnectionPool();
        this.idGenerator = idGenerator;
    }

    public synchronized static FunkosRepositoryImpl getInstance(DatabaseManager databaseManager, IdGenerator idGenerator) {
        if (instance == null) {
            instance = new FunkosRepositoryImpl(databaseManager, idGenerator);
        }
        return instance;
    }

    /**
     * Obtiene todos los Funkos almacenados en la base de datos.
     *
     * @return Un Flux que emite todos los Funkos encontrados.
     */
    @Override
    public Flux<Funko> findAll() {
        logger.debug("Buscando todos los funkos");
        String sql = "SELECT * FROM funkos";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql).execute())
                        .flatMap(result -> result.map((row, rowMetadata) ->
                                Funko.builder()
                                        .id(row.get("ID", Long.class))
                                        .cod(row.get("cod", java.util.UUID.class))
                                        .myId(row.get("MyId", Long.class))
                                        .name(row.get("nombre", String.class))
                                        .model(Model.valueOf(row.get("modelo", String.class)))
                                        .price(row.get("precio", Double.class))
                                        .releaseData((row.get("fecha_lanzamiento", java.time.LocalDate.class)))
                                        .createdAt(row.get("created_at", java.time.LocalDateTime.class))
                                        .updatedAt(row.get("updated_at", java.time.LocalDateTime.class))
                                        .build()
                        )),
                Connection::close
        );
    }

    /**
     * Busca Funkos por su nombre del cual coincidan con la cadena proporcionada.
     * @param nombre El nombre de los Funkos que se buscan.
     * @return Un Flux que muestra los Funkos encontrados
     */
    @Override
    public Flux<Funko> findByNombre(String nombre) {
        logger.debug("Buscando todos los funkos por nombre");
        String sql = "SELECT * FROM funkos WHERE nombre LIKE ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(sql)
                        .bind(0, "%" + nombre + "%")
                        .execute()
                ).flatMap(result -> result.map((row, rowMetadata) ->
                        Funko.builder()
                                .id(row.get("ID", Long.class))
                                .cod(row.get("cod", java.util.UUID.class))
                                .myId(row.get("MyId", Long.class))
                                .name(row.get("nombre", String.class))
                                .model(Model.valueOf(row.get("modelo", String.class)))
                                .price(row.get("precio", Double.class))
                                .releaseData((row.get("fecha_lanzamiento", java.time.LocalDate.class)))
                                .createdAt(row.get("created_at", java.time.LocalDateTime.class))
                                .updatedAt(row.get("updated_at", java.time.LocalDateTime.class))
                                .build()
                )),
                Connection::close
        );
    }

    /**
     * Busca un Funko por su ID unico.
     * @param id El identificador de la entidad que se busca.
     * @return Un Mono que muestra el Funko encontrado o un valor vacío su no se encuentra
     */
    @Override
    public Mono<Funko> findById(Long id) {
        logger.debug("Buscando funko por id: " + id);
        String sql = "SELECT * FROM funkos WHERE ID = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, id)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        Funko.builder()
                                .id(row.get("ID", Long.class))
                                .cod(row.get("cod", java.util.UUID.class))
                                .myId(row.get("MyId", Long.class))
                                .name(row.get("nombre", String.class))
                                .model(Model.valueOf(row.get("modelo", String.class)))
                                .price(row.get("precio", Double.class))
                                .releaseData((row.get("fecha_lanzamiento", java.time.LocalDate.class)))
                                .createdAt(row.get("created_at", java.time.LocalDateTime.class))
                                .updatedAt(row.get("updated_at", java.time.LocalDateTime.class))
                                .build()
                ))),
                Connection::close
        );
    }

    /**
     * Guarda un Funko en la base de datos
     * @param funko La entidad que se va a guardar.
     * @return Un Mono que muestra el Funko guardado
     */
    public Mono<Funko> save(Funko funko) {
        logger.debug("Guardando funko: " + funko);
        String sql = "INSERT INTO funkos (cod, MyId, nombre, modelo, precio, fecha_lanzamiento, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, funko.getCod())
                        .bind(1, idGenerator.getIdAndIncrement())
                        .bind(2, funko.getName())
                        .bind(3, funko.getModel().toString())
                        .bind(4, funko.getPrice())
                        .bind(5, funko.getReleaseData())
                        .bind(6, funko.getCreatedAt())
                        .bind(7, funko.getUpdatedAt())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    /**
     * Busca un Funko por su UUID unico
     * @param uuid El UUID del Funko que se busca.
     * @return Un Mono que muestra el Funko encontrado o un valor vacio si no se encuentra.
     */
    @Override
    public Mono<Funko> findByUuid(UUID uuid) {
        logger.debug("Buscando funko por uuid: " + uuid);
        String sql = "SELECT * FROM funkos WHERE cod = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .bind(0, uuid)
                        .execute()
                ).flatMap(result -> Mono.from(result.map((row, rowMetadata) ->
                        Funko.builder()
                                .id(row.get("ID", Long.class))
                                .cod(row.get("cod", java.util.UUID.class))
                                .myId(row.get("MyId", Long.class))
                                .name(row.get("nombre", String.class))
                                .model(Model.valueOf(row.get("modelo", String.class)))
                                .price(row.get("precio", Double.class))
                                .releaseData((row.get("fecha_lanzamiento", java.time.LocalDate.class)))
                                .createdAt(row.get("created_at", java.time.LocalDateTime.class))
                                .updatedAt(row.get("updated_at", java.time.LocalDateTime.class))
                                .build()
                ))),
                Connection::close
        );
    }

    /**
     * Actualiza un Funko existente en la base de datos
     * @param funko La entidad que se va a actualizar.
     * @return Un Mono que muestra el Funko actualizado.
     */
    @Override
    public Mono<Funko> update(Funko funko) {
        logger.debug("Actualizando funko: " + funko);
        String query = "UPDATE funkos SET nombre = ?, modelo = ?, precio = ?, updated_at = ? WHERE ID = ?";
        funko.setUpdatedAt(LocalDateTime.now());
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getName())
                        .bind(1, funko.getModel().toString())
                        .bind(2, funko.getPrice())
                        .bind(3, funko.getUpdatedAt())
                        .bind(4, funko.getId())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    /**
     * Elimina todos los Funkos en la base de datos
     * @param id El identificador de la entidad que se va a eliminar.
     * @return Un Mono que emite un boolean indicando si se realizo el borrado o no.
     */
    @Override
    public Mono<Boolean> deleteById(Long id) {
        logger.debug("Borrando funko por id: " + id);
        String sql = "DELETE FROM funkos WHERE ID = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                                .bind(0, id)
                                .execute()
                        ).flatMapMany(Result::getRowsUpdated)
                        .hasElements(),
                Connection::close
        );
    }

    /**
     * Elimina todos los Funkos de la base de datos.
     * @return Un Mono que completa una vez la eliminacion de los Funkos
     */
    @Override
    public Mono<Void> deleteAll() {
        logger.debug("Borrando todos los funkos");
        String sql = "DELETE FROM funkos";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(sql)
                        .execute()
                ).then(),
                Connection::close
        );
    }
}

package develop.server.services.services.funkos;

import develop.common.models.Funko;
import develop.common.models.Model;
import develop.common.utils.LocalDateAdapter;
import develop.common.utils.LocalDateTimeAdapter;
import develop.common.utils.UuidAdapter;
import develop.server.exceptions.storage.RutaInvalidaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FunkosStorageImpl implements FunkosStorage {
    private final Logger logger = LoggerFactory.getLogger(FunkosStorageImpl.class);
    private static FunkosStorageImpl instance;

    private FunkosStorageImpl() {}

    public static FunkosStorageImpl getInstance() {
        if (instance == null) {
            instance = new FunkosStorageImpl();
        }
        return instance;
    }

    @Override
    public Mono<Void> exportJson(List<Funko> data, String file) {
        return Mono.fromCallable(() -> {
                    if (!validarRuta(file)) {
                        String errorMessage = "Ruta de fichero inválida: " + file;
                        logger.error(errorMessage);
                        throw new RutaInvalidaException(errorMessage);
                    } else {
                        String appPath = System.getProperty("user.dir");
                        String dataPath = appPath + File.separator + "data";
                        String backupFile = dataPath + File.separator + file;

                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                .registerTypeAdapter(UUID.class, new UuidAdapter())
                                .setPrettyPrinting()
                                .create();

                        String json = gson.toJson(data);
                        logger.debug("Escribiendo el archivo backup: " + backupFile);

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile))) {
                            writer.write(json);
                        } catch (IOException e) {
                            throw new RuntimeException("Error al escribir el archivo de respaldo", e);
                        }
                    }
                    return null;
                })
                .onErrorMap(e -> {
                    if (e instanceof RutaInvalidaException) {
                        return e;
                    }
                    return new RuntimeException("Error al escribir el archivo de respaldo", e);
                })
                .then();
    }

    private boolean validarRuta(String ruta) {
        Path path = Paths.get(ruta);
        String fileName = path.getFileName().toString();
        return fileName.matches(".*\\.json$");
    }

    @Override
    public Flux<Funko> importCsv() {
        return Flux.create(sink -> {
            String dataPath = "data" + File.separator + "funkos.csv";
            String appPath = System.getProperty("user.dir");
            Path filePath = Paths.get(appPath, dataPath);
            logger.debug("Leyendo el archivo: " + filePath.toString());

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toString()))) {
                reader.lines().skip(1).map(this::getFunko).forEach(sink::next);
            } catch (FileNotFoundException e) {
                logger.error("No se encontró el archivo: " + filePath.toString());
                sink.error(e);
            } catch (IOException e) {
                logger.error("Error al leer el archivo: " + filePath.toString());
                sink.error(e);
            }
            sink.complete();
        });
    }

    private UUID getUUID(String uuid) {
        try {
            return uuid.length() > 36 ? UUID.fromString(uuid.substring(0, 36)) : UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            logger.error("Error al convertir UUID: " + uuid);
            throw e;
        }
    }

    private LocalDate getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(date, formatter);
        } catch (java.time.format.DateTimeParseException e) {
            logger.error("Error al convertir fecha: " + date);
            throw e;
        }
    }

    private Funko getFunko(String linea) {
        String[] campos = linea.split(",");
        UUID cod = getUUID(campos[0]);
        String name = campos[1];
        Model model = Model.valueOf(campos[2]);
        double price = Double.parseDouble(campos[3]);
        LocalDate releaseData = getDate(campos[4]);
        return Funko.builder()
                .cod(cod)
                .name(name)
                .model(model)
                .price(price)
                .releaseData(releaseData)
                .build();
    }
}
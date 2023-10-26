package develop.server.services.services.funkos;

import develop.common.models.Funko;
import develop.common.models.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import reactor.core.publisher.Flux;

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
            return uuid.length() > 36 ? UUID.fromString(uuid.substring(0, 36)) : UUID.fromString(uuid);
    }

    private LocalDate getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);

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
package develop.services.funkos;


import develop.common.models.Funko;
import develop.server.services.services.funkos.FunkosStorage;
import develop.server.services.services.funkos.FunkosStorageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunkosStorageTest {
    private FunkosStorage funkosStorage;

    @BeforeEach
    void setUp() throws SQLException {
        funkosStorage = FunkosStorageImpl.getInstance();
    }

    @Test
    void importFunkosCsv() {
        List<Funko> funkos = funkosStorage.importCsv().collectList().block(); // Importamos todos los funkos desde el CSV preestablecido
        assertAll(() -> assertNotNull(funkos), // Comprobamos que la lista no sea nula
                () -> assertFalse(funkos.isEmpty()), // Comprobamos que la lista tenga al menos un elemento
                () -> assertEquals(90, funkos.size() ) // Sabiendo que la lista tiene 90 elementos, comprobamos que el tamano sea el correcto
        );
    }
}

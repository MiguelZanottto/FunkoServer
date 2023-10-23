package develop;

import develop.common.models.Funko;
import develop.common.models.IdGenerator;
import develop.common.models.Model;
import develop.server.repositories.funkos.FunkosRepositoryImpl;
import develop.server.services.services.database.DatabaseManager;

import java.time.LocalDate;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        IdGenerator id = IdGenerator.getInstance();
        FunkosRepositoryImpl repo = FunkosRepositoryImpl.getInstance(db,id);
        Funko f = Funko.builder().name("nombre").cod(UUID.randomUUID()).price(19.99).releaseData(LocalDate.now()).model(Model.ANIME).build();
        repo.save(f).block();
        repo.findById(1L).subscribe(fuk -> System.out.println(fuk));
    }
}
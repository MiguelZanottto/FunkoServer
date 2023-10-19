package develop;

import develop.exceptions.funkos.FunkoNoEncotradoException;
import develop.locale.MyLocale;
import develop.models.Funko;
import develop.models.IdGenerator;
import develop.models.Model;
import develop.models.Notificacion;
import develop.repositories.funkos.FunkosRepository;
import develop.repositories.funkos.FunkosRepositoryImpl;
import develop.services.database.DatabaseManager;
import develop.services.funkos.FunkoStorageImpl;
import develop.services.funkos.FunkosCacheImpl;
import develop.services.funkos.FunkosNotificationImpl;
import develop.services.funkos.FunkosServiceImpl;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        FunkosServiceImpl servicio = FunkosServiceImpl.getInstance(FunkosRepositoryImpl.getInstance(DatabaseManager.getInstance(), IdGenerator.getInstance()), FunkosNotificationImpl.getInstance(), FunkoStorageImpl.getInstance());
        MyLocale myLocale = new MyLocale();

        servicio.getNotifications().subscribe(
                notificacion -> {
                    switch (notificacion.getTipo()) {
                        case NEW:
                            System.out.println("🟢 Funko insertado: " + notificacion.getContenido());
                            break;
                        case UPDATED:
                            System.out.println("🟠 Funko actualizado: " + notificacion.getContenido());
                            break;
                        case DELETED:
                            System.out.println("🔴 Funko eliminado: " + notificacion.getContenido());
                            break;
                    }
                },
                error -> System.err.println("Se ha producido un error: " + error),
                () -> System.out.println("Completado")
        );

        System.out.println("======= HACEMOS EL IMPORT DE FUNKOS DEL CSV =======");
        Flux <Funko> importDeFunkos = servicio.importFile();

        System.out.println("======= POR CADA FUNKO QUE DEVUELVA GUARDAMOS EN LA BASE DE DATOS =======");
        importDeFunkos.subscribe(funko -> servicio.save(funko).block());

        System.out.println("======= UNA VEZ TENGAMOS TODOS GUARDADOS HACEMOS LAS CONSULTAS =======");

        // FUNKO MAS CARO
        servicio.findAll()
                .reduce((funko1, funko2) -> funko1.getPrice() >= funko2.getPrice() ? funko1 : funko2)
                .subscribe(mostExpensive -> {
                    if (mostExpensive != null) {
                        System.out.println("Funko más caro:");
                        System.out.println(mostExpensive);
                    } else {
                        System.out.println("No se encontraron Funkos.");
                    }
                });

        // MEDIA DE PRECIO DE LOS FUNKOS
        servicio.findAll()
                .map(Funko::getPrice)
                .collect(Collectors.averagingDouble(Double::doubleValue))
                .subscribe(averagePrice -> {
                    System.out.println("La media de precios de los Funkos es: " + averagePrice);
                });

        // FUNKOS AGRUPADOS POR MODELO
        servicio.findAll()
                .collectMultimap(Funko::getModel)
                .subscribe(modeloToFunkosMap -> {
                    modeloToFunkosMap.forEach((modelo, funkos) -> {
                        System.out.println("Modelo: " + modelo);
                        funkos.forEach(System.out::println);
                    });
                });

        // NÚMERO DE FUNKOS POR MODELO
        servicio.findAll()
                .collectMultimap(Funko::getModel)
                .subscribe(modeloToCountMap -> {
                    modeloToCountMap.forEach((modelo, funkos) -> {
                        System.out.print(modelo);
                        System.out.println(": " + funkos.size());
                    });
                });

        // FUNKOS QUE HAN SIDO LANZADO EN EL 2023
        servicio.findAll()
                .filter(funko -> funko.getReleaseData().getYear() == 2023)
                .collectList()
                .subscribe(funkos -> {
                    System.out.println("Funkos lanzados en el 2023:");
                    funkos.forEach(System.out::println);
                });

        // NUMERO DE FUNKO DE STITCH
        servicio.findAll()
                .filter(funko -> funko.getName().contains("Stitch"))
                .count()
                .subscribe(numero ->
                        System.out.println("Numero de funkos de stitch: " + numero));

        // LISTADO DE STITCH
        servicio.findAll()
                .filter(funko -> funko.getName().contains("Stitch"))
                .collectList()
                .subscribe(stitch -> {
                    System.out.println("Lista de funkos de stitch");
                    System.out.println(stitch);
                });

        // EXPORTAMOS A JSON
        System.out.println("EXPORTANDO FUNKOS A JSON...");
        servicio.exportFile("funko.json").subscribe();

        // EL PROGRAMA SIGUE CORRIENDO POR EL CACHE Y LAS NOTIFICACIONES

    }
}
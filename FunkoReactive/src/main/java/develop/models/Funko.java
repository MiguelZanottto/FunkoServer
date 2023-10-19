package develop.models;

import develop.locale.MyLocale;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
public class Funko {
    private long id; // Identificador unico del funko, este lo asigna la base de datos
    private UUID COD; // Codigo unico del funko
    private long myId; // Otro identificador que se le asigna con el IdGenerator
    private String name; // Nombre del funko
    private Model model; // Modelo del funko
    private double price; // Precio del funko
    private LocalDate releaseData; // Fecha de creacion del funko
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // Fecha de creacion del funko en la base de datos
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now(); // Fecha de ultima actualizacion del funko


    @Override
    public String toString() {
        MyLocale myLocal = new MyLocale();
        return "Funko{" +
                "id=" + id +
                ", COD=" + COD +
                ", myId=" + myId +
                ", name='" + name + '\'' +
                ", model=" + model +
                ", price=" + myLocal.toLocalMoney(price) +    // Imprimimos el precio codificado a la moneda Local
                ", releaseData=" + myLocal.toLocalDate(releaseData) + // Imprimimos el año de lanzamiento codificado a la fecha local
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }


    public static UUID getUUID(String uuid) {
        return uuid.length() > 36? UUID.fromString(uuid.substring(0,36)): UUID.fromString(uuid);
    }

    public static LocalDate getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date,formatter);
    }

    public static Funko getFunko(String linea){
        String [] campos = linea.split(",");
        UUID COD = getUUID(campos[0]);
        String name = campos[1];
        Model model = Model.valueOf(campos[2]);
        double price = Double.parseDouble(campos[3]);
        LocalDate releaseData = getDate(campos[4]);
        return Funko.builder()
                .COD(COD)
                .name(name)
                .model(model)
                .price(price)
                .releaseData(releaseData)
                .build();
    }
}
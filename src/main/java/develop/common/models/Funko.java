package develop.common.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
public class Funko {
    private long id; // Identificador unico del funko, este lo asigna la base de datos
    private UUID cod; // Codigo unico del funko
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
        return "Funko{" +
                "id=" + id +
                ", cod=" + cod +
                ", myId=" + myId +
                ", name='" + name + '\'' +
                ", model=" + model +
                ", price=" + MyLocale.toLocalMoney(price) +    // Imprimimos el precio codificado a la moneda Local
                ", releaseData=" + MyLocale.toLocalDate(releaseData) + // Imprimimos el año de lanzamiento codificado a la fecha local
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
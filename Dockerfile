# Etapa de compilación, un docker especifico, que se etiqueta como build
FROM gradle:jdk17 AS build

# Directorio de trabajo
WORKDIR /app

# Copia los archivos build.gradle y src de nuestro proyecto
COPY build.gradle.kts .
COPY gradlew .
COPY cert cert
COPY data data
COPY gradle gradle
COPY src src

# Podemos decirle que saque la documentación de los test (coverage)
# RUN ./gradlew test jacocoTestReport
# Podemos decirle que saque el javaDoc
# RUN ./gradlew javadoc

# Compila y construye el proyecto, podemos evitar los test evitando con -x test
RUN ./gradlew shadowJar -x test

# Etapa de ejecución, un docker especifico, que se etiqueta como run
# Con una imagen de java
FROM openjdk:17-jdk AS run

# Directorio de trabajo
WORKDIR /app

# Copia el jar de la aplicación, ojo que esta en la etapa de compilación, etiquetado como build
# Cuidado con la ruta definida cuando has copiado las cosas en la etapa de compilación
# Para copiar un archivo de una etapa a otra, se usa la instrucción COPY --from=etapaOrigen
COPY --from=build /app/build/libs/*.jar /app/my-app.jar
COPY --from=build /app/cert/server_keystore.p12 /app/cert/server_keystore.p12
COPY --from=build /app/data/funkos.csv /app/data/funkos.csv

# Podemos copiar la documentación de los test (coverage) o el javaDoc
# COPY --from=build /app/build/reports/jacoco/test/html/ coverage
# COPY --from=build /app/build/docs/javadoc/ javadoc

# Expone el puerto 8080, pero en esta imagen no es necesario
EXPOSE 3000
# Ejecuta el jar
ENTRYPOINT ["java","-jar","/app/my-app.jar"]
package develop.server;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import develop.common.models.*;
import develop.common.utils.LocalDateAdapter;
import develop.common.utils.LocalDateTimeAdapter;
import develop.common.utils.UuidAdapter;
import develop.server.exceptions.server.ServerException;
import develop.server.repositories.users.UsersRepository;
import develop.server.services.services.funkos.FunkosService;
import develop.server.services.token.TokenService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
/**
 * Clase que maneja la comunicacion con un cliente a traves de un socket.
 */
public class ClientHandler extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(UUID.class, new UuidAdapter()).create();
    private final long clientNumber;
    private final FunkosService funkosService;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Socket socket, long clientNumber, FunkosService funkosService) {
        this.clientSocket = socket;
        this.clientNumber = clientNumber;
        this.funkosService = funkosService;
    }

    public void run() {
        try {
            openConnection();
            String clientInput;
            Request request;

            while (true) {
                clientInput = in.readLine();
                logger.debug("Petición recibida en bruto: " + clientInput);
                request = gson.fromJson(clientInput, Request.class);
                handleRequest(request);
            }

        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        } catch (ServerException ex) {
            out.println(gson.toJson(new Response(Response.Status.ERROR, ex.getMessage(), LocalDateTime.now().toString())));
        }
    }

    private void closeConnection() throws IOException {
        logger.debug("Cerrando la conexión con el cliente: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
        out.close();
        in.close();
        clientSocket.close();
    }

    private void openConnection() throws IOException {
        logger.debug("Conectando con el cliente nº: " + clientNumber + " : " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    /**
     * Maneja una solicitud del cliente y toma acciones en funcion del tipo de solicitud.
     *
     * @param request La solicitud del cliente.
     * @throws IOException      Excepcion de E/S.
     * @throws ServerException Excepcion de servidor.
     */
    @SuppressWarnings("unchecked")
    private void handleRequest(Request request) throws IOException, ServerException {
        logger.debug("Petición para procesar: " + request);

        switch (request.type()) {
            case LOGIN -> processLogin(request);
            case SALIR -> processSalir();
            case GETALL -> procesasGetAll(request);
            case GETBYID -> procesasGetById(request);
            case GETBYMODEL-> procesasGetByModel(request);
            case GETBYRELEASEDATA-> procesasGetByReleaseData(request);
            case POST -> procesarPost(request);
            case UPDATE -> procesasUpdate(request);
            case DELETE -> procesasDelete(request);
            default -> new Response(Response.Status.ERROR, "No tengo ni idea", LocalDateTime.now().toString());
        }
    }

    private void processSalir() throws IOException {
        out.println(gson.toJson(new Response(Response.Status.BYE, "Adios", LocalDateTime.now().toString())));
        closeConnection();
    }

    private void processLogin(Request request) throws ServerException {
        logger.debug("Petición de login recibida: " + request);
        Login login = gson.fromJson(String.valueOf(request.content()), new TypeToken<Login>() {
        }.getType());

        var user = UsersRepository.getInstance().findByByUsername(login.username());
        if (user.isEmpty() || !BCrypt.checkpw(login.password(), user.get().password())) {
            logger.warn("Usuario no encontrado o falla la contraseña");
            throw new ServerException("Usuario o contraseña incorrectos");
        }

        var token = TokenService.getInstance().createToken(user.get(), Server.TOKEN_SECRET, Server.TOKEN_EXPIRATION);

        logger.debug("Respuesta enviada: " + token);
        out.println(gson.toJson(new Response(Response.Status.TOKEN, token, LocalDateTime.now().toString())));
    }

    private Optional<User> procesarToken(String token) throws ServerException {
        if (TokenService.getInstance().verifyToken(token, Server.TOKEN_SECRET)) {
            logger.debug("Token válido");
            var claims = TokenService.getInstance().getClaims(token, Server.TOKEN_SECRET);
            var id = claims.get("userid").asInt();
            var user = UsersRepository.getInstance().findByById(id);
            if (user.isEmpty()) {
                logger.error("Usuario no autenticado correctamente");
                throw new ServerException("Usuario no autenticado correctamente");
            }
            return user;
        } else {
            logger.error("Token no válido");
            throw new ServerException("Token no válido");
        }
    }

    private void procesasGetAll(Request request) throws ServerException {
        procesarToken(request.token());

        funkosService.findAll()
                .collectList()
                .subscribe(funkos -> {
                    logger.debug("Respuesta enviada: " + funkos);
                    var resJson = gson.toJson(funkos);
                    out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString())));
                });
    }

    private void procesasGetById(Request request) throws ServerException {
        procesarToken(request.token());

        var id = Long.parseLong(request.content());
        funkosService.findById(id).subscribe(
                funko -> {
                    logger.debug("Respuesta enviada: " + funko);
                    var resJson = gson.toJson(funko);
                    out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString())));
                },
                error -> {
                    logger.warn("Funko no encontrado con id: " + request.content());
                    out.println(gson.toJson(new Response(Response.Status.ERROR, error.getMessage(), LocalDateTime.now().toString())));
                }
        );
    }

    private void procesasGetByModel(Request request) throws ServerException {
        procesarToken(request.token());
        var model = request.content();
        funkosService
                .findAll()
                .filter(f -> f.getModel().name().equals(model))
                .collectList()
                .subscribe(funkos -> {
                    logger.debug("Respuesta enviada: " + funkos);
                    var resJson = gson.toJson(funkos);
                    out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString())));
                });
    }

    private void procesasGetByReleaseData(Request request) throws ServerException {
        procesarToken(request.token());
        var ano = Integer.parseInt(request.content());
        funkosService
                .findAll()
                .filter(f -> f.getReleaseData().getYear() == ano)
                .collectList()
                .subscribe(funkos -> {
                    logger.debug("Respuesta enviada: " + funkos);
                    var resJson = gson.toJson(funkos);
                    out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString())));
                });
    }


    private void procesarPost(Request request) throws ServerException {
        var user = procesarToken(request.token());
        if (user.isPresent() && user.get().role().equals(User.Role.ADMIN)) {
            Funko funkoToSave = gson.fromJson(String.valueOf(request.content()), new TypeToken<Funko>() {
            }.getType());
            funkosService.save(funkoToSave).subscribe(
                    funko -> {
                        logger.debug("Respuesta enviada: " + funko);
                        var resJson = gson.toJson(funko); // Mandamos todo como cadenas contenido
                        out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString())));
                    },
                    error -> {
                        logger.error("Funko no encontrado con id: " + error.getMessage());
                        out.println(gson.toJson(new Response(Response.Status.ERROR, error.getMessage(), LocalDateTime.now().toString())));
                    }
            );
        } else {
            logger.error("Usuario no autenticado correctamente o no tiene permisos para esta acción");
            throw new ServerException("Usuario no autenticado correctamente o no tiene permisos para esta acción");
        }
    }

    private void procesasUpdate(Request request) throws ServerException {
        var user = procesarToken(request.token());
        if (user.isPresent() && user.get().role().equals(User.Role.ADMIN)) {
            Funko funkoToUpdate = gson.fromJson(String.valueOf(request.content()), new TypeToken<Funko>() {
            }.getType());
            funkosService.update(funkoToUpdate).subscribe(
                    funko -> {
                        logger.debug("Respuesta enviada: " + funko);
                        var resJson = gson.toJson(funko); // Mandamos todo como cadenas contenido
                        out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString()))); // Respuesta
                    },
                    error -> {
                        logger.error("Funko no encontrado con id: " + error.getMessage());
                        out.println(gson.toJson(new Response(Response.Status.ERROR, error.getMessage(), LocalDateTime.now().toString())));
                    }
            );
        } else {
            logger.error("Usuario no autenticado correctamente o no tiene permisos para esta acción");
            throw new ServerException("Usuario no autenticado correctamente o no tiene permisos para esta acción");
        }
    }


    private void procesasDelete(Request request) throws ServerException {
        var user = procesarToken(request.token());
        if (user.isPresent() && user.get().role().equals(User.Role.ADMIN)) { // Solo los admin pueden crear
            var myId = Long.parseLong(request.content());
            funkosService.deleteById(myId).subscribe(
                    funko -> {
                        var resJson = gson.toJson(funko); // Mandamos todo como cadenas
                        out.println(gson.toJson(new Response(Response.Status.OK, resJson, LocalDateTime.now().toString())));
                    },
                    error -> {
                        logger.error("Funko no encontrado con id: " + request.content());
                        out.println(gson.toJson(new Response(Response.Status.ERROR, error.getMessage(), LocalDateTime.now().toString())));
                    }
            );
        } else {
            logger.error("Usuario no autenticado correctamente o no tiene permisos para esta acción");
            throw new ServerException("Usuario no autenticado correctamente o no tiene permisos para esta acción");
        }
    }
}
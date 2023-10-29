# FunkoServer
Creación y despliegue de un servidor para hacer solicitudes en relación a objetos "Funkos".
<h1><b>Trabajo realizado por Miguel Zanotto y Laura Garrido</b></h1>
<h2>Estructura de nuestro repositorio:</h2>

![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/95bfcc31-623c-48ee-9dad-9d9640967202)
<h3>Carpeta develop</h3>
<p>Es nuestra carpeta principal donde se mostraran: Como crear un cliente con sus excepciones; las utilidades donde se tomaran en cuenta los modelos y adpatadores de los modelos para nuestro codigo; el servidor del cliente con sus excepciones, repositorios y servicios; Y por ultimo los manejadores
del Servidor y Cliente, preparados para ser ejecutados.</p>
<ul>
  <li><b>Carpeta Cliente</b>
  <ol>
    <li><i>Carpeta Exceptions</i>
    <b>ClientException</b>
<p>En esta clase se crea la excepcion que se creara con los Clientes si hay un problema con ellos.</p>
      
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/ccc7a59d-6e57-4e7f-ad2b-cebaf279f30d)
    </li>
    <li><u>Clase Client</u>
    <p>Esta clase, se representa un cliente que se comunica con un servidor. Su principal función es enviar solicitudes al servidor y gestionar la comunicación con él. Con los siguientes metodos:</p>
      
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/2ab9dff5-7c23-4100-8d2a-8722bc927094)
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/aef9d964-cf03-4c7a-9aa4-17c57c68ee5c)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/0a973747-1e4b-4453-8c35-8df489b0f676)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/e3912d53-4a5f-4bce-9b42-c2b4fbcbba0e)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/e54ac077-7eba-4d48-9def-3e495886370f)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/f1493e8d-23d9-4612-ba18-e90819ed742e)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/703698ef-0889-4ae2-8015-828bf00780cd)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/fe953ebe-2271-414f-9817-ca40742a4ddf)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/ea323dd8-961e-46d9-b102-2dca85b5b108)


<p>Configuración de la comunicación segura: La clase se encarga de configurar una comunicación segura con el servidor. Para ello, establece propiedades de seguridad, como el archivo de almacén de claves y la contraseña, y crea un socket seguro (SSLSocket) para la comunicación.</p>
<p>Inicio de la sesión: La clase inicia la sesión con el servidor enviando una solicitud de inicio de sesión (login) con un nombre de usuario y contraseña. El servidor responde con un token de autenticación, que se almacena en la variable "token."</p>
<p>Envío de solicitudes al servidor: La clase contiene métodos para enviar diversas solicitudes al servidor, incluyendo solicitudes para obtener todos los Funkos disponibles, obtener un Funko por su ID, obtener Funkos por modelo, obtener Funkos por año de lanzamiento, agregar un nuevo Funko, actualizar un Funko existente y eliminar un Funko por su ID. Estas solicitudes se envían al servidor en formato JSON.</p>
<p>Manejo de respuestas del servidor: La clase procesa las respuestas del servidor, que pueden ser de diferentes tipos, como "OK" (éxito) o "ERROR" (error). Dependiendo del tipo de respuesta, la clase realiza acciones específicas, como mostrar información en la consola o registrar errores.</p>
<p>Cierre de la sesión: La clase tiene un método para cerrar la sesión, que implica enviar una solicitud al servidor para cerrar la sesión del cliente. Esto provoca la desconexión del cliente del servidor.</p>

<p>Lectura de configuración: La clase lee la configuración del cliente desde un archivo de propiedades llamado "client.properties." Esta configuración incluye información como la ubicación del archivo de almacén de claves y la contraseña necesaria para la comunicación segura.
</p>
<p>Información de sesión: La clase muestra información detallada sobre la sesión de comunicación segura, como la información del servidor, el cifrado utilizado, el protocolo, el identificador de la sesión, la fecha de creación de la sesión y detalles del certificado del servidor.</p>
    </li>
  </ol>
  </li>
  <li><b>Carpeta Common</b>
  <ol>
    <li><i>Carpeta models</i>
      <ul>
<li><P>Aqui esta la clase Funko con sus atributos definidos en el codigo.</P>

  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/07084c16-299b-4741-82ee-cea5c55409cf)
</li>
<li><p>La clase Id Generator para crear un Id unico para cada Funko</p>

![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/8db81415-f7bf-40a7-8b20-0b2365b5d515)
</li>
<li><p>La clase record Login del cual se encarga de representar las credenciales para usuarios.</p>
  
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/b9c6d4d0-042d-4592-bc8d-ee0001426cd1)
</li>
<li><p>La clase Model del cual se muestra los modelos que se utilizaran para los Funkos.</p>
  
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/ca0e655e-b59f-4ded-9e44-b8f258fed6de)
</li>
<li><p>La clase My Locale, encargada del formato de la moneda y de las fechas dentro de Funko</p>
  
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/f4afea5c-7b8d-477f-b0f6-000dee3adde1)
</li>
<li><p>La clase Notificacion, encargada del formato de las notificaciones</p>

![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/02ae4fd1-9fd8-4cfc-a7ad-ebdea614ee5c)
</li>
<li>
  <p>La clase Request, encargado del formato de los request de cliente y Servidor.</p>
  
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/7f23ab65-a839-425f-ab55-bbb5a7b1cc2f)
</li>
<li>
  <p>La clase Response, encargado de representar las respuestas que pueda ocurrir.</p>
  
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/fbe8a513-a49b-4639-b58d-51c2702b1f7b)
</li>
<li>
  <p>La clase User, representando los roles de los usuarios.</p>
  
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/321d4878-c124-4679-86e7-7906ca672d87)
</li>
      </ul>
    </li>
    <li><i>Carpeta utils</i></li>
    <ul>
      <li><p>Clase LocalDateAdapter, encargado de adaptar los datos Date a JSON y viceversa</p>
        
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/f75f1ed2-f8de-4165-90c6-a87624ac653a)
      </li>
      <li><p>Clase LocalDateTimeAdapter, encargado de adaptar los datos DataTime a Json y viceversa</p>
      ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/2493a0ad-3c1a-4329-ba2e-18053bac7249)
      </li>
      <li><p>Clase PropiertiesReader, encargada de leer el fichero .properties</p>
      ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/f74d7c70-0590-442f-9b00-8939a965368a)
      </li>
      <li><p>Clase UUID adaper, encargado de la serializacion y deseralizacion del UUID de funkos</p>
      ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/0e312e55-061b-41e4-9f6f-77f78c28773d)
      </li>
    </ul>
  </ol>
  </li>
  <li><b>Capera Server</b>
  <ol>
   <li><i>Carpeta exceptions</i>
   <ul>
     <li><i>Carpeta funkos</i>
     <ul>
       <li><p>Clase Funko Exception, encargado de dar un mensaje cuando ocurra una excepcion en Funkos</p>
         
   ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/fef060f3-6cb3-4dab-a5f6-d57a37b0805e)
       </li>
       <li><p>Clase Funko no encontrado, encargado de dar un mensaje cuando no se encuentre un funko.</p>
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/e0b649ed-e711-4322-a662-dbf71c06b34f)
       </li>
     </ul>
     </li>
     <li><i>Carpeta server</i>
     <ul>
     <li><p>Clase Server Exception, encargado de dar un mensaje cuando haya una excepcion en el servidor.</p>
     ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/59417879-2ec2-4384-9d13-87b2e24f0b0f)
     </li>
     </ul>
     </li>
   </ul>
   </li>
    <li><i>Carpeta Repositories</i>
     <ul>
     <li><i>Carpeta crud</i>
     <ul>
       <li><p>Interfaz Crud Repository, encargada de definir las operaciones basicas de CRUD.</p>
         
   ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/61adc588-2da9-4431-aadb-192999b91e3d)
   ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/4e870c1e-fa5a-4403-b2dd-54c02fb484ad)
       </li>
     </ul>
     </li>
     <li><i>Carpeta funkos</i>
     <ul>
     <li><p>Interfaz FunkosRepository, encargado de extender las operaciones de Crud a Funko</p>
     ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/d38a653c-fa74-462c-b4b0-b8754f4d533c)
     </li>
     <li><p>CLase Funko RespositoryImpl, encargada de realizar las operaciones de CRUD de Funkos</p>
     ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/9feabc42-39bf-434b-b771-fe1d6baf7af5)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/1937c8a5-dac7-4794-b1c7-91e1c355eb4b)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/e1a480c7-4d5f-4ac7-a2ad-abccd69ba92b)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/33a3d04f-fd7b-4fb6-8006-b7feeef91e82)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/77411cc3-8077-4521-b398-230cc6dfb9c1)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/477d3bd2-804f-4cbd-9a60-8b82fbbfdc33)
![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/2159f2dc-7701-48b5-8985-50dda6f21281)
     </li>
     </ul>
     </li>
     <li><i>Carpeta users</i>
     <ul>
     <li><p>Clase UserRepository, encargado de almacenar usuarios en memoria.</p>
     ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/053f8446-9b71-44eb-ba31-f1062b200cd3)
     ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/4c555129-616c-4119-8141-8569a5ff7a68)
     </li>
     </ul>
     </li>
   </ul>
    </li>
     <li><i>Carpeta Services</i>
     <ul>
       <li><i>Services</i>
       <ul>
         <li><i>Cache</i>
         <ul>
           <li><p>Interfaz Cache, del cual define un cache para almacenar recuperar claves o valores.</p>
             
  ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/ded5bb28-e6f7-4d29-aa2b-c5dead9329f3)
           </li>
         </ul>
         </li>
         <li><i>Database</i>
         <ul>
         <li><p>Clase DataBaseManager, encargado de gestionar una conexion a la base de datos, inciar tablas con SQL y obteniendo un pool de conexiones</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/fe188731-19be-4463-b968-0fe9a7ac4c0b)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/22e4e96a-fa21-4514-85f5-6933f79704a8)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/9dcb1f82-0472-4035-9758-f4cb07b88e40)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/479ee925-768f-4512-9746-136dffbfbcd4)
         </li>
         </ul>
         </li>
         <li><i>Funkos</i>
         <ul>
         <li><p>Interfaz Funko Cache, encargado de cachear objetos de Funko utilizando el cache</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/94ef96fe-1c09-441c-a06e-8057b6b094a4)
         </li>
         <li><p>Clase FunkosCacheImpl, encargado de almacenar Funkos en un cache, elimandsoe por caducidad y tamaño maximo.</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/d3db9acf-e3fd-4a1f-93cd-1187772e2e88)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/4e55bf69-4855-464c-875b-5cbb93fc531d)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/73f51772-1bae-4f74-9322-39a59bc0b7aa)
         </li>
         <li><p>Intefaz FunkosNotification, encargado de notificar eventos</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/1e07e7bf-ad6f-423a-84e5-7982a821350d)
         </li>
         <li><p>Clase FunkosNotificationImpl, encargado de notificar eventos siguiendo la interfaz FunkosNotification.</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/6bbaec49-fd86-48fe-ad27-584dfa7d9e23)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/29c5abd9-3490-4b5e-beed-ee30306f86d1)
         </li>
         <li><p>Interfaz FunkoService, encargado de definir las operaciones con Funko</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/03bacbc5-fcd9-4682-9834-7a7c3474128c)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/57df1689-4db5-4eb0-afa8-1757bd8e660f)
         </li>
         <li><p>Clase FunkoServiceImpl, se encarga de administrar y proporcionar servicios relacionados con Funkos en el sistema, incluyendo la gestión de la base de datos, la caché y las notificaciones.</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/9f79c9ae-8bba-444b-b8a8-820dea58c5e2)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/84e8098e-2cae-4259-b17b-1ad523b147d4)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/1a0718c1-d865-4f10-a978-0a61b9dc6ec1)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/a3ee668c-8653-4db3-80b5-f73997d2b56b)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/a61849e9-1aaf-47c4-9dbd-85135fcf5ad3)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/0b92982e-3eca-49a8-82fd-e77d852dffa2)
         </li>
         <li><p>Interfaz FunkosStorage,encargado de definir las operaciones de almacenamiento de Funko</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/d847b177-381b-4b3b-adaa-c84706e23245)
         </li>
         <li><p>Clase FunkosStorageImpl, implementa la interfaz y se encarga de importar Funkos desde el csv.</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/710aa67f-1d74-461c-87fa-c4eb9f1fb992)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/807e33bf-da37-4c03-96f2-6339592deb08)
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/6f5eec8e-b56b-479b-a75e-aaf186004b04)
         </li>
         </ul>
         </li>
         <li><i>Storage</i>
         <ul>
         <li><p>Interfaz Storage, encargada de realizar operaciones para importar los datos del csv y emitirlos como Flux.</p>
         ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/14b09490-5542-4c62-88aa-00220fbb70fb)
         </li>
         </ul>
         </li>
       </ul>
       </li>
       <li><i>Token</i>
       <ul>
       <li><p>Clase Token Service, es la encargada de la creacion y verificacion de tokens del JSON</p>
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/9ef78ffc-e2ae-49a1-bca5-68a33d5c1b54)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/c7726f8e-e8b8-4539-b68f-3747e2f28ede)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/f4b38875-129b-4cc8-a87c-46b54eaab105)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/236a1746-ac43-4682-a50b-b665c5424532)
       </li>
       </ul>
       </li>
     </ul>
       <li><u>Clase ClientHandler,es el manejador del clinete a traves de un socket</u>
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/ca99437a-75d0-4211-a05d-779bedf6f9e3)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/ae0f4b1c-6d11-4d49-a111-bc376faba552)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/8eef1d31-4dc2-473f-a581-2ec6ad5a6086)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/cd064cb1-0855-430f-af96-75bcf8f3d52d)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/fb64e116-4386-48d6-801b-f15ad610eba9)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/9fe1890b-d5d9-4ce6-870e-b0ad78db028b)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/a444b27f-b472-4c1d-88bb-72e1f55b33d1)
       ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/7f30410b-df32-4ef6-87a6-15386519c9e0)
       </li>
        <li><u>Clase Server que representa al servidor, listo para ser ejecutado.</u>
        ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/f12c5fc1-885b-434e-ad4e-4348ce3676e2)
        ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/d8308029-01ca-44e3-a2c1-356c1bf114b4)
        ![image](https://github.com/MiguelZanottto/FunkoServer/assets/132077920/efc6b1f1-8740-49e2-8fdf-60b2a6a88df9)
        </li>
     </li>
  </ol>
  </li>
</ul>

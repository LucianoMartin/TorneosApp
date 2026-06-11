Proyecto creado por Ivan Kozicz Czura y Luciano Martin.

La aplicacion permite crear y gestionar torneos, a la vez que espectadores pueden predecir los resultados para ganar puntos por premios.

La aplicación utiliza Firebase, por lo que es necesario configurar una cuenta propia para poder ejecutarla correctamente.

1. Crear proyecto en Firebase
Ingresar a: https://console.firebase.google.com/
Iniciar sesión con una cuenta de Google.
Seleccionar “Crear proyecto”.
Asignarle un nombre al proyecto (por ejemplo: TorneosApp).
Continuar hasta finalizar la creación (no es necesario habilitar Google Analytics si no se desea).

2. Registrar la aplicación Android
Dentro del proyecto de Firebase, hacer clic en “Agregar app” → Android.
Ingresar el package name del proyecto (debe coincidir exactamente con el de Android Studio, por ejemplo: com.tpgrupal.appsmoviles).
(Opcional) Agregar nickname.
Registrar la app.

3. Descargar el archivo google-services.json
Luego de registrar la app, Firebase permitirá descargar el archivo google-services.json.
Descargarlo y guardarlo.

4. Ubicación del archivo en el proyecto

Colocar el archivo descargado en la siguiente ruta del proyecto:

app/google-services.json

5. Sincronizar y ejecutar

Luego de agregar el archivo:

Sincronizar Gradle
Ejecutar la aplicación

Si necesita el archivo nos lo puede pedir
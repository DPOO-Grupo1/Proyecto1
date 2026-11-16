# Proyecto 2: Club Deportivo Séneca

**Diseño y Programación Orientada a Objetos (ISIS-1226)**  
**Grupo 1**

Aplicación de consola para el club. Hay tres consolas, una para cada tipo de usuario (socio, empleado y administrador). Cada una pide login y contraseña y muestra solo lo que ese usuario puede hacer. Los datos se cargan de la carpeta `datos/` al iniciar y se guardan ahí al salir.

## 1. Estructura

```text
Proyecto 2/Entrega única/
├── .project, .classpath        # Proyecto de Eclipse (Proyecto2)
├── lib/
│   └── org.json-1.6-20240205.jar
├── datos/                      # Archivos JSON con los datos del club
│   ├── usuarios.json
│   ├── deportes.json
│   ├── competencias.json
│   ├── torneos.json
│   ├── academias.json
│   ├── entrenamiento.json
│   ├── tienda.json
│   └── personal.json
├── docs/
│   ├── diagrama-clases-diseno.pdf      # Diagrama de clases (también .png y .uxf)
│   ├── diagrama-alto-nivel.pdf         # Diagrama de alto nivel (también .png y .uxf)
│   ├── documentacion_Diagramas_Secuencia_modelo.pdf   # Documento de diseño
│   ├── historias-usuario.pdf           # Historias de usuario (banco de la Entrega 1 y torneos)
│   ├── historias-usuario.md            # Las mismas historias en Markdown
│   └── diagramas/
│       ├── secuencias/         # Diagramas de secuencia (.mmd en formato_raiz, imágenes en imagenes)
│       └── sustentacion/       # Diagramas de flujo y de estados
├── src/
│   ├── consola/                # Las tres consolas y sus menús
│   ├── logica/                 # Club y los gestores
│   ├── modelo/                 # Clases del dominio, por paquete
│   ├── persistencia/           # Carga y guardado en JSON
│   ├── excepciones/
│   └── pruebas/                # Programas de prueba de la Entrega 2
└── test/                       # Pruebas JUnit 5
```

## 2. Cómo abrir el proyecto en Eclipse

1. Abrir Eclipse y escoger **File > Import > General > Existing Projects into Workspace**.
2. En **Select root directory** escoger la carpeta `Proyecto 2/Entrega única`.
3. Aparece el proyecto **Proyecto2**. Marcarlo y dar **Finish**.
4. El proyecto ya trae en el classpath `lib/org.json-1.6-20240205.jar` y JUnit 5. Si Eclipse marca error en la librería, ir a **Build Path > Configure Build Path > Libraries** y agregar el JAR de `lib/` con **Add JARs...**.

Se necesita Java 21.

## 3. Cómo correr las consolas

Las tres consolas están en `src/consola`. Se corren con clic derecho, **Run As > Java Application**:

| Clase | Quién entra |
|---|---|
| `ConsolaSocio` | Socios del club y participantes externos de torneos |
| `ConsolaEmpleado` | Entrenadores y fisioterapeutas |
| `ConsolaAdministrador` | Administrador |

Usuarios de prueba (están todos en `datos/usuarios.json`):

| Login | Contraseña | Tipo |
|---|---|---|
| `admin` | `123` | Administrador |
| `e1`, `e2`, `e3` | `pass` | Entrenadores |
| `j1` a `j14`, `b1` a `b14`, `h1` a `h8`, y los demás socios | `pass` | Socios |
| `x1`, `x2`, `x3` | `pass` | Participantes externos (solo torneos) |

Las opciones se escogen por número y `0` vuelve al menú anterior. Si una operación no cumple alguna regla del club, la consola muestra `RECHAZADO:` con el motivo y no cambia nada. Al salir con `0` desde el menú principal se guardan los cambios en `datos/`.

## 4. Qué se puede hacer en cada consola

### Consola del socio

| Menú | Opciones | Requerimientos |
|---|---|---|
| Deportes y competencias | Ver la información deportiva propia, tablas de posiciones, goleadores y destacados | RF1, RF2 |
| Academias y prácticas | Inscribirse en una academia, reservar, registrar ingreso, cancelar, programar partidos de práctica y su resultado, ver perfil, historial y ranking, ver convocatorias | RF6 a RF11, RF16 |
| Tienda y cafetería | Comprar en la tienda y en la cafetería, ver puntos, compras y bonos | RF3, RF4, RF5 |
| Torneos internos | Ver torneos y su historial, inscribirse en un torneo individual, salirse, ver mis torneos y mis bonos. El entrenador también puede inscribir y retirar sus equipos | HU_T3, HU_T4, HU_T5, HU_T9, HU_T10 |

Los participantes externos solo ven el menú de torneos internos.

### Consola del empleado

| Menú | Opciones | Requerimientos |
|---|---|---|
| Equipos y partidos internos (entrenador) | Ver mis equipos, programar partidos internos, registrar resultado y estadísticas | RF12, RF13, RF14 |
| Entrenamientos (entrenador) | Planear sesiones, finalizarlas, registrar asistencia y evaluaciones | RF15, RF16 |
| Tienda y cafetería | Compras con descuento de empleado | RF17, RF20 |
| Turnos y sugerencias | Ver mis turnos, solicitar cambios de turno, sugerir productos | RF18, RF19, RF21 |
| Torneos internos | Las mismas opciones del socio | HU_T3 a HU_T5, HU_T9, HU_T10 |

### Consola del administrador

| Menú | Opciones | Requerimientos |
|---|---|---|
| Equipos y jugadores | Crear equipos, asignar entrenador, inscribir y retirar jugadores, dar de baja equipos | RF22, RF23, RF24 |
| Competencias externas | Crear competencias, rivales externos, inscribir equipos, programar partidos, registrar resultados y estadísticas, ver tablas | RF2, RF25, RF26, RF27 |
| Academias y reservas | Inscribir socios, consultar reservas e instalaciones | RF6, RF36 |
| Inventario de tienda y cafetería | Consultar inventario, mover productos, reabastecer y registrar productos | RF29, RF30, RF31 |
| Turnos y sugerencias del personal | Publicar turnos, resolver solicitudes de cambio y sugerencias | RF32, RF33 |
| Reportes y mensualidades | Historial global, reportes financiero y deportivo, registrar mensualidades | RF28, RF34, RF35 |
| Torneos internos | Crear torneos, registrar participantes externos, inscribir y retirar, liberar cupos preferenciales, iniciar y registrar resultados | HU_T1 a HU_T9 |

Las historias de usuario están en `docs/historias-usuario.pdf` (y en Markdown en `docs/historias-usuario.md`). El código de cada historia coincide con el inicio del nombre de sus pruebas: HU_RF3 se prueba con `rf3...` y HU_T3 con `hut3...`.

## 5. Persistencia

`CentralPersistencia` carga y guarda todo el club. Cada parte del club tiene su clase de persistencia y su archivo en `datos/` (por ejemplo, `PersistenciaTorneos` con `torneos.json`). La carpeta se busca sola, así que las consolas funcionan desde Eclipse o desde la terminal.

## 6. Pruebas JUnit

Las pruebas están en `test/`, con los mismos paquetes de `src/`:

- **Unitarias**, en `test/modelo` y `test/persistencia`: prueban una clase del modelo sin gestor (por ejemplo, la tabla de una competencia, los formatos de torneo, las ventas o los turnos).
- **De integración**, en `test/logica`: llaman a los gestores. Cada prueba empieza con el requerimiento que verifica (por ejemplo `rf8...` o `hut3...`), y casi todos los requerimientos tienen un caso aceptado y uno rechazado.

Las pruebas no usan los archivos de `datos/`. Cada clase `ClubDePrueba...` arma un club pequeño en memoria con una fecha fija.

Para correrlas: clic derecho sobre la carpeta `test`, **Run As > JUnit Test**.

## 7. Ejecución desde la terminal

Desde la carpeta `Proyecto 2/Entrega única`:

```bash
javac -d bin -cp "lib/org.json-1.6-20240205.jar" $(find src -name "*.java")
java -cp "bin:lib/org.json-1.6-20240205.jar" consola.ConsolaAdministrador
```

Son comandos de macOS o Linux; en Windows el separador del classpath es `;` en lugar de `:`. Las otras consolas son `consola.ConsolaSocio` y `consola.ConsolaEmpleado`.

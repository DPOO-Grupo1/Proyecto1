# Entrega 2
**Diseño y Programación Orientada a Objetos (DPOO)**  
**Grupo 1**


## 1. Estructura del Repositorio

El proyecto se encuentra en la carpeta `Entrega 2/` y sigue la siguiente arquitectura por capas y paquetes:

```text
Proyecto1/
├── Entrega 1/                          # Documento de análisis y diseño inicial
├── Entrega 2/                          # Código fuente y recursos de la Entrega 2
│   ├── .project                        # Descriptor de proyecto de Eclipse
│   ├── .classpath                      # Configuración de classpath de Eclipse (Java + lib)
│   ├── lib/
│   │   └── org.json-1.6-20240205.jar   # Biblioteca para serialización y parseo JSON
│   ├── datos/                          # Conjuntos de datos JSON para persistencia
│   │   ├── academias.json
│   │   ├── competencias.json
│   │   ├── deportes.json
│   │   ├── entrenamiento.json
│   │   ├── personal.json
│   │   ├── tienda.json
│   │   └── usuarios.json
│   ├── docs/
│   │   ├── diagrama-alto-nivel.pdf     # Diagrama de arquitectura de alto nivel
│   │   ├── diagrama-clases-diseno.pdf  # Diagrama de clases completo
│   │   └── diagramas/
│   │       ├── secuencias/             # Diagramas de secuencia UML en formato Mermaid
│   │       │   ├── formato_raiz/       # Archivos .mmd editables
│   │       │   └── imagenes/           # Exportaciones gráficas (.png / .jpeg)
│   │       └── sustentacion/           # Diagramas de flujo y estados
│   │           ├── formato_raiz/       # Archivos .mmd editables
│   │           └── imagenes/           # Exportaciones gráficas
│   └── src/
│       ├── modelo/                     # Clases de dominio del negocio
│       │   ├── academias/              # Disciplina, Instalacion, Canchas, Reserva, ELO
│       │   ├── competencias/           # Competencia, Grupo, TablaPosiciones
│       │   ├── deportes/               # DeporteEquipo, Categoria, Equipo, Suspension
│       │   ├── entrenamiento/          # SesionEntrenamiento, Convocatoria, Evaluacion
│       │   ├── partidos/               # PartidoInterno, PartidoCompetencia, Estadisticas
│       │   ├── personal/               # HorarioAtencion, Turno, Solicitud, Sugerencia
│       │   ├── reportes/               # Historial, ReporteFinanciero, ReporteDeportivo, Mensualidad
│       │   ├── tienda/                 # Articulo, Bebida, Snack, VentaTienda, VentaCafeteria
│       │   └── usuarios/               # Socio, Entrenador, Fisioterapeuta, Administrador
│       ├── logica/                     # Controladores y lógica de negocio
│       │   ├── Club.java               # Fachada central y contenedor de entidades
│       │   ├── Gestor.java             # Clase base de gestores
│       │   ├── GestorAcademias.java
│       │   ├── GestorCompetencias.java
│       │   ├── GestorDeportes.java
│       │   ├── GestorEntrenamiento.java
│       │   ├── GestorPersonal.java
│       │   ├── GestorReportes.java
│       │   └── GestorTienda.java
│       ├── persistencia/               # Módulos de persistencia JSON independientes
│       │   ├── CentralPersistencia.java
│       │   ├── IPersistenciaModulo.java
│       │   ├── PersistenciaAcademias.java
│       │   ├── PersistenciaCompetencias.java
│       │   ├── PersistenciaDeportes.java
│       │   ├── PersistenciaEntrenamiento.java
│       │   ├── PersistenciaPersonal.java
│       │   ├── PersistenciaTienda.java
│       │   └── PersistenciaUsuarios.java
│       ├── excepciones/                # Jerarquía de excepciones de negocio del club
│       └── pruebas/                    # Programas de prueba (Consolas PP1 a PP6)
│           ├── ConsolaPP1.java
│           ├── ConsolaPP2.java
│           ├── ConsolaPP3.java
│           ├── ConsolaPP4.java
│           ├── ConsolaPP5.java
│           ├── ConsolaPP6.java
│           ├── PruebaPersistenciaAreaB.java
│           └── PruebaPersistenciaC.java
└── README.md                           # Guía de ejecución del proyecto
```

---

## 2. Guía para Monitoras: Cómo abrir y correr el proyecto en Eclipse

El proyecto ya cuenta con los metadatos oficiales de Eclipse (`.project` y `.classpath`) apuntando a la biblioteca `lib/org.json-1.6-20240205.jar`, por lo que **no requiere instalación de plugins adicionales**.

### Paso 1: Importar en Eclipse
1. Abrir **Eclipse IDE**.
2. En el menú superior, seleccionar **File** $\rightarrow$ **Open Projects from File System...**  
   *(o alternativamente: **File** $\rightarrow$ **Import...** $\rightarrow$ **General** $\rightarrow$ **Existing Projects into Workspace**)*.
3. En el campo **Import source**, hacer clic en **Directory...** y seleccionar la carpeta:
   ```text
   .../Proyecto1/Entrega 2
   ```
4. Eclipse detectará automáticamente el proyecto llamado **`Proyecto1`**.
5. Asegurarse de que la casilla esté marcada y hacer clic en **Finish**.

### Paso 2: Verificar la biblioteca externa (JAR de JSON)
1. En la vista **Package Explorer**, expandir el proyecto `Proyecto1`.
2. Verificar que aparezca la carpeta **`Referenced Libraries`** con el archivo:
   ```text
   org.json-1.6-20240205.jar
   ```
3. Si el proyecto muestra un icono de advertencia rojo en las librerías:
   - Clic derecho sobre `Proyecto1` $\rightarrow$ **Build Path** $\rightarrow$ **Configure Build Path...**
   - Pestaña **Libraries** $\rightarrow$ Seleccionar **Classpath** $\rightarrow$ **Add JARs...**
   - Seleccionar `Proyecto1/lib/org.json-1.6-20240205.jar` $\rightarrow$ Clic en **Apply and Close**.

### Paso 3: Ejecutar las Consolas de Prueba
Cada programa de prueba oficial (`PP1` a `PP6`) es una clase autónoma ejecutable con método `main`:
1. En el **Package Explorer**, expandir la carpeta:
   ```text
   src  ->  pruebas
   ```
2. Hacer **clic derecho** sobre cualquiera de las consolas (por ejemplo, `ConsolaPP1.java`).
3. Seleccionar **Run As** $\rightarrow$ **1 Java Application**.
4. En la parte inferior, en la pestaña **Console**, se desplegará el encabezado del programa y cada prueba unitaria/de integración con la verificación de sus precondiciones, casos aprobados y casos rechazados con excepción.

---

## 4. Consolas de Prueba Oficiales (Mapeo de Requerimientos)

| Consola | Nombre y Alcance | Requerimientos Evaluados | Descripción de Pruebas |
|---|---|---|---|
| **`ConsolaPP1.java`** | **Gestión deportiva y partidos internos** | **RF12, RF13, RF14, RF22, RF23, RF24** | Creación de deportes de equipo y categorías; registro de equipos y nóminas; programación de partidos internos; verificación de nómina mínima y habilitación de jugadores; captura de marcadores y estadísticas detalladas por deporte (fútbol, baloncesto, voleibol); acumulación de tarjetas amarillas y generación automática de suspensiones por tarjeta roja o doble amarilla. |
| **`ConsolaPP2.java`** | **Competencias y partidos oficiales** | **RF1, RF2, RF25, RF26, RF27** | Creación de torneos en formato todos contra todos o eliminación directa; inscripción de equipos; programación del calendario oficial; captura de resultados; cálculo automático de la tabla de posiciones con criterios de desempate por enfrentamiento directo; y control de acumulación de tarjetas amarillas en competencia. |
| **`ConsolaPP3.java`** | **Ventas, fidelidad y restricciones** | **RF3, RF4, RF5, RF17, RF20** | Compras en tienda física con IVA (19%); consumo en cafetería con impoconsumo (8%) y propina voluntaria; consulta obligatoria de alérgenos; restricción de seguridad que bloquea la venta de bebidas calientes a usuarios con reservas activas en pádel o mesas techadas; aplicación de códigos de descuento de empleados (15% propio, 8% ajeno); acumulación del 2% en puntos de fidelidad; y redención de puntos como descuento monetario. |
| **`ConsolaPP4.java`** | **Academias individuales, reservas y ELO** | **RF6, RF7, RF8, RF9, RF10, RF11, RF36** | Inscripciones en disciplinas individuales (tenis, tenis de mesa, pádel); reservas de instalaciones con validación de horario, disciplina, número de jugadores, modalidad (sencillos/dobles) y mensualidad al día; registro de ingreso a la cancha; cancelación anticipada ($\ge 2$ horas antes) con liberación de cupo y cascada a partidos; programación de partidos de práctica; actualización matemática del ranking ELO (fórmula logística con factor K=32); y consultas administrativas. |
| **`ConsolaPP5.java`** | **Entrenamiento, sesiones y asistencia** | **RF15, RF16** | Planeación de sesiones grupales, individuales y de equipo por el entrenador responsable; validación de compatibilidad con la instalación y cruces de horario; convocatoria de socios inscritos; registro de asistencia (asistió, faltó, tarde) únicamente sobre sesiones finalizadas; asignación de evaluaciones técnicas formativas (calificación 1 a 5 con retroalimentación); y consulta de convocatorias. |
| **`ConsolaPP6.java`** | **Administración del club** | **RF18, RF19, RF21, RF28, RF29, RF30, RF31, RF32, RF33, RF34, RF35** | Consulta y auditoría de inventario por ubicación; traslado de productos entre almacenes; reabastecimiento e ingreso de nuevos artículos/snacks; ciclo de vida de sugerencias del personal (sugerir, aprobar, rechazar con motivo); gestión semanal de turnos garantizando la cobertura mínima (2 entrenadores y 1 fisioterapeuta por bloque); solicitudes y resolución de cambios de turno; consulta del historial global con filtros avanzados; generación de reportes financieros y deportivos; y control de mensualidades con reactivación de socios en mora. |

---

## 5. Pruebas de Persistencia JSON

Para comprobar la serialización y carga de datos en formato JSON de forma independiente a la lógica interactiva:

- **`PruebaPersistenciaAreaB.java`**: Valida el ciclo completo de guardado y carga de socios, inscripciones, instalaciones, reservas, partidos de práctica, ELO y sesiones de entrenamiento en los archivos `academias.json` y `entrenamiento.json`.
- **`PruebaPersistenciaC.java`**: Valida el guardado y recarga de productos, existencias por ubicación, ventas, códigos de descuento, turnos y personal en `tienda.json` y `personal.json`.

Los archivos JSON persistidos se encuentran organizados en la carpeta:
```text
Entrega 2/datos/
```

---

## 6. Ejecución desde Terminal

Si se prefiere compilar y ejecutar todo el suite de pruebas directamente desde la línea de comandos:

```bash
# 1. Posicionarse en la carpeta de la Entrega 2
cd "Entrega 2"

# 2. Compilar todas las clases en una carpeta bin temporal o local
find src -name "*.java" > /tmp/sources.txt
javac -d bin -cp "lib/org.json-1.6-20240205.jar" @/tmp/sources.txt

# 3. Ejecutar cualquiera de las consolas oficiales:
java -cp "bin:lib/org.json-1.6-20240205.jar" pruebas.ConsolaPP1
java -cp "bin:lib/org.json-1.6-20240205.jar" pruebas.ConsolaPP2
java -cp "bin:lib/org.json-1.6-20240205.jar" pruebas.ConsolaPP3
java -cp "bin:lib/org.json-1.6-20240205.jar" pruebas.ConsolaPP4
java -cp "bin:lib/org.json-1.6-20240205.jar" pruebas.ConsolaPP5
java -cp "bin:lib/org.json-1.6-20240205.jar" pruebas.ConsolaPP6
```

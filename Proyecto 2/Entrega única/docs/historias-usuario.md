# Historias de usuario

Club Deportivo Séneca, Proyecto 2. Grupo 1.

Este documento reúne las historias de usuario del Proyecto 2 en cuatro partes. La primera tiene las historias del banco de la Entrega 1 para los deportes de conjunto y las competencias. La segunda, las de la tienda, la cafetería, el personal y los reportes. La tercera, las historias nuevas de los torneos internos. La cuarta, las historias del banco de la Entrega 1 para academias, reservas, prácticas y entrenamiento.

## Cómo están numeradas

- **HU_RFn** es la historia del banco de la Entrega 1 que corresponde al requerimiento RFn. Debajo del título está el código que tenía en la Entrega 1 (por ejemplo, HU_RF3 era HU_S2). HU_RF1 y HU_RF25 no tenían historia en la Entrega 1, así que son nuevas.
- **HU_Tn** es una historia nueva de torneos internos, de HU_T1 a HU_T10.

Las pruebas de integración de cada historia empiezan con el mismo código en minúscula: `rf3...` para HU_RF3 y `hut3...` para HU_T3.

| Historia | Código en la Entrega 1 | Título | Pruebas |
|---|---|---|---|
| [HU_RF1](#hu_rf1-consultar-mi-información-deportiva) | No tenía | Consultar mi información deportiva | `GestorDeportesTest` |
| [HU_RF2](#hu_rf2-consultar-tablas-y-clasificaciones-de-una-competencia) | HU_S1 | Consultar tablas y clasificaciones de una competencia | `GestorCompetenciasTest`, `CompetenciaTest` |
| [HU_RF6](#hu_rf6-inscribirse-en-una-academia) | HU_S5 | Inscribirse en una academia | `GestorAcademiasTest` |
| [HU_RF7](#hu_rf7-reservar-una-instalación-y-registrar-ingreso) | HU_S6 | Reservar una instalación y registrar ingreso | `GestorAcademiasTest`, `AcademiaYReservaTest` |
| [HU_RF8](#hu_rf8-cancelar-una-reserva) | HU_S7 | Cancelar una reserva | `GestorAcademiasTest`, `AcademiaYReservaTest` |
| [HU_RF9](#hu_rf9-programar-un-partido-de-práctica) | HU_S8 | Programar un partido de práctica | `GestorAcademiasTest` |
| [HU_RF10](#hu_rf10-registrar-el-resultado-de-un-partido-de-práctica) | HU_S9 | Registrar el resultado de un partido de práctica | `GestorAcademiasTest`, `AcademiaYReservaTest` |
| [HU_RF11](#hu_rf11-consultar-perfil-historial-y-ranking-elo) | HU_S10 | Consultar perfil, historial y ranking ELO | `GestorAcademiasTest`, `AcademiaYReservaTest` |
| [HU_RF15](#hu_rf15-planear-una-sesión-de-entrenamiento) | HU_E5 | Planear una sesión de entrenamiento | `GestorEntrenamientoTest`, `EntrenamientoModeloTest` |
| [HU_RF16](#hu_rf16-registrar-asistencia-evaluación-y-consultar-convocatorias) | HU_E6 | Registrar asistencia, evaluación y consultar convocatorias | `GestorEntrenamientoTest`, `EntrenamientoModeloTest` |
| [HU_RF36](#hu_rf36-consultar-reservas-e-instalaciones) | HU_A13 | Consultar reservas e instalaciones | `GestorAcademiasTest` |
| [HU_RF12](#hu_rf12-programar-un-partido-interno) | HU_E1 | Programar un partido interno | `GestorDeportesTest` |
| [HU_RF13](#hu_rf13-registrar-el-resultado-y-las-estadísticas-de-un-partido-interno) | HU_E2 | Registrar el resultado y las estadísticas de un partido interno | `GestorDeportesTest`, `DeporteYCategoriaTest` |
| [HU_RF14](#hu_rf14-registrar-tarjetas-y-suspender-jugadores) | HU_E3 | Registrar tarjetas y suspender jugadores | `GestorDeportesTest`, `EquipoYSuspensionTest`, `CompetenciaTest` |
| [HU_RF22](#hu_rf22-crear-un-equipo) | HU_A1 | Crear un equipo | `GestorDeportesTest` |
| [HU_RF23](#hu_rf23-inscribir-a-un-socio-en-un-equipo) | HU_A2 | Inscribir a un socio en un equipo | `GestorDeportesTest`, `DeporteYCategoriaTest`, `EquipoYSuspensionTest` |
| [HU_RF24](#hu_rf24-dar-de-baja-equipos-y-retirar-jugadores) | HU_A3 | Dar de baja equipos y retirar jugadores | `GestorDeportesTest`, `EquipoYSuspensionTest` |
| [HU_RF25](#hu_rf25-crear-una-competencia-y-registrar-a-sus-rivales) | No tenía | Crear una competencia y registrar a sus rivales | `GestorCompetenciasTest`, `CompetenciaTest` |
| [HU_RF26](#hu_rf26-inscribir-un-equipo-del-club-en-una-competencia) | HU_A4 | Inscribir un equipo del club en una competencia | `GestorCompetenciasTest` |
| [HU_RF27](#hu_rf27-programar-y-registrar-partidos-oficiales) | HU_A5 | Programar y registrar partidos oficiales | `GestorCompetenciasTest`, `CompetenciaTest`, `DeporteYCategoriaTest` |
| [HU_RF3](#hu_rf3-comprar-en-la-tienda) | HU_S2 | Comprar en la tienda | `GestorTiendaTest`, `VentaTest` |
| [HU_RF4](#hu_rf4-consumir-en-la-cafetería) | HU_S3 | Consumir en la cafetería | `GestorTiendaTest`, `VentaTest` |
| [HU_RF5](#hu_rf5-redimir-puntos-de-fidelidad) | HU_S4 | Redimir puntos de fidelidad | `GestorTiendaTest`, `VentaTest` |
| [HU_RF17](#hu_rf17-comprar-con-código-de-descuento-de-empleado) | HU_E4 | Comprar con código de descuento de empleado | `GestorTiendaTest`, `VentaTest` |
| [HU_RF18](#hu_rf18-solicitar-un-cambio-de-turno) | HU_E7 | Solicitar un cambio de turno | `GestorPersonalTest` |
| [HU_RF19](#hu_rf19-solicitar-un-cambio-de-turno-y-consultar-su-estado) | HU_F1 | Solicitar un cambio de turno y consultar su estado | `GestorPersonalTest`, `TurnoTest` |
| [HU_RF20](#hu_rf20-comprar-con-descuento-de-empleado) | HU_F2 | Comprar con descuento de empleado | `GestorTiendaTest` |
| [HU_RF21](#hu_rf21-sugerir-productos) | HU_F3 | Sugerir productos | `GestorPersonalTest` |
| [HU_RF28](#hu_rf28-consultar-el-historial-global) | HU_A8 | Consultar el historial global | `GestorReportesTest` |
| [HU_RF29](#hu_rf29-consultar-el-inventario) | HU_A6 | Consultar el inventario | `GestorTiendaTest` |
| [HU_RF30](#hu_rf30-mover-productos-entre-ubicaciones) | HU_A7 | Mover productos entre ubicaciones | `GestorTiendaTest` |
| [HU_RF31](#hu_rf31-reabastecer-y-registrar-productos) | HU_A6c | Reabastecer y registrar productos | `GestorTiendaTest` |
| [HU_RF32](#hu_rf32-resolver-sugerencias-de-productos) | HU_A9 | Resolver sugerencias de productos | `GestorPersonalTest` |
| [HU_RF33](#hu_rf33-gestionar-turnos-y-solicitudes-de-cambio) | HU_A10 | Gestionar turnos y solicitudes de cambio | `GestorPersonalTest`, `TurnoTest` |
| [HU_RF34](#hu_rf34-generar-reportes) | HU_A11 | Generar reportes | `GestorReportesTest` |
| [HU_RF35](#hu_rf35-registrar-mensualidades) | HU_A12 | Registrar mensualidades | `GestorReportesTest` |
| [HU_T1](#hu_t1-crear-un-torneo) | Nueva | Crear un torneo | `GestorTorneosTest`, `TorneoTest` |
| [HU_T2](#hu_t2-registrar-un-estudiante-o-participante-externo) | Nueva | Registrar un estudiante o participante externo | `GestorTorneosTest` |
| [HU_T3](#hu_t3-inscribirme-en-un-torneo-individual) | Nueva | Inscribirme en un torneo individual | `GestorTorneosTest`, `TorneoTest` |
| [HU_T4](#hu_t4-inscribir-un-equipo-en-un-torneo-de-conjunto) | Nueva | Inscribir un equipo en un torneo de conjunto | `GestorTorneosTest` |
| [HU_T5](#hu_t5-salirme-de-un-torneo) | Nueva | Salirme de un torneo | `GestorTorneosTest`, `TorneoTest` |
| [HU_T6](#hu_t6-liberar-los-cupos-preferenciales) | Nueva | Liberar los cupos preferenciales | `GestorTorneosTest`, `TorneoTest` |
| [HU_T7](#hu_t7-iniciar-un-torneo) | Nueva | Iniciar un torneo | `GestorTorneosTest`, `FormatoTorneoTest` |
| [HU_T8](#hu_t8-registrar-el-resultado-de-un-encuentro-y-entregar-el-premio) | Nueva | Registrar el resultado de un encuentro y entregar el premio | `GestorTorneosTest`, `FormatoTorneoTest` |
| [HU_T9](#hu_t9-consultar-los-torneos-y-su-historial) | Nueva | Consultar los torneos y su historial | `GestorTorneosTest`, `FormatoTorneoTest` |
| [HU_T10](#hu_t10-consultar-y-usar-mis-bonos) | Nueva | Consultar y usar mis bonos | `GestorTorneosTest`, `PersistenciaTorneosTest`, `GestorTiendaTest`, `VentaTest` |

## Parte 1. Deportes de conjunto y competencias

Estas historias son las del banco de la Entrega 1 para los deportes de conjunto y las competencias externas (RF1, RF2, RF12 a RF14 y RF22 a RF27). Para cada una se indica desde dónde se usa en la consola, sus entradas y salidas, sus criterios de aceptación, los cambios frente a la Entrega 1 y la prueba JUnit que la verifica (en `test/`). Las pruebas de integración están en `GestorDeportesTest` y `GestorCompetenciasTest` y tienen el requerimiento al inicio del nombre (por ejemplo `rf23...`). RF1 y RF25 no tenían historia en el banco de la Entrega 1, así que sus historias son nuevas.

### Supuestos de la parte 1

El análisis deja abiertos algunos puntos. Estas son las decisiones del grupo para esta parte:

1. **Mínimo de habilitados.** Cada deporte fija cuántos jugadores habilitados necesita un equipo para jugar: 7 en fútbol, 5 en baloncesto y 6 en voleibol. Un jugador habilitado está activo en el equipo y no tiene una suspensión pendiente para ese partido.
2. **Puntos por deporte.** En fútbol la victoria da 3 puntos, el empate 1 y la derrota 0. En baloncesto y voleibol la victoria da 2 y no hay empates. El marcador del voleibol es en sets.
3. **Edad por categoría.** La edad que cuenta es la que el socio tenía el 1 de enero del año. Mayores no tiene edad máxima.
4. **Suspensiones por contexto.** Una roja o dos amarillas en un partido suspenden al jugador para el siguiente partido del mismo tipo: si fue en un partido interno, para el siguiente interno de su equipo; si fue en una competencia, para el siguiente partido de esa competencia. La suspensión se cumple cuando su equipo juega ese partido.
5. **Amarillas acumuladas.** Cada competencia fija cuántas amarillas acumuladas suspenden. Se cuentan desde la última sanción del jugador en esa competencia.
6. **Desempate en la tabla.** Se ordena por puntos, luego por el enfrentamiento directo y luego por la diferencia.
7. **Jugador destacado.** Es el de más asistencias en fútbol, más puntos en baloncesto y más aces en voleibol.
8. **Retiros y bajas.** Retirar a un jugador no lo borra: queda inactivo, conserva su historial y deja libre su número. Dar de baja un equipo deja inactivos a todos sus jugadores.
9. **Rivales externos.** Los equipos de otras instituciones se registran dentro de cada competencia, sin jugadores. Sus partidos entre ellos también cuentan para la tabla.

### HU_RF1. Consultar mi información deportiva

No tenía historia en la Entrega 1.

**Como** socio, **quiero** ver los equipos en los que juego, los partidos de cada uno con mis estadísticas y si estoy suspendido, **para** seguir mi desempeño deportivo.

- **Entradas:** desde la consola del socio, Deportes y competencias, Ver mi información deportiva. No pide datos.
- **Salidas:** por cada equipo en el que está o estuvo inscrito, el equipo, la posición, el número y si fue retirado; los partidos de ese equipo con su estadística o "no jugó"; y la suspensión pendiente, si tiene. Si no está en ningún equipo, un aviso.

Criterios de aceptación:

1. Dado que juego en un equipo y tengo estadística en uno de sus partidos, cuando consulto mi información, entonces veo el equipo y ese partido con mi estadística.
2. Dado que tengo una suspensión pendiente, cuando consulto mi información, entonces aparece con su motivo.
3. Dado que intento ver los partidos de la participación de otro socio, cuando consulto, entonces se rechaza por permiso.
4. Dado que soy administrador o empleado, cuando intento consultar, entonces se rechaza por permiso.

INVEST: independiente de las competencias, valiosa porque es lo que el socio consulta de sus deportes, pequeña y verificable.

Pruebas: `rf1ElSocioVeSusEquiposYLosPartidosDeCadaUno`, `rf1NadieConsultaLaInformacionDeOtroSocio`.

### HU_RF2. Consultar tablas y clasificaciones de una competencia

Código en la Entrega 1: HU_S1.

**Como** socio, **quiero** consultar la tabla de posiciones y las clasificaciones de goleadores o jugadores destacados de una competencia, **para** conocer el estado y la evolución del torneo.

- **Entradas:** desde la consola del socio, Deportes y competencias, Ver la tabla de posiciones o Ver goleadores y jugadores destacados. El administrador tiene las mismas opciones en Competencias externas. Filtros opcionales por deporte y por categoría, la competencia (elegida de la lista) y, en formato de grupos, el grupo.
- **Salidas:** la tabla con partidos jugados, ganados, empatados y perdidos, tantos a favor y en contra, diferencia y puntos, seguida de los clasificados; o las listas de goleadores y destacados de mayor a menor. Si no hay datos se muestra "(sin información disponible)".

Criterios de aceptación:

1. Dada una liga con resultados registrados, cuando consulto la tabla, entonces veo a los participantes ordenados por puntos, con jugados, ganados, empatados, perdidos y diferencia.
2. Dados dos equipos empatados en puntos, cuando consulto la tabla, entonces queda arriba el que ganó el enfrentamiento directo, aunque el otro tenga mejor diferencia.
3. Dada una competencia de grupos y eliminación directa, cuando consulto un grupo, entonces veo la tabla de ese grupo y los equipos que clasifican de cada grupo.
4. Dada una competencia de fútbol con partidos jugados, cuando consulto los goleadores, entonces veo a los jugadores ordenados por goles en esa competencia. En baloncesto o voleibol se informa que no hay tabla de goleadores.
5. Dada una competencia con partidos jugados, cuando consulto los destacados, entonces veo a los jugadores ordenados por asistencias en fútbol, puntos en baloncesto o aces en voleibol.
6. Dado un filtro de deporte o de categoría sin competencias, o una competencia sin participantes, cuando consulto, entonces se muestra que no hay información disponible.
7. Dado un grupo que no existe en el formato de la competencia, cuando consulto su tabla, entonces se rechaza por dato inválido.

Cambios frente a la Entrega 1: la tabla de goleadores solo existe en fútbol.

INVEST: depende de HU_RF25 a HU_RF27 para tener datos, valiosa para socios y administración, verificable.

Pruebas: `rf2LaTablaOrdenaPorPuntosYDesempataPorEnfrentamientoDirecto`, `rf2EnGruposClasificanLosPrimerosDeCadaGrupo`, `rf2GoleadoresYDestacadosVanDeMayorAMenor`, `rf2LosGoleadoresSoloExistenEnFutbolYElFiltroPuedeDarVacio`, y en `CompetenciaTest`, `laTablaSoloCuentaPartidosJugados`, `elEnfrentamientoDirectoSumaLosPuntosEntreLosDos` y `cadaFormatoDecideQueGruposAcepta`.

### HU_RF12. Programar un partido interno

Código en la Entrega 1: HU_E1.

**Como** entrenador, **quiero** programar un partido interno entre dos equipos del club del mismo deporte y categoría, que cuenten con el mínimo de jugadores habilitados, **para** evaluar a mis jugadores en condiciones de juego.

- **Entradas:** desde la consola del empleado (solo entrenadores), Equipos y partidos internos, Programar un partido interno. El equipo local (elegido de los equipos activos), el visitante (elegido entre los del mismo deporte y categoría), la fecha (AAAA-MM-DD) y el propósito.
- **Salidas:** el partido con su número, o el motivo del rechazo.

Criterios de aceptación:

1. Dados dos equipos activos del mismo deporte y categoría con el mínimo de habilitados, cuando programo un partido para una fecha futura, entonces queda registrado sin resultado.
2. Dados dos equipos de deportes o categorías distintas, o el mismo equipo como local y visitante, cuando intento programar el partido, entonces se rechaza por regla deportiva.
3. Dado un equipo que no alcanza el mínimo de habilitados, contando a los suspendidos como no habilitados, cuando intento programar el partido, entonces se rechaza y no se gasta el número del partido.
4. Dada una fecha que no es posterior a hoy, cuando intento programar el partido, entonces se rechaza por dato inválido.
5. Dado un equipo dado de baja, cuando intento programar el partido, entonces se rechaza por estado.
6. Dado que soy administrador, cuando intento programar un partido interno, entonces se rechaza por permiso.

Cambios frente a la Entrega 1: el partido se programa por día, sin hora, y solo los entrenadores lo programan.

INVEST: depende de HU_RF22 y HU_RF23, valiosa para los entrenadores, pequeña y verificable.

Pruebas: `rf12ElEntrenadorProgramaUnPartidoInterno`, `rf12RechazaMismoEquipoOtroDeporteFechaPasadaYAlAdministrador`, `rf12SinElMinimoDeHabilitadosNoSeProgramaYNoSeGastaElId`.

### HU_RF13. Registrar el resultado y las estadísticas de un partido interno

Código en la Entrega 1: HU_E2.

**Como** entrenador, **quiero** registrar el marcador final y las estadísticas individuales correspondientes al deporte disputado, **para** llevar el historial de desempeño de cada jugador.

- **Entradas:** desde Equipos y partidos internos. Para el resultado: el partido (elegido de los que no tienen resultado) y los dos marcadores, en sets si es voleibol. Para la estadística: el partido, el jugador (elegido de los habilitados) y los datos de su deporte: goles, asistencias, amarillas y rojas en fútbol; puntos y rebotes en baloncesto; aces y bloqueos en voleibol.
- **Salidas:** el partido con su resultado o la estadística registrada, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un partido sin resultado, cuando registro el marcador, entonces queda jugado con ese marcador.
2. Dado un partido que ya tiene resultado, cuando intento registrarlo otra vez, entonces se rechaza por estado.
3. Dado un marcador negativo, cuando lo registro, entonces se rechaza por dato inválido.
4. Dado un partido de baloncesto o voleibol, cuando registro un empate, entonces se rechaza por regla deportiva.
5. Dada una estadística de otro deporte, o un jugador que no es de ninguno de los dos equipos, cuando la registro, entonces se rechaza por regla deportiva.
6. Dado un jugador que ya tiene estadística en ese partido, cuando registro otra, entonces se rechaza por duplicado.
7. Dado un jugador inactivo o suspendido para ese partido, cuando registro su estadística, entonces se rechaza porque no está habilitado.

Cambios frente a la Entrega 1: el resultado y las estadísticas se registran en dos opciones distintas del menú. Cada jugador tiene una sola estadística por partido.

INVEST: depende de HU_RF12, valiosa porque de ella salen el historial y las suspensiones, verificable.

Pruebas: `rf13RegistraElResultadoUnaSolaVez`, `rf13EnBaloncestoNoHayEmpates`, `rf13LaEstadisticaDebeSerDelDeporteYDeUnJugadorDelPartido`, y en `DeporteYCategoriaTest`, `soloAdmiteEstadisticasDeSuDeporte`.

### HU_RF14. Registrar tarjetas y suspender jugadores

Código en la Entrega 1: HU_E3.

**Como** entrenador, **quiero** registrar las tarjetas de un partido interno de fútbol, **para** que el sistema impida alinear a los jugadores suspendidos en el siguiente partido de su equipo.

- **Entradas:** las tarjetas hacen parte de la estadística de fútbol de HU_RF13: amarillas (0 a 2) y rojas (0 o 1).
- **Salidas:** la estadística registrada y, si corresponde, el aviso de la suspensión.

Criterios de aceptación:

1. Dado un partido interno de fútbol, cuando registro una roja a un jugador, entonces queda suspendido para el siguiente partido interno de su equipo.
2. Dado un partido interno de fútbol, cuando registro dos amarillas a un mismo jugador, entonces queda suspendido para el siguiente partido interno de su equipo.
3. Dado un jugador que recibió una sola amarilla, cuando se registra su estadística, entonces no queda suspendido.
4. Dado un jugador suspendido, cuando se registra el siguiente partido interno de su equipo, entonces no aparece entre los habilitados, no se le puede registrar estadística y no cuenta para el mínimo de habilitados.
5. Dado que el equipo ya jugó el partido en que el jugador cumplía la suspensión, cuando se programa el siguiente, entonces el jugador vuelve a estar habilitado.
6. Dado un jugador suspendido en un partido interno, cuando juega su equipo en una competencia, entonces la suspensión no aplica ahí.

Cambios frente a la Entrega 1: las tarjetas no son una opción aparte, sino parte de la estadística de fútbol. Por eso en baloncesto y voleibol no se pueden registrar. La suspensión solo aplica en partidos del mismo tipo (ver el supuesto 4 de la parte 1).

INVEST: depende de HU_RF13, pequeña y verificable.

Pruebas: `rf14UnaRojaSuspendeParaElSiguienteInternoYSeCumpleAlJugarlo`, `rf14DosAmarillasSuspendenYUnEquipoJustoNoAlcanzaElMinimo`, en `EquipoYSuspensionTest`, `laSuspensionDeUnInternoAplicaSoloEnLosSiguientesInternos` y `unaSuspensionCumplidaSigueImpidiendoJugarEsePartido`, y en `CompetenciaTest`, `unaRojaODosAmarillasDanSuMotivoDeSuspension`.

### HU_RF22. Crear un equipo

Código en la Entrega 1: HU_A1.

**Como** administrador, **quiero** crear un equipo indicando su nombre, deporte, categoría y cupo máximo, **para** organizar a los jugadores del club por disciplina y edad.

- **Entradas:** desde la consola del administrador, Equipos y jugadores, Crear un equipo. El deporte y la categoría (elegidos de listas), el nombre y el cupo máximo (1 a 50). Para Asignar el entrenador de un equipo: el equipo y el entrenador, elegidos de listas.
- **Salidas:** el equipo creado sin jugadores, o el entrenador asignado, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que no existe un equipo con ese nombre, cuando creo el equipo, entonces queda registrado sin jugadores.
2. Dado que ya existe un equipo con ese nombre, cuando intento crear otro, entonces se rechaza por duplicado.
3. Dado un cupo máximo menor al mínimo de habilitados del deporte, cuando intento crear el equipo, entonces se rechaza porque nunca podría jugar un partido válido.
4. Dado un equipo y un entrenador del club, cuando asigno el entrenador, entonces queda a cargo del equipo. Si el equipo no existe o el empleado no es entrenador, se rechaza.
5. Dado que no soy administrador, cuando intento crear un equipo, entonces se rechaza por permiso.

Cambios frente a la Entrega 1: el nombre no se puede repetir en todo el club (antes solo dentro del mismo deporte y categoría), porque los menús buscan los equipos por nombre. Se agregó la asignación del entrenador.

INVEST: independiente, valiosa porque sin equipos no hay partidos, pequeña y verificable.

Pruebas: `rf22ElAdministradorCreaUnEquipoYLeAsignaEntrenador`, `rf22RechazaNombreRepetidoCupoBajoYUsuariosSinPermiso`.

### HU_RF23. Inscribir a un socio en un equipo

Código en la Entrega 1: HU_A2.

**Como** administrador, **quiero** inscribir a un socio en un equipo con su posición y número de camiseta, **para** que pueda ser alineado en los partidos de ese equipo.

- **Entradas:** desde Equipos y jugadores, Inscribir a un socio en un equipo. El equipo (elegido de los activos), el id del socio (por ejemplo S1), el número de camiseta (1 a 99) y la posición.
- **Salidas:** el jugador inscrito con su número y posición, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un socio con la edad de la categoría, sin otro equipo del mismo deporte, y un equipo con cupo, cuando lo inscribo con un número libre, entonces queda inscrito y el equipo aparece en su información deportiva.
2. Dado un socio con una edad fuera de la categoría, cuando intento inscribirlo, entonces se rechaza por edad. La edad que cuenta es la del 1 de enero del año.
3. Dado un socio que ya juega en otro equipo del mismo deporte, cuando intento inscribirlo, entonces se rechaza.
4. Dado un número que ya usa otro jugador activo del equipo, cuando intento asignarlo, entonces se rechaza por duplicado.
5. Dado un equipo sin cupo, cuando intento inscribir a un jugador más, entonces se rechaza por cupo lleno.
6. Dado un número de camiseta menor a 1 o un equipo dado de baja, cuando intento inscribir, entonces se rechaza.

Cambios frente a la Entrega 1: la edad se cuenta al 1 de enero, y el cupo y los números solo cuentan a los jugadores activos.

INVEST: depende de HU_RF22, valiosa, pequeña y verificable.

Pruebas: `rf23InscribeAUnSocioConNumeroYPosicion`, `rf23RechazaNumeroUsadoEquipoLlenoYSegundoEquipoDelMismoDeporte`, `rf23LaEdadQueCuentaEsLaDelPrimeroDeEnero`, en `DeporteYCategoriaTest`, `laEdadSeCuentaAlPrimeroDeEnero` y `mayoresNoTieneEdadMaxima`, y en `EquipoYSuspensionTest`, `elCupoYLosNumerosSoloCuentanJugadoresActivos`.

### HU_RF24. Dar de baja equipos y retirar jugadores

Código en la Entrega 1: HU_A3.

**Como** administrador, **quiero** dar de baja equipos y retirar jugadores, **para** mantener actualizada la estructura activa del club sin afectar partidos pendientes.

- **Entradas:** desde Equipos y jugadores. Para Retirar a un jugador de un equipo: el equipo, el jugador (elegidos de listas) y una confirmación. Para Dar de baja un equipo: el equipo y una confirmación.
- **Salidas:** confirmación de que el número quedó libre o de que el equipo quedó dado de baja, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un equipo activo sin partidos por jugar, cuando lo doy de baja, entonces queda inactivo y sus jugadores también.
2. Dado un equipo con algún partido sin jugar, interno o de competencia, cuando intento darlo de baja, entonces se rechaza porque tiene compromisos pendientes.
3. Dado un jugador de un equipo, cuando lo retiro, entonces queda inactivo y su número queda libre para otro socio.
4. Dado un equipo ya dado de baja, cuando intento darlo de baja o inscribirle jugadores, entonces se rechaza por estado.

Cambios frente a la Entrega 1: retirar a un jugador no borra su historial; sigue apareciendo en su información deportiva como retirado. Los compromisos pendientes son los partidos sin jugar, internos o de competencia.

INVEST: depende de HU_RF22 y HU_RF23, pequeña y verificable.

Pruebas: `rf24AlRetirarAUnJugadorSuNumeroQuedaLibre`, `rf24NoSeDaDeBajaUnEquipoConPartidosPendientes`, y en `EquipoYSuspensionTest`, `alDarDeBajaElEquipoSusJugadoresQuedanInactivos`.

### HU_RF25. Crear una competencia y registrar a sus rivales

No tenía historia en la Entrega 1.

**Como** administrador, **quiero** registrar una competencia externa con su deporte, categoría, formato y reglas, y agregar a los rivales de otras instituciones, **para** llevar en el sistema las competencias en que participa el club.

- **Entradas:** desde la consola del administrador, Competencias externas, Crear una competencia. El deporte y la categoría (elegidos de listas), el nombre, la entidad organizadora, la temporada (año), la nómina mínima, las amarillas acumuladas que suspenden y el formato: liga (con o sin ida y vuelta) o grupos y eliminación (con el número de grupos y los clasificados por grupo). Para Agregar un rival externo: la competencia, el nombre, la institución y, si es por grupos, el grupo.
- **Salidas:** la competencia creada o el rival agregado, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que no existe una competencia con ese nombre en esa temporada, cuando la creo, entonces queda registrada. El mismo nombre en otra temporada también se acepta.
2. Dado que ya existe una competencia con ese nombre y esa temporada, cuando intento crearla, entonces se rechaza por duplicado.
3. Dada una temporada, una nómina mínima o unas amarillas que no son positivas, cuando intento crearla, entonces se rechaza por dato inválido.
4. Dado un rival con un nombre que ya participa en la competencia, cuando lo agrego, entonces se rechaza por duplicado.
5. Dada una liga, cuando agrego un rival con grupo, o dada una competencia por grupos, cuando lo agrego en un grupo que no existe, entonces se rechaza por dato inválido.
6. Dado que no soy administrador, cuando intento crear una competencia, entonces se rechaza por permiso.

INVEST: independiente, valiosa porque sin competencia no hay inscripciones ni partidos oficiales, pequeña y verificable.

Pruebas: `rf25ElAdministradorCreaUnaCompetenciaPorTemporada`, `rf25RechazaValoresInvalidosYUsuariosSinPermiso`, `rf25ElGrupoDelRivalDependeDelFormato`, y en `CompetenciaTest`, `cadaFormatoDecideQueGruposAcepta`.

### HU_RF26. Inscribir un equipo del club en una competencia

Código en la Entrega 1: HU_A4.

**Como** administrador, **quiero** inscribir un equipo del club en una competencia con su nómina de jugadores, **para** que pueda disputar los partidos oficiales del circuito.

- **Entradas:** desde Competencias externas, Inscribir un equipo del club. La competencia, el equipo (elegidos de listas), los números de camiseta de la nómina separados por comas y, si es por grupos, el grupo.
- **Salidas:** la inscripción con el número de jugadores de la nómina, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un equipo del deporte y la categoría de la competencia, cuando lo inscribo con una nómina de al menos la nómina mínima de habilitados, entonces queda inscrito y solo esos jugadores pueden jugar sus partidos oficiales.
2. Dado un equipo de otra categoría o de otro deporte, cuando intento inscribirlo, entonces se rechaza.
3. Dada una nómina en la que, sin contar a los suspendidos, quedan menos habilitados que la nómina mínima, cuando intento inscribirla, entonces se rechaza e indica cuántos faltan.
4. Dada una nómina con un número que no es de ningún jugador del equipo, cuando intento inscribirla, entonces se rechaza.
5. Dada una nómina con un número repetido, cuando intento inscribirla, entonces se rechaza por dato inválido.
6. Dado un equipo que ya está inscrito en la competencia, cuando intento inscribirlo otra vez, entonces se rechaza por duplicado.

Cambios frente a la Entrega 1: se agregaron los criterios 5 y 6.

INVEST: depende de HU_RF23 y HU_RF25, valiosa, pequeña y verificable.

Pruebas: `rf26InscribeUnEquipoConSuNomina`, `rf26LaNominaDebeTenerElMinimoSinRepetidosNiDesconocidos`, `rf26ElEquipoDebeSerDelDeporteYLaCategoriaYNoRepetirse`.

### HU_RF27. Programar y registrar partidos oficiales

Código en la Entrega 1: HU_A5.

**Como** administrador, **quiero** programar los partidos oficiales de una competencia y registrar sus resultados y estadísticas, **para** que se actualicen la tabla de posiciones según las reglas del deporte, las estadísticas acumuladas y las sanciones de los jugadores por acumulación de amonestaciones.

- **Entradas:** desde Competencias externas. Para Programar un partido: la competencia, el local y el visitante (elegidos entre sus participantes), la fecha, el escenario y la fase. Para Registrar el resultado: el partido (elegido de los que no tienen resultado) y los dos marcadores. Para Registrar la estadística de un jugador: el partido, el jugador (elegido de los habilitados de la nómina) y los datos de su deporte.
- **Salidas:** el partido programado o con su resultado, o la estadística registrada con el aviso de suspensión si corresponde, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un partido de liga de fútbol, cuando registro la victoria de un equipo, entonces el ganador suma 3 puntos y el perdedor 0; si registro un empate, cada uno suma 1.
2. Dado un partido de baloncesto o voleibol, cuando registro un empate, entonces se rechaza porque en esos deportes no hay empates.
3. Dado un partido entre dos rivales de otras instituciones, cuando registro su resultado, entonces la tabla de los dos se actualiza.
4. Dado un jugador que no está en la nómina de su equipo, cuando intento registrarle una estadística, entonces se rechaza porque no está habilitado.
5. Dado un jugador que alcanza las amarillas acumuladas que fija la competencia, cuando registro su estadística, entonces queda suspendido para el siguiente partido de esa competencia. Una roja o dos amarillas en el mismo partido también lo suspenden.
6. Dado un marcador negativo o un partido que ya tiene resultado, cuando lo registro, entonces se rechaza.
7. Dado un jugador suspendido en la competencia, cuando juega el siguiente partido de esa competencia, entonces no está habilitado; en un partido interno sí puede jugar.
8. Dado el mismo participante como local y visitante, o uno que no está en la competencia, cuando programo el partido, entonces se rechaza.

Cambios frente a la Entrega 1: la historia también cubre programar los partidos. La suspensión se decide al registrar la estadística del jugador, no el resultado, y solo aplica en esa competencia.

INVEST: depende de HU_RF25 y HU_RF26, es la más grande de esta parte; se mantiene junta porque resultado, estadísticas y sanciones se registran sobre el mismo partido. Verificable con los criterios.

Pruebas: `rf27ProgramaYRegistraPartidosInclusoEntreRivalesExternos`, `rf27SoloJueganLosDeLaNominaConEstadisticaDelDeporte`, `rf27LasAmarillasAcumuladasSuspendenSoloEnEsaCompetencia`, en `CompetenciaTest`, `laTablaSoloCuentaPartidosJugados` y `lasAmarillasSeCuentanDesdeLaUltimaSancion`, y en `DeporteYCategoriaTest`, `cadaDeporteDaSusPropiosPuntos`.

## Parte 2. Tienda, cafetería, personal y reportes

Estas historias son las del banco de la Entrega 1 para la tienda, la cafetería, el personal y los reportes (RF3 a RF5, RF17 a RF21 y RF28 a RF35). No se reescriben: aquí se indica, para cada una, desde dónde se usa en la consola, sus entradas y salidas, sus criterios de aceptación, los cambios frente a la Entrega 1 y la prueba JUnit que la verifica (en `test/`). Las pruebas de integración están en `GestorTiendaTest`, `GestorPersonalTest` y `GestorReportesTest` y tienen el requerimiento al inicio del nombre (por ejemplo `rf33...`). La parte de HU_T10 que toca la tienda, usar el bono de torneo en una compra, está en la parte 3, con el resto de esa historia.

### Supuestos de la parte 2

El análisis deja abiertos algunos puntos. Estas son las decisiones del grupo para esta parte:

1. **Descuento de empleado.** Todo código de descuento pertenece a un empleado. Da 15 % si lo usa su dueño y 8 % si lo usa cualquier otro cliente. Un código que no existe o está inactivo se rechaza.
2. **No acumulable.** Una compra lleva como máximo un descuento: un código o un bono de torneo, no los dos. Los puntos de fidelidad no cuentan como descuento y se pueden usar con cualquiera de ellos.
3. **Puntos.** Un punto vale $1. Se gana el 2 % del total pagado, redondeado hacia abajo. No se pueden redimir más puntos que el saldo ni más que el valor de la compra.
4. **Impuestos y propina.** La tienda cobra IVA del 19 % y la cafetería impuesto al consumo del 8 %. La propina se calcula sobre el subtotal; la consola sugiere el 10 % y el cliente la puede cambiar entre 0 y 100 %.
5. **Bebidas calientes.** No se venden a quien en ese momento tenga una reserva vigente, un partido de práctica o una sesión de entrenamiento en una cancha de pádel o una mesa de tenis de mesa techada.
6. **Fecha de las ventas.** Una venta toma la fecha del club y la hora actual, para que los reportes no dependan del día en que se ejecute el programa.
7. **Horario de atención.** De lunes a sábado, de 6:00 a 20:00, en bloques de una hora. Ningún turno ni solicitud de cambio puede quedar fuera de ese horario.
8. **Turnos.** Se repiten cada semana. Al publicar una semana se reemplazan todos los turnos anteriores, y cada bloque debe tener al menos 2 entrenadores y 1 fisioterapeuta.
9. **Ranking del reporte deportivo.** Es el ELO actual por disciplina, de mayor a menor, con los partidos de práctica jugados y ganados en el periodo. El sistema no guarda el historial del ELO, así que no se muestra cuánto cambió.

### HU_RF3. Comprar en la tienda

Código en la Entrega 1: HU_S2.

**Como** socio, **quiero** comprar cuantos artículos desee en una sola transacción, **para** llevarme lo que necesito y acumular puntos.

- **Entradas:** desde la consola del socio, Tienda y cafetería, Comprar en tienda. Los productos (elegidos de la lista de artículos) con su cantidad de 1 a 100, un código de descuento opcional, los puntos a redimir (de 0 al saldo) y, si tiene, un bono de torneo.
- **Salidas:** la compra con subtotal, IVA, descuento, puntos redimidos, total y puntos ganados, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que hay existencias, cuando compro 2 camisetas, entonces pago el subtotal más el 19 % de IVA, el inventario de la tienda baja en 2 y sumo el 2 % del total en puntos.
2. Dado que uso el código de un empleado, cuando compro, entonces recibo el 8 % de descuento.
3. Dado que el código no existe o está inactivo, cuando compro, entonces se rechaza y no cambia nada.
4. Dado que pido más unidades de las que hay, o un producto de la cafetería, cuando compro, entonces se rechaza y no cambia nada.
5. Dado que soy administrador, cuando intento comprar, entonces se rechaza por permiso.

Cambios frente a la Entrega 1: la venta toma la fecha del club y se puede pagar con un bono de torneo.

INVEST: independiente, valiosa porque es la compra básica del club, pequeña y verificable.

Pruebas: `rf3CompraEnLaTiendaConIvaYPuntos`, `rf3LaVentaQuedaConLaFechaDelClub`, `rf3RechazaSinExistenciasYNoCambiaNada`, `rf3RechazaProductosDeCafeteriaCarritoVacioYAlAdministrador`, `rf3ElSocioUsaUnCodigoDeEmpleadoYRecibeOchoPorCiento`, `rf3RechazaCodigoInexistenteEInactivo`, y en `VentaTest`, `laTiendaCobraIvaDelDiecinuevePorCiento`.

### HU_RF4. Consumir en la cafetería

Código en la Entrega 1: HU_S3.

**Como** socio, **quiero** comprar bebidas y snacks conociendo sus alérgenos, **para** consumir de forma segura.

- **Entradas:** desde la consola del socio, Tienda y cafetería, Comprar en cafetería. Los productos con su cantidad, la confirmación después de ver los alérgenos (s/n), un código o un bono opcional, los puntos y la propina (10 % sugerido o un valor de 0 a 100).
- **Salidas:** el aviso de alérgenos del pedido y la compra con subtotal, impuesto al consumo, descuento, propina, total y puntos, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que pido una limonada con la propina sugerida, cuando compro, entonces pago el subtotal más el 8 % de impuesto al consumo más el 10 % de propina.
2. Dado que pido snacks con alérgenos, cuando termino el pedido, entonces se me informan todos, sin repetir, antes de confirmar.
3. Dado que tengo una reserva vigente en una cancha de pádel, cuando pido una bebida caliente, entonces se rechaza; a otro socio sí se le vende.
4. Dado que la propina es negativa o pido un artículo de la tienda, cuando compro, entonces se rechaza.

Cambios frente a la Entrega 1: los alérgenos se informan juntos para todo el pedido y se puede pagar con un bono de torneo.

INVEST: independiente de la tienda, valiosa por la regla de alérgenos y la de bebidas calientes, pequeña y verificable.

Pruebas: `rf4CompraEnLaCafeteriaConImpuestoAlConsumoYPropina`, `rf4InformaLosAlergenosDeLosSnacksSinRepetir`, `rf4NoVendeBebidaCalienteAQuienEstaEnPadel`, `rf4RechazaPropinaNegativaYArticulosDeTienda`, y en `VentaTest`, `laCafeteriaCobraImpuestoAlConsumoYPropinaSobreElSubtotal`.

### HU_RF5. Redimir puntos de fidelidad

Código en la Entrega 1: HU_S4.

**Como** socio, **quiero** usar mis puntos como descuento, **para** pagar menos en mis compras.

- **Entradas:** en cualquiera de las dos compras, los puntos a redimir, de 0 al saldo actual.
- **Salidas:** el total con los puntos descontados y el nuevo saldo, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que tengo 9.000 puntos, cuando redimo 5.000 en una compra, entonces se descuentan $5.000 y mi saldo queda en 4.000 más los puntos que gane.
2. Dado que pido más puntos que mi saldo, cuando compro, entonces se rechaza y el saldo no cambia.
3. Dado que pido más puntos que el valor de la compra, cuando compro, entonces se rechaza y el saldo no cambia.

Cambios frente a la Entrega 1: se agregó el criterio 3, porque antes el total podía quedar negativo.

INVEST: depende de HU_RF3 o HU_RF4, pequeña y verificable.

Pruebas: `rf5RedimePuntosComoPesos`, `rf5RechazaMasPuntosQueElSaldo`, `rf5RechazaMasPuntosQueElValorDeLaCompra`, y en `VentaTest`, `losPuntosRedimidosSeRestanComoPesos`.

### HU_RF17. Comprar con código de descuento de empleado

Código en la Entrega 1: HU_E4.

**Como** entrenador, **quiero** comprar con un código de empleado, **para** recibir el descuento del personal.

- **Entradas:** desde la consola del empleado, Tienda y cafetería. Las mismas de la compra, con el número del código.
- **Salidas:** la compra con el descuento aplicado y el 2 % en puntos, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que uso mi propio código, cuando compro, entonces recibo el 15 % de descuento.
2. Dado que uso el código de otro empleado, cuando compro, entonces recibo el 8 %.
3. Dado que el código no existe o está inactivo, cuando compro, entonces se rechaza.

Cambios frente a la Entrega 1: el menú del empleado hereda del menú del socio porque la compra es igual; el porcentaje lo decide `CodigoDescuento`.

INVEST: depende de HU_RF3, pequeña y verificable.

Pruebas: `rf17ElEmpleadoConSuCodigoRecibeQuincePorCientoYConElAjenoOcho`, `rf3RechazaCodigoInexistenteEInactivo`, y en `VentaTest`, `elCodigoDaQuincePorCientoASuDuenoYOchoAlResto`.

### HU_RF18. Solicitar un cambio de turno

Código en la Entrega 1: HU_E7.

**Como** entrenador, **quiero** pedir que me cambien un turno a otro día u hora, **para** ajustar mi horario cuando lo necesite.

- **Entradas:** desde la consola del empleado, Turnos y sugerencias, Solicitar un cambio de turno. Uno de mis turnos (elegido de la lista), el nuevo día y las nuevas horas de inicio y fin (HH:MM).
- **Salidas:** la solicitud en estado pendiente, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que el turno es mío y el nuevo horario está dentro del horario de atención, cuando lo pido, entonces la solicitud queda pendiente y el turno no cambia todavía.
2. Dado que el turno es de otro empleado, cuando lo pido, entonces se rechaza por permiso.
3. Dado que propongo un domingo o un horario fuera de 6:00 a 20:00, cuando lo pido, entonces se rechaza por dato inválido.

INVEST: independiente, valiosa para el personal, pequeña y verificable.

Pruebas: `rf18ElEntrenadorPideCambiarSuTurnoYQuedaPendiente`, `rf18RechazaTurnoAjenoDomingoYHorarioFueraDeAtencion`.

### HU_RF19. Solicitar un cambio de turno y consultar su estado

Código en la Entrega 1: HU_F1.

**Como** fisioterapeuta, **quiero** pedir un cambio de turno y ver si fue aprobado o rechazado, **para** organizar mi semana.

- **Entradas:** las mismas de HU_RF18. Para consultar, ninguna (Ver mis solicitudes de cambio).
- **Salidas:** la lista de mis solicitudes con su estado: pendiente, aprobada o rechazada.

Criterios de aceptación:

1. Dado que ya tengo una solicitud pendiente sobre un turno, cuando pido otra sobre el mismo turno, entonces se rechaza por estado.
2. Dado que el administrador resolvió mi solicitud, cuando consulto mis solicitudes, entonces veo si fue aprobada o rechazada.

INVEST: depende de HU_RF18 y de HU_RF33, pequeña y verificable.

Pruebas: `rf19NoAceptaUnaSegundaSolicitudPendienteYMuestraElEstado`, y en `TurnoTest`, `unTurnoConSolicitudPendienteNoAdmiteOtraHastaQueSeResuelva`.

### HU_RF20. Comprar con descuento de empleado

Código en la Entrega 1: HU_F2.

**Como** fisioterapeuta, **quiero** comprar con mi código, **para** recibir el 15 % de descuento.

- **Entradas:** las mismas de HU_RF17.
- **Salidas:** la compra con el descuento, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que uso mi código en la cafetería, cuando compro, entonces recibo el 15 %.
2. Dado que el código no es de ningún empleado, cuando compro, entonces se rechaza.

INVEST: depende de HU_RF4, pequeña y verificable.

Pruebas: `rf20ElFisioterapeutaCompraEnLaCafeteriaConCodigoDeEmpleado`.

### HU_RF21. Sugerir productos

Código en la Entrega 1: HU_F3.

**Como** empleado, **quiero** sugerir productos para la tienda o la cafetería, **para** mejorar la oferta del club.

- **Entradas:** desde la consola del empleado, Turnos y sugerencias, Sugerir un producto. Nombre, tipo (artículo, bebida o snack, elegido de una lista) y descripción.
- **Salidas:** la sugerencia pendiente de aprobación, o el motivo del rechazo. En Ver mis sugerencias, su estado y el motivo si fue rechazada.

Criterios de aceptación:

1. Dado que escribo nombre y descripción, cuando envío la sugerencia, entonces queda pendiente.
2. Dado que falta el nombre o la descripción, cuando la envío, entonces se rechaza por dato inválido.
3. Dado que soy socio, cuando intento sugerir, entonces se rechaza por permiso.

Cambios frente a la Entrega 1: el tipo se elige de una lista y el empleado puede ver el motivo de un rechazo.

INVEST: independiente, valiosa para el personal, pequeña y verificable.

Pruebas: `rf21ElEmpleadoSugiereUnProductoQueQuedaPendiente`, `rf21RechazaSugerenciaIncompletaYDeUnSocio`.

### HU_RF28. Consultar el historial global

Código en la Entrega 1: HU_A8.

**Como** administrador, **quiero** ver todas las reservas, partidos y sesiones con filtros, **para** auditar la actividad del club.

- **Entradas:** desde la consola del administrador, Reportes y mensualidades, Consultar el historial global. Filtros opcionales: rango de fechas, socio (por su id), entrenador, equipo, deporte y categoría.
- **Salidas:** cuatro listas (partidos internos y de competencia, partidos de práctica, reservas y sesiones de entrenamiento), con un aviso en las que estén vacías.

Criterios de aceptación:

1. Dado que filtro por un socio, cuando consulto, entonces solo veo sus reservas, partidos y sesiones.
2. Dado que filtro por un rango de fechas sin actividad, cuando consulto, entonces las listas salen vacías.
3. Dado que no soy administrador, cuando consulto, entonces se rechaza por permiso.

INVEST: independiente, valiosa para la administración, verificable.

Pruebas: `rf28ElHistorialFiltraPorSocioYPorFechas`, `rf28SoloElAdministradorConsultaElHistorial`.

### HU_RF29. Consultar el inventario

Código en la Entrega 1: HU_A6.

**Como** administrador, **quiero** ver los productos y cantidades de cada ubicación, **para** controlar el inventario.

- **Entradas:** desde la consola del administrador, Inventario de tienda y cafetería, Consultar el inventario. La ubicación, elegida de la lista.
- **Salidas:** cada producto de la ubicación con sus unidades, o un aviso si no hay productos.

Criterios de aceptación:

1. Dado que la ubicación existe, cuando la consulto, entonces veo sus productos y cantidades.
2. Dado que la ubicación no existe, cuando la consulto, entonces se rechaza.
3. Dado que no soy administrador, cuando consulto, entonces se rechaza por permiso.

INVEST: independiente, pequeña y verificable.

Pruebas: `rf29ConsultaElInventarioDeUnaUbicacion`, `rf29RechazaUbicacionInexistenteYUsuariosSinPermiso`.

### HU_RF30. Mover productos entre ubicaciones

Código en la Entrega 1: HU_A7.

**Como** administrador, **quiero** trasladar productos de una ubicación a otra, **para** abastecer la tienda y la cafetería desde la bodega.

- **Entradas:** desde Inventario de tienda y cafetería, Mover productos. El producto, el origen y el destino (elegidos de listas) y la cantidad.
- **Salidas:** confirmación de lo movido, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que hay suficientes unidades en el origen, cuando muevo, entonces bajan en el origen y suben en el destino; si el destino no tenía el producto, se crea su existencia.
2. Dado que pido mover más de lo disponible, cuando muevo, entonces se rechaza y nada cambia.

INVEST: depende de HU_RF29 para verificar, pequeña y verificable.

Pruebas: `rf30MueveProductosEntreUbicaciones`, `rf30RechazaMoverMasDeLoDisponible`.

### HU_RF31. Reabastecer y registrar productos

Código en la Entrega 1: HU_A6c.

**Como** administrador, **quiero** aumentar las existencias y registrar productos nuevos, **para** mantener la oferta de la tienda y la cafetería.

- **Entradas:** desde Inventario de tienda y cafetería. Para reabastecer: producto, ubicación y unidades. Para registrar: tipo, nombre, precio, los datos de su tipo (categoría; temperatura y tamaño; o alérgenos separados por comas), ubicación y cantidad inicial.
- **Salidas:** confirmación con el producto y sus unidades, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que el producto existe, cuando lo reabastezco, entonces suben sus unidades en esa ubicación, aunque antes no estuviera en ella.
2. Dado un producto nuevo con precio y cantidad válidos, cuando lo registro, entonces queda en la ubicación con su cantidad inicial.
3. Dado que el precio es 0, la cantidad inicial es negativa o el nombre ya existe, cuando lo registro, entonces se rechaza.

Cambios frente a la Entrega 1: reabastecer crea la existencia si el producto no estaba en esa ubicación.

INVEST: independiente, pequeña y verificable.

Pruebas: `rf31ReabasteceYRegistraUnProductoNuevo`, `rf31ReabasteceUnProductoEnUnaUbicacionDondeNoTeniaExistencias`, `rf31RechazaPrecioCeroCantidadNegativaYNombreRepetido`.

### HU_RF32. Resolver sugerencias de productos

Código en la Entrega 1: HU_A9.

**Como** administrador, **quiero** aprobar o rechazar las sugerencias del personal, **para** decidir qué productos incorporar.

- **Entradas:** desde Turnos y sugerencias del personal, Resolver sugerencias pendientes. La sugerencia (elegida de la lista de pendientes), si se aprueba (s/n) y el motivo si se rechaza.
- **Salidas:** la sugerencia con su nuevo estado; si se aprueba, el aviso de que ya se puede registrar el producto.

Criterios de aceptación:

1. Dado que apruebo una sugerencia, cuando la resuelvo, entonces queda aprobada.
2. Dado que rechazo una sugerencia con motivo, cuando la resuelvo, entonces queda rechazada y el empleado ve el motivo.
3. Dado que rechazo sin motivo, cuando la resuelvo, entonces se rechaza la acción.
4. Dado que la sugerencia ya fue resuelta, cuando intento resolverla otra vez, entonces se rechaza por estado.

INVEST: depende de HU_RF21, pequeña y verificable.

Pruebas: `rf32ApruebaUnaYRechazaOtraConMotivo`, `rf32RechazaSinMotivoDosVecesYSinPermiso`.

### HU_RF33. Gestionar turnos y solicitudes de cambio

Código en la Entrega 1: HU_A10.

**Como** administrador, **quiero** publicar los turnos de la semana y resolver las solicitudes de cambio sin dejar bloques sin cobertura, **para** que el club siempre tenga personal.

- **Entradas:** desde Turnos y sugerencias del personal. Para publicar la semana o agregar un turno: empleado, día, hora de inicio y hora de fin de cada turno. Para resolver una solicitud: cuál (de la lista de pendientes) y si se aprueba (s/n).
- **Salidas:** los turnos publicados o la solicitud resuelta, o el motivo del rechazo (el bloque sin cobertura, el solapamiento o el turno fuera de horario).

Criterios de aceptación:

1. Dado que en cada bloque hay al menos 2 entrenadores y 1 fisioterapeuta, cuando publico la semana, entonces se registran los turnos y cada empleado queda solo con los nuevos.
2. Dado que algún bloque queda sin el mínimo, cuando publico, entonces se rechaza por cobertura.
3. Dado que un empleado tiene dos turnos que se cruzan, cuando publico, entonces se rechaza por conflicto de horario.
4. Dado que un turno queda fuera del horario de atención, cuando publico, entonces se rechaza por dato inválido.
5. Dado que aprobar una solicitud mantiene la cobertura, cuando la apruebo, entonces el turno cambia; si deja un bloque sin el mínimo, se rechaza y el turno no cambia.

Cambios frente a la Entrega 1: se agregó el criterio 4 y, al publicar, se quitan los turnos anteriores de cada empleado.

INVEST: independiente, valiosa porque de ella dependen las solicitudes y la regla de torneos para empleados de turno, verificable.

Pruebas: `rf33PublicaUnaSemanaConCoberturaMinima`, `rf33AlPublicarDeNuevoCadaEmpleadoQuedaSoloConSusTurnosNuevos`, `rf33RechazaSemanaSinFisioterapeutaYTurnosQueSeSolapan`, `rf33RechazaTurnosFueraDelHorarioDeAtencion`, `rf33ApruebaUnCambioQueMantieneLaCobertura`, `rf33RechazaUnCambioQueDejaUnBloqueSinCobertura`, y en `TurnoTest`, `dosTurnosSeSolapanSoloSiSonElMismoDiaYSeCruzan`, `elTurnoCubreDesdeElInicioHastaAntesDelFin` y `elHorarioDeAtencionEsDeLunesASabadoDeSeisAVeinte`.

### HU_RF34. Generar reportes

Código en la Entrega 1: HU_A11.

**Como** administrador, **quiero** reportes financieros y deportivos por periodo, **para** tomar decisiones sobre el club.

- **Entradas:** desde Reportes y mensualidades, Generar reporte financiero o deportivo. El periodo (diario, semanal o mensual) y la fecha de inicio (AAAA-MM-DD).
- **Salidas:** el financiero con neto, impuesto y total de la tienda y de la cafetería y el total de mensualidades, con un aviso si no hubo movimientos; el deportivo con los resultados de los partidos, la asistencia a entrenamientos y el ranking ELO por disciplina con los partidos jugados y ganados en el periodo.

Criterios de aceptación:

1. Dado un mes con una venta en la tienda y un pago de mensualidad, cuando genero el reporte financiero mensual, entonces suma solo lo de ese mes por rubro.
2. Dado un periodo sin datos, cuando genero el reporte, entonces todo sale en cero.
3. Dado que hay socios inscritos en una disciplina, cuando genero el reporte deportivo, entonces aparecen ordenados por ELO con su actividad del periodo.
4. Dado que no soy administrador, cuando genero un reporte, entonces se rechaza por permiso.

Cambios frente a la Entrega 1: el ranking se ordena de mayor a menor y muestra los partidos del periodo (ver el supuesto 9 de la parte 2).

INVEST: depende de que haya ventas, pagos y partidos, valiosa para la administración, verificable.

Pruebas: `rf34ReporteFinancieroSumaVentasYMensualidadesDelPeriodo`, `rf34PeriodoSinDatosDaReporteEnCeroYSoloLoGeneraElAdministrador`, `rf34ReporteDeportivoIncluyeLaAsistenciaYSoloLoGeneraElAdministrador`, `rf34ReporteDeportivoMuestraElRankingConLosPartidosDelPeriodo`.

### HU_RF35. Registrar mensualidades

Código en la Entrega 1: HU_A12.

**Como** administrador, **quiero** registrar el pago mensual de un socio, **para** mantener su vigencia en el club.

- **Entradas:** desde Reportes y mensualidades, Registrar el pago de una mensualidad. El id del socio, el año, el mes y el valor pagado.
- **Salidas:** el periodo pagado con el socio al día, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que el socio estaba en mora y no ha pagado ese mes, cuando registro el pago, entonces queda al día.
2. Dado que el socio ya pagó ese mes, cuando registro otro pago del mismo mes, entonces se rechaza por duplicado.
3. Dado que el valor es 0 o el socio no existe, cuando registro, entonces se rechaza.
4. Dado que no soy administrador, cuando registro, entonces se rechaza por permiso.

INVEST: independiente, pequeña y verificable.

Pruebas: `rf35RegistraElPagoYElSocioQuedaAlDia`, `rf35RechazaPagoRepetidoValorCeroSocioInexistenteYSinPermiso`.

## Parte 3. Torneos internos

Estas historias cubren los requerimientos nuevos de torneos recreativos y competitivos. Cada una indica sus entradas y salidas, sus criterios de aceptación y la prueba JUnit que la verifica (en `test/`). Las pruebas de integración están en `GestorTorneosTest` y tienen el prefijo de la historia en el nombre (por ejemplo `hut3...`).

### Supuestos de la parte 3

El enunciado deja abiertos algunos puntos. Estas son las decisiones del grupo:

1. **Premio en efectivo.** El premio de un torneo competitivo es el 70 % de lo recaudado. El resto cubre los gastos del club.
2. **Tarifa.** En los torneos individuales cada jugador paga la tarifa, salvo los empleados. En los de conjunto el equipo paga una sola tarifa.
3. **Premio y empleados.** El premio en efectivo se reparte en partes iguales entre los jugadores del ganador. La parte de un empleado no se entrega.
4. **Bono recreativo.** Es un porcentaje de descuento que fija el administrador al crear el torneo. Lo recibe cada jugador del ganador, se usa una vez y no se suma con otros descuentos. Un participante externo puede ganarlo, pero solo socios y empleados compran en el club.
5. **Ranking para los cupos preferenciales.** En disciplinas individuales es el ELO de la academia, comparado con los socios del mismo nivel. En deportes de conjunto son los puntos sumados en las competencias externas del mismo deporte, comparados con los equipos de la misma categoría. Un equipo sin puntos no es preferente. Es preferente quien tiene a menos de N rivales por encima, donde N son los cupos reservados.
6. **Día del torneo.** Cada torneo se juega en un día. Un empleado está "de turno" si tiene un turno ese día de la semana, porque los turnos se repiten cada semana.
7. **Participantes externos.** El administrador registra a los estudiantes y externos con login y contraseña. Después entran por la consola del socio, donde solo ven el menú de torneos.
8. **Nivel en individuales.** Si un socio está inscrito en la academia de la disciplina, su nivel debe ser el del torneo. Empleados, externos y socios que no están en la academia no tienen nivel registrado y se aceptan.
9. **Equipo completo.** Un equipo está completo si tiene al menos el mínimo de jugadores activos que pide su deporte.
10. **Inicio y estados.** El administrador inicia el torneo cuando se cierran las inscripciones. Un torneo pasa por "Inscripciones abiertas", "En curso" y "Finalizado", en ese orden.
11. **Formatos.** Un torneo es "Todos contra todos" (una tabla por puntos) o "Eliminación directa" (cuadro donde el que pierde sale). En eliminación no hay empates, el mejor sembrado juega contra el peor y, si quedan impares, el mejor sembrado pasa directo.

### HU_T1. Crear un torneo

**Como** administrador, **quiero** crear un torneo indicando su deporte o disciplina, su categoría o nivel, sus cupos, su tipo y su formato, **para** organizar actividades que animen a los socios a participar.

- **Entradas:** nombre, día del torneo (AAAA-MM-DD), número de cupos, deporte y categoría (conjunto) o disciplina, nivel y modalidad (individual), tipo (recreativo con porcentaje de bono, o competitivo con tarifa) y formato.
- **Salidas:** el torneo creado con estado "Inscripciones abiertas" y sus cupos preferenciales calculados, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que soy administrador, cuando creo un torneo de tenis intermedio con 8 cupos, entonces queda en "Inscripciones abiertas" con 2 cupos preferenciales.
2. Dado que ya existe un torneo con ese nombre, cuando intento crear otro igual, entonces se rechaza por nombre repetido.
3. Dado que la fecha no es posterior a hoy, o hay menos de 2 cupos, cuando intento crearlo, entonces se rechaza por dato inválido.
4. Dado que el torneo es competitivo con tarifa 0, o recreativo con un bono fuera de 1 a 100 %, cuando intento crearlo, entonces se rechaza por dato inválido.
5. Dado que soy socio, cuando intento crear un torneo, entonces se rechaza por permiso.

INVEST: independiente de las demás, negociable en los valores de tipo y formato, valiosa porque sin torneo no hay nada más, estimable y pequeña, y verificable con los criterios anteriores.

Pruebas: `hut1ElAdministradorCreaUnTorneoConInscripcionesAbiertas`, `hut1SoloElAdministradorCreaTorneos`, `hut1RechazaNombreRepetidoFechaPasadaYValoresInvalidos`, y en `TorneoTest`, `reservaElVeintePorCientoRedondeadoHaciaArriba`.

### HU_T2. Registrar un estudiante o participante externo

**Como** administrador, **quiero** registrar a estudiantes de la universidad y a personas externas con un usuario propio, **para** que también puedan inscribirse en los torneos internos.

- **Entradas:** nombre, apellidos, documento, si es estudiante, login y contraseña.
- **Salidas:** la cuenta creada, que ya puede iniciar sesión en la consola del socio, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que soy administrador, cuando registro a un estudiante con login y documento nuevos, entonces puede iniciar sesión con su contraseña.
2. Dado que el login ya existe, o ya hay un externo con ese documento, cuando intento registrarlo, entonces se rechaza por duplicado.
3. Dado que falta algún dato, cuando intento registrarlo, entonces se rechaza por dato inválido.

INVEST: independiente, valiosa porque el enunciado abre los torneos a personas fuera del club, pequeña y verificable.

Pruebas: `hut2ElEstudianteRegistradoPuedeIniciarSesion`, `hut2NoSeRepitenLoginNiDocumento`.

### HU_T3. Inscribirme en un torneo individual

**Como** socio, empleado, estudiante o participante externo, **quiero** inscribirme en un torneo de una disciplina individual, solo o con mi pareja si es de dobles, **para** competir con otros jugadores.

- **Entradas:** el torneo (elegido de la lista de torneos individuales abiertos) y, en dobles, el login de la pareja.
- **Salidas:** la inscripción, que dice si ocupó un cupo preferencial o regular y cuánto debe pagar, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que estoy entre los mejores del ranking y quedan cupos preferenciales, cuando me inscribo, entonces ocupo un cupo preferencial.
2. Dado que no estoy entre los mejores y no quedan cupos regulares, cuando me inscribo, entonces se rechaza por cupo aunque queden cupos preferenciales.
3. Dado que el torneo es competitivo, cuando me inscribo como socio o externo, entonces pago la tarifa; si soy empleado, la inscripción es gratis.
4. Dado que soy empleado y tengo turno el día del torneo, cuando me inscribo, entonces se rechaza por conflicto de horario.
5. Dado que soy socio de otro nivel en esa disciplina, cuando me inscribo, entonces se rechaza por regla deportiva.
6. Dado que ya estoy inscrito (solo o como pareja de alguien), cuando me inscribo otra vez, entonces se rechaza por duplicado.
7. Dado que el torneo es de dobles y no doy pareja, cuando me inscribo, entonces se rechaza.
8. Dado que el torneo ya empezó, cuando me inscribo, entonces se rechaza por estado.

INVEST: depende solo de HU_T1, es valiosa para el jugador, estimable y verificable. Los cupos preferenciales se podrían separar en otra historia, pero se dejaron aquí porque se deciden en el mismo momento de la inscripción.

Pruebas: `hut3ElMejorDelRankingTomaUnCupoPreferencial`, `hut3CuandoNoQuedanRegularesSoloEntraElPreferente`, `hut3EnUnCompetitivoElEmpleadoNoPagaYLosDemasSi`, `hut3ElEmpleadoDeTurnoEseDiaNoSePuedeInscribir`, `hut3RechazaOtroNivelDobleInscripcionYAlAdministrador`, `hut3EnDoblesSeInscribeConPareja`, y en `TorneoTest`, `losEmpleadosNoPaganTarifaEnUnCompetitivo`.

### HU_T4. Inscribir un equipo en un torneo de conjunto

**Como** entrenador (o administrador), **quiero** inscribir un equipo completo en un torneo de su deporte y categoría, **para** que mi equipo juegue el torneo.

- **Entradas:** el torneo de conjunto abierto y uno de mis equipos.
- **Salidas:** la inscripción con su tipo de cupo y el valor a pagar, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que el equipo está activo, es del deporte y la categoría del torneo y tiene el mínimo de jugadores activos, cuando lo inscribo, entonces queda inscrito.
2. Dado que el equipo tiene menos jugadores activos que el mínimo del deporte, cuando lo inscribo, entonces se rechaza porque no está completo.
3. Dado que no soy el entrenador del equipo ni el administrador, cuando lo inscribo, entonces se rechaza por permiso.
4. Dado que el equipo sumó puntos en competencias de su deporte y está entre los mejores de su categoría, cuando lo inscribo, entonces ocupa un cupo preferencial.
5. Dado que el torneo es de conjunto, cuando un jugador intenta inscribirse solo, entonces se rechaza.

INVEST: depende de HU_T1, valiosa para los equipos del club, pequeña y verificable.

Pruebas: `hut4ElEntrenadorInscribeSuEquipoCompleto`, `hut4RechazaEquipoIncompletoOtroEntrenadorYJugadoresSueltos`, `hut4ElEquipoConPuntosEnCompetenciasEsPreferente`.

### HU_T5. Salirme de un torneo

**Como** jugador inscrito (o entrenador de un equipo inscrito), **quiero** retirar mi inscripción antes de que el torneo empiece, **para** liberar el cupo si ya no puedo jugar.

- **Entradas:** el torneo del que me quiero salir y una confirmación.
- **Salidas:** confirmación de que el cupo quedó libre, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que estoy inscrito y el torneo tiene inscripciones abiertas, cuando me salgo, entonces el cupo vuelve a quedar libre.
2. Dado que el torneo ya empezó, cuando intento salirme, entonces se rechaza por estado.
3. Dado que la inscripción no es mía, cuando intento retirarla, entonces se rechaza por permiso (el administrador sí puede retirar cualquiera).

INVEST: independiente de los resultados, valiosa porque el enunciado pide liberar cupos, pequeña y verificable.

Pruebas: `hut5AlSalirseSeLiberaElCupo`, `hut5NadieRetiraInscripcionesAjenasNiDespuesDeEmpezar`, y en `TorneoTest`, `alRetirarseQuedaElCupoLibre`.

### HU_T6. Liberar los cupos preferenciales

**Como** administrador, **quiero** liberar los cupos preferenciales que no se llenaron, **para** que los ocupen jugadores o equipos regulares.

- **Entradas:** un torneo con inscripciones abiertas.
- **Salidas:** el número de cupos regulares libres después de liberar.

Criterios de aceptación:

1. Dado que no quedan cupos regulares pero sí preferenciales, cuando los libero, entonces un jugador regular ya se puede inscribir.
2. Dado que ya se liberaron, cuando intento liberarlos otra vez, entonces se rechaza por estado.

INVEST: independiente, pequeña, valiosa y verificable.

Pruebas: `hut6AlLiberarlosEntraUnJugadorRegular`, y en `TorneoTest`, `alLiberarLosPreferencialesPasanASerRegulares`.

### HU_T7. Iniciar un torneo

**Como** administrador, **quiero** cerrar las inscripciones e iniciar el torneo, **para** que el sistema arme los encuentros según el formato.

- **Entradas:** un torneo con inscripciones abiertas y una confirmación.
- **Salidas:** la lista de encuentros de la primera ronda y el torneo "En curso".

Criterios de aceptación:

1. Dado que hay al menos 2 inscritos, cuando inicio el torneo, entonces se crean los encuentros y queda "En curso".
2. Dado que hay menos de 2 inscritos, cuando intento iniciarlo, entonces se rechaza por cupo.
3. Dado que el torneo ya empezó, cuando alguien intenta inscribirse, entonces se rechaza por estado.
4. Dado que es de eliminación directa, cuando se inicia, entonces los preferenciales se siembran primero, el mejor juega contra el peor y con impares el mejor pasa directo.

INVEST: depende de HU_T3 o HU_T4, valiosa, estimable y verificable.

Pruebas: `hut7AlIniciarSeArmanLosEncuentrosYSeCierranLasInscripciones`, y en `FormatoTorneoTest`, `todosContraTodosArmaUnEncuentroPorPareja`, `eliminacionEnfrentaAlMejorSembradoContraElPeor`, `eliminacionSiembraPrimeroALosPreferenciales`, `conImparesElMejorSembradoPasaDirecto`.

### HU_T8. Registrar el resultado de un encuentro y entregar el premio

**Como** administrador, **quiero** registrar el marcador de cada encuentro, **para** que avancen las rondas y, al terminar, se entregue el premio al ganador.

- **Entradas:** el torneo en curso, un encuentro pendiente y los dos marcadores.
- **Salidas:** el encuentro con su resultado; los encuentros de la siguiente ronda si se completó una; y, si el torneo terminó, el ganador y su premio (bonos creados o premio en efectivo).

Criterios de aceptación:

1. Dado un torneo recreativo, cuando se registra el último resultado, entonces queda "Finalizado" y cada jugador del ganador recibe un bono con el porcentaje del torneo.
2. Dado un torneo competitivo, cuando termina, entonces el premio es el 70 % de lo recaudado y un empleado ganador no recibe su parte.
3. Dado un torneo de eliminación, cuando se completa una ronda, entonces se arma la siguiente con los ganadores.
4. Dado un encuentro de eliminación o de una disciplina sin empates, cuando registro un empate, entonces se rechaza por regla deportiva.
5. Dado un encuentro que ya tiene resultado, o un marcador negativo, cuando lo registro, entonces se rechaza.

INVEST: depende de HU_T7, es la más grande; se mantiene junta porque el premio sale del último resultado. Verificable con los criterios.

Pruebas: `hut8ElRecreativoTerminaYDaUnBonoACadaJugadorDelGanador`, `hut8LaEliminacionAvanzaDeRondaYElEmpleadoGanadorNoRecibeEfectivo`, y en `FormatoTorneoTest`, `eliminacionArmaLaFinalYDeclaraCampeon`, `todosContraTodosTerminaCuandoSeJueganTodos`.

### HU_T9. Consultar los torneos y su historial

**Como** cualquier usuario, **quiero** ver los torneos por estado y el historial detallado de cualquiera de ellos, **para** seguir su avance y sus resultados.

- **Entradas:** el estado que quiero ver (o todos) y el torneo.
- **Salidas:** la lista de torneos; y para uno de ellos: datos, cupos libres, premio, participantes con lo que pagaron, encuentros y posiciones. Si no hay datos se muestra "(sin información disponible)".

Criterios de aceptación:

1. Dado que hay torneos en distintos estados, cuando filtro por "En curso", entonces solo veo los que están en curso.
2. Dado un torneo en cualquier estado, cuando veo su historial, entonces aparecen participantes, encuentros y posiciones.
3. Dado un torneo todos contra todos, cuando hay empate en puntos, entonces se ordena por diferencia y luego por tantos a favor.
4. Dado un torneo de eliminación, cuando veo las posiciones, entonces dicen quién es el campeón y en qué ronda quedó eliminado cada uno.

INVEST: independiente, valiosa y verificable.

Pruebas: `hut9SeConsultanLosTorneosPorEstado`, y en `FormatoTorneoTest`, `todosContraTodosDesempataPorDiferencia`, `eliminacionArmaLaFinalYDeclaraCampeon`.

### HU_T10. Consultar y usar mis bonos

**Como** socio, **quiero** ver los bonos de descuento que he ganado en torneos recreativos y usarlos en una compra futura, **para** aprovechar el premio en la tienda o la cafetería.

- **Entradas:** ninguna para consultar. Para usarlo, el bono elegido al comprar.
- **Salidas:** la lista de bonos con su porcentaje, el torneo donde se ganó y si está disponible o usado.

Criterios de aceptación:

1. Dado que gané un torneo recreativo, cuando consulto mis bonos, entonces aparece disponible.
2. Dado que el bono es de otra persona, o ya lo usé, cuando intento usarlo, entonces no se acepta.
3. Dado que uso un bono en una compra, cuando se calcula el total, entonces no se suma con un código de descuento.

INVEST: depende de HU_T8, pequeña y verificable. El uso del bono en una compra está en la sección siguiente.

Pruebas: `hut10ElSocioVeSusBonosYSoloElPuedeUsarlos`. La persistencia de torneos y bonos se prueba en `PersistenciaTorneosTest`.

#### HU_T10 en la tienda: usar el bono en una compra

- **Entradas:** al comprar en la tienda o la cafetería, si el usuario tiene bonos sin usar, si quiere usar uno (s/n) y cuál (elegido de la lista). Para consultarlos, Tienda y cafetería, Ver mis bonos de torneos.
- **Salidas:** la compra con el porcentaje del bono como descuento y el bono marcado como usado, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que tengo un bono del 20 % sin usar, cuando lo uso en una compra, entonces se descuenta el 20 % y el bono queda usado.
2. Dado que uso el bono y un código a la vez, cuando compro, entonces se rechaza porque no son acumulables.
3. Dado que el bono es de otra persona o ya lo usé, cuando intento usarlo, entonces se rechaza y no cambia nada.

Pruebas: `hut10ElSocioUsaSuBonoYQuedaUsado`, `hut10ElBonoNoSeSumaConElCodigoNiLoUsaOtroNiSeUsaDosVeces`, y en `VentaTest`, `elBonoReemplazaAlCodigoYNoSeSuman`.

## Parte 4. Academias y entrenamiento

Estas historias son las del banco de la Entrega 1 para academias individuales, reservas, partidos de práctica y sesiones de entrenamiento (RF6 a RF11, RF15, RF16 y RF36). Las pruebas de integración están en `GestorAcademiasTest` y `GestorEntrenamientoTest`, y las unitarias del modelo en `AcademiaYReservaTest` y `EntrenamientoModeloTest`.

### Supuestos de la parte 4

1. **ELO inicial y factor K.** Toda inscripción nueva inicia con ELO 1000 y usa factor $K = 32$.
2. **ELO en dobles.** En partidos de 4 participantes (pareja 1: índices 0 y 1; pareja 2: índices 2 y 3), los compañeros de pareja deben registrar los mismos sets ganados sin empate entre parejas, y cada jugador actualiza su ELO contra el promedio ELO redondeado de la pareja rival.
3. **Permiso de cancelación.** Solo el socio titular de la reserva puede cancelarla, con al menos 2 horas de anticipación.

### HU_RF6. Inscribirse en una academia

Código en la Entrega 1: HU_S5.

**Como** socio, **quiero** inscribirme en una disciplina individual (tenis, pádel o tenis de mesa) indicando mi nivel inicial, **para** poder reservar canchas y jugar partidos de práctica.

- **Entradas:** desde la consola del socio (Academias y prácticas, Inscribirse en una academia) o del administrador (Academias y reservas, Inscribir a un socio en una academia). La disciplina (elegida de la lista) y el nivel (`PRINCIPIANTE`, `INTERMEDIO`, `AVANZADO`).
- **Salidas:** la inscripción creada con ELO inicial 1000, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que no estoy inscrito en una disciplina, cuando me inscribo con un nivel válido, entonces quedo inscrito con ELO 1000.
2. Dado que el administrador inscribe a un socio, cuando confirma, entonces la inscripción queda registrada.
3. Dado que ya estoy inscrito en la disciplina o intento inscribir a otro socio siendo socio, cuando solicito la inscripción, entonces se rechaza.

Pruebas: `rf6SocioSeInscribeEnAcademiaYAdminPuedeInscribir`, `rf6RechazaInscripcionDuplicadaOPorOtroSocio`.

### HU_RF7. Reservar una instalación y registrar ingreso

Código en la Entrega 1: HU_S6.

**Como** socio, **quiero** reservar una instalación disponible de mi disciplina y registrar el ingreso, **para** realizar mis prácticas cumpliendo las reglas del club.

- **Entradas:** desde la consola del socio, Academias y prácticas, Reservar una instalación o Registrar ingreso a una reserva. Instalación, fecha, hora de inicio y fin, modalidad (`SENCILLOS` o `DOBLES`), número de jugadores; para ingreso, si lleva bebida caliente.
- **Salidas:** la reserva creada en estado `ACTIVA` o la confirmación de ingreso, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que estoy inscrito y al día con la mensualidad, cuando reservo una instalación libre dentro del cupo simultáneo, entonces la reserva queda activa.
2. Dado un escenario de conjunto, un socio moroso, un horario inválido o el cupo simultáneo lleno, cuando intento reservar, entonces se rechaza.
3. Dado que ingreso a una cancha de pádel o mesa techada con bebida caliente, cuando registro el ingreso, entonces se rechaza.

Pruebas: `rf7SocioReservaCanchaEIngresaSinBebidaCaliente`, `rf7RechazaEscenarioConjuntoMorosoYCupoSimultaneo`, `rf7RechazaIngresoConBebidaCalienteEnPadel`, `rf7RechazaHorarioInvalido`, y en `AcademiaYReservaTest`, `instalacionesValidanCapacidadYBebidaCaliente`.

### HU_RF8. Cancelar una reserva

Código en la Entrega 1: HU_S7.

**Como** socio titular, **quiero** cancelar una reserva activa con al menos dos horas de anticipación, **para** liberar la instalación cuando no pueda asistir.

- **Entradas:** desde la consola del socio, Academias y prácticas, Cancelar una reserva. La reserva (elegida de la lista) y la hora actual.
- **Salidas:** la reserva en estado `CANCELADA` (y su partido de práctica cancelado si tenía uno), o el motivo del rechazo.

Criterios de aceptación:

1. Dado que soy el titular y faltan al menos 2 horas, cuando cancelo la reserva, entonces pasa a `CANCELADA` y cancela el partido asociado.
2. Dado que faltan menos de 2 horas o intenta cancelar otro socio o el administrador, cuando se solicita cancelar, entonces se rechaza.

Cambios frente a la Entrega 1: se unificó la validación de permiso al inicio de `cancelarReserva` para que únicamente el socio titular pueda cancelar.

Pruebas: `rf8TitularCancelaConMasDeDosHorasYCancelaPartido`, `rf8RechazaCancelarTardeYPorOtroUsuario`, y en `AcademiaYReservaTest`, `cancelarReservaCancelaPartidoAsociado`.

### HU_RF9. Programar un partido de práctica

Código en la Entrega 1: HU_S8.

**Como** socio, **quiero** programar un partido de práctica sobre una reserva activa, **para** jugar con socios de nivel compatible.

- **Entradas:** desde la consola del socio, Academias y prácticas, Programar un partido de práctica. La reserva y los socios participantes según la modalidad.
- **Salidas:** el partido programado, o el motivo del rechazo.

Criterios de aceptación:

1. Dado una reserva activa y participantes inscritos de niveles adyacentes sin choque de horario, cuando programo el partido, entonces queda asociado a la reserva.
2. Dado participantes con niveles no adyacentes (sin entrenador) o con choque de horario, cuando intento programar, entonces se rechaza.

Pruebas: `rf9ProgramaPartidoEntreNivelesAdyacentes`, `rf9RechazaNivelesNoAdyacentesYChoqueDeHorario`.

### HU_RF10. Registrar el resultado de un partido de práctica

Código en la Entrega 1: HU_S9.

**Como** socio participante, **quiero** registrar el marcador y estadísticas de un partido de práctica, **para** cerrar la reserva y actualizar mi ELO.

- **Entradas:** desde la consola del socio, Academias y prácticas, Registrar resultado de un partido de práctica. El partido y las estadísticas de cada participante según la disciplina.
- **Salidas:** el ganador del partido, la reserva en `FINALIZADA` y los ELO actualizados, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un partido en sencillos o dobles con marcador válido y sin empate en sets, cuando un participante registra el resultado, entonces se guarda el ganador y se actualiza el ELO.
2. Dado un empate en sets, compañeros con distintos sets en dobles o un registro por alguien que no participó, cuando se intenta registrar, entonces se rechaza.

Cambios frente a la Entrega 1: se implementó el cálculo de ELO en dobles usando el promedio ELO de la pareja rival.

Pruebas: `rf10RegistraResultadoYActualizaElo`, `rf10RechazaEmpateEnSetsYRegistroPorNoParticipante`, y en `AcademiaYReservaTest`, `partidoDoblesActualizaEloConPromedioParejaRival`, `partidoDoblesRechazaSetsDistintosEnLaMismaPareja`.

### HU_RF11. Consultar perfil, historial y ranking ELO

Código en la Entrega 1: HU_S10.

**Como** socio, **quiero** consultar mis inscripciones, mi historial de partidos y el ranking ELO de cada disciplina, **para** seguir mi progreso.

- **Entradas:** desde la consola del socio, Academias y prácticas, Ver mi perfil e inscripciones, Ver mi historial de partidos o Ver el ranking de una disciplina.
- **Salidas:** la lista de inscripciones, los partidos filtrados por fecha o el ranking ordenado de mayor a menor ELO.

Criterios de aceptación:

1. Dado que tengo inscripciones y partidos jugados, cuando consulto mi perfil, historial o ranking, entonces veo mis datos y el ranking ordenado de mayor a menor ELO.
2. Dado que intento consultar el perfil o historial de otro socio, cuando consulto, entonces se rechaza por permiso.

Pruebas: `rf11ConsultaPerfilHistorialYRanking`, `rf11RechazaConsultarPerfilOHistorialDeOtroSocio`, y en `AcademiaYReservaTest`, `inscripcionActualizaEloYDisciplinaOrdenaRanking`.

### HU_RF15. Planear una sesión de entrenamiento

Código en la Entrega 1: HU_E5.

**Como** entrenador, **quiero** planear sesiones de entrenamiento individuales, grupales o de equipo, **para** preparar a los socios y equipos del club.

- **Entradas:** desde la consola del empleado (solo entrenadores), Entrenamientos, Planear sesión individual o grupal o Planear sesión de equipo. Disciplina o equipo, instalación, fecha, hora, duración y socios convocados.
- **Salidas:** la sesión planeada con sus convocatorias en estado `PENDIENTE`, o el motivo del rechazo.

Criterios de aceptación:

1. Dado un entrenador, una instalación compatible libre y socios/jugadores disponibles, cuando planeo la sesión, entonces queda registrada con sus convocatorias.
2. Dado un fisioterapeuta, un socio, una instalación incompatible o un choque de horario, cuando intento planear, entonces se rechaza.

Pruebas: `rf15EntrenadorPlaneaSesionGrupalYDeEquipo`, `rf15RechazaFisioterapeutaSocioYChoqueDeHorario`, y en `EntrenamientoModeloTest`, `sesionDetectaConflictoHorarioYBuscaConvocatoria`.

### HU_RF16. Registrar asistencia, evaluación y consultar convocatorias

Código en la Entrega 1: HU_E6.

**Como** entrenador, **quiero** finalizar la sesión y registrar la asistencia y evaluación de cada convocado, y como socio consultar mis convocatorias, **para** llevar el seguimiento del entrenamiento.

- **Entradas:** desde la consola del empleado (Entrenamientos: Finalizar una sesión, Registrar asistencia, Registrar evaluación) o del socio (Academias y prácticas: Ver mis convocatorias de entrenamiento).
- **Salidas:** la sesión finalizada, la asistencia y evaluación guardadas o el listado de convocatorias del socio.

Criterios de aceptación:

1. Dado una sesión finalizada, cuando el entrenador responsable registra asistencia (`asistió`, `faltó`, `llegó tarde`) y una evaluación de 1 a 5, entonces quedan guardadas y visibles para el socio.
2. Dado una sesión sin finalizar, otro entrenador no responsable, estado `pendiente` o nota fuera de 1 a 5, cuando se intenta registrar, entonces se rechaza.

Pruebas: `rf16FinalizaSesionRegistraAsistenciaEvaluacionYConsultaConvocatorias`, `rf16RechazaRegistrarAntesDeFinalizarOtroEntrenadorYEstadoPendiente`, y en `EntrenamientoModeloTest`, `evaluacionValidaRangoDeNota`, `estadoAsistenciaConvierteTextosYRechazaInvalidos`, `convocatoriaRegistraAsistenciaYEvaluacion`.

### HU_RF36. Consultar reservas e instalaciones

Código en la Entrega 1: HU_A13.

**Como** administrador, **quiero** consultar las reservas por fecha y las instalaciones por disciplina, **para** supervisar el uso de los espacios deportivos.

- **Entradas:** desde la consola del administrador, Academias y reservas, Consultar reservas de una fecha o Consultar instalaciones. Fecha o disciplina opcional.
- **Salidas:** la lista de reservas o instalaciones filtradas, o el rechazo si el usuario no tiene permiso.

Criterios de aceptación:

1. Dado que soy administrador o socio, cuando consulto reservas por fecha o instalaciones por disciplina, entonces recibo la lista filtrada correspondiente.
2. Dado que soy un empleado (entrenador o fisioterapeuta), cuando intento consultar las reservas, entonces se rechaza por permiso.

Pruebas: `rf36ConsultaReservasPorFechaEInstalacionesPorDisciplina`, `rf36RechazaConsultarReservasAEmpleados`.

# Historias de usuario: torneos internos

Club Deportivo Séneca, Proyecto 2. Grupo 1.

Estas historias cubren los requerimientos nuevos de torneos recreativos y competitivos. Cada una indica sus entradas y salidas, sus criterios de aceptación y la prueba JUnit que la verifica (en `test/`). Las pruebas de integración están en `GestorTorneosTest` y tienen el prefijo de la historia en el nombre (por ejemplo `hut3...`).

## Supuestos

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

## HU_T1. Crear un torneo

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

## HU_T2. Registrar un estudiante o participante externo

**Como** administrador, **quiero** registrar a estudiantes de la universidad y a personas externas con un usuario propio, **para** que también puedan inscribirse en los torneos internos.

- **Entradas:** nombre, apellidos, documento, si es estudiante, login y contraseña.
- **Salidas:** la cuenta creada, que ya puede iniciar sesión en la consola del socio, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que soy administrador, cuando registro a un estudiante con login y documento nuevos, entonces puede iniciar sesión con su contraseña.
2. Dado que el login ya existe, o ya hay un externo con ese documento, cuando intento registrarlo, entonces se rechaza por duplicado.
3. Dado que falta algún dato, cuando intento registrarlo, entonces se rechaza por dato inválido.

INVEST: independiente, valiosa porque el enunciado abre los torneos a personas fuera del club, pequeña y verificable.

Pruebas: `hut2ElEstudianteRegistradoPuedeIniciarSesion`, `hut2NoSeRepitenLoginNiDocumento`.

## HU_T3. Inscribirme en un torneo individual

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

## HU_T4. Inscribir un equipo en un torneo de conjunto

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

## HU_T5. Salirme de un torneo

**Como** jugador inscrito (o entrenador de un equipo inscrito), **quiero** retirar mi inscripción antes de que el torneo empiece, **para** liberar el cupo si ya no puedo jugar.

- **Entradas:** el torneo del que me quiero salir y una confirmación.
- **Salidas:** confirmación de que el cupo quedó libre, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que estoy inscrito y el torneo tiene inscripciones abiertas, cuando me salgo, entonces el cupo vuelve a quedar libre.
2. Dado que el torneo ya empezó, cuando intento salirme, entonces se rechaza por estado.
3. Dado que la inscripción no es mía, cuando intento retirarla, entonces se rechaza por permiso (el administrador sí puede retirar cualquiera).

INVEST: independiente de los resultados, valiosa porque el enunciado pide liberar cupos, pequeña y verificable.

Pruebas: `hut5AlSalirseSeLiberaElCupo`, `hut5NadieRetiraInscripcionesAjenasNiDespuesDeEmpezar`, y en `TorneoTest`, `alRetirarseQuedaElCupoLibre`.

## HU_T6. Liberar los cupos preferenciales

**Como** administrador, **quiero** liberar los cupos preferenciales que no se llenaron, **para** que los ocupen jugadores o equipos regulares.

- **Entradas:** un torneo con inscripciones abiertas.
- **Salidas:** el número de cupos regulares libres después de liberar.

Criterios de aceptación:

1. Dado que no quedan cupos regulares pero sí preferenciales, cuando los libero, entonces un jugador regular ya se puede inscribir.
2. Dado que ya se liberaron, cuando intento liberarlos otra vez, entonces se rechaza por estado.

INVEST: independiente, pequeña, valiosa y verificable.

Pruebas: `hut6AlLiberarlosEntraUnJugadorRegular`, y en `TorneoTest`, `alLiberarLosPreferencialesPasanASerRegulares`.

## HU_T7. Iniciar un torneo

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

## HU_T8. Registrar el resultado de un encuentro y entregar el premio

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

## HU_T9. Consultar los torneos y su historial

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

## HU_T10. Consultar y usar mis bonos

**Como** socio, **quiero** ver los bonos de descuento que he ganado en torneos recreativos y usarlos en una compra futura, **para** aprovechar el premio en la tienda o la cafetería.

- **Entradas:** ninguna para consultar. Para usarlo, el bono elegido al comprar.
- **Salidas:** la lista de bonos con su porcentaje, el torneo donde se ganó y si está disponible o usado.

Criterios de aceptación:

1. Dado que gané un torneo recreativo, cuando consulto mis bonos, entonces aparece disponible.
2. Dado que el bono es de otra persona, o ya lo usé, cuando intento usarlo, entonces no se acepta.
3. Dado que uso un bono en una compra, cuando se calcula el total, entonces no se suma con un código de descuento.

INVEST: depende de HU_T8, pequeña y verificable. El uso del bono en una compra está en `historias-usuario-tienda-personal.md`.

Pruebas: `hut10ElSocioVeSusBonosYSoloElPuedeUsarlos`. La persistencia de torneos y bonos se prueba en `PersistenciaTorneosTest`.

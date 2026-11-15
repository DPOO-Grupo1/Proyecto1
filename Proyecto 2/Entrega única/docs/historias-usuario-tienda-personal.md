# Historias de usuario: tienda, cafetería, personal y reportes

Club Deportivo Séneca, Proyecto 2. Grupo 1, área C (tienda, cafetería, personal y reportes).

Estas historias son las del banco de la Entrega 1 para el área C (RF3 a RF5, RF17 a RF21 y RF28 a RF35). No se reescriben: aquí se indica, para cada una, desde dónde se usa en la consola, sus entradas y salidas, sus criterios de aceptación, los cambios frente a la Entrega 1 y la prueba JUnit que la verifica (en `test/`). Las pruebas de integración están en `GestorTiendaTest`, `GestorPersonalTest` y `GestorReportesTest` y tienen el requerimiento al inicio del nombre (por ejemplo `rf33...`). Al final está la parte de HU_T10 (usar el bono de torneo) que implementa el área C.

## Supuestos

El análisis deja abiertos algunos puntos. Estas son las decisiones del grupo para el área C:

1. **Descuento de empleado.** Todo código de descuento pertenece a un empleado. Da 15 % si lo usa su dueño y 8 % si lo usa cualquier otro cliente. Un código que no existe o está inactivo se rechaza.
2. **No acumulable.** Una compra lleva como máximo un descuento: un código o un bono de torneo, no los dos. Los puntos de fidelidad no cuentan como descuento y se pueden usar con cualquiera de ellos.
3. **Puntos.** Un punto vale $1. Se gana el 2 % del total pagado, redondeado hacia abajo. No se pueden redimir más puntos que el saldo ni más que el valor de la compra.
4. **Impuestos y propina.** La tienda cobra IVA del 19 % y la cafetería impuesto al consumo del 8 %. La propina se calcula sobre el subtotal; la consola sugiere el 10 % y el cliente la puede cambiar entre 0 y 100 %.
5. **Bebidas calientes.** No se venden a quien en ese momento tenga una reserva vigente, un partido de práctica o una sesión de entrenamiento en una cancha de pádel o una mesa de tenis de mesa techada.
6. **Fecha de las ventas.** Una venta toma la fecha del club y la hora actual, para que los reportes no dependan del día en que se ejecute el programa.
7. **Horario de atención.** De lunes a sábado, de 6:00 a 20:00, en bloques de una hora. Ningún turno ni solicitud de cambio puede quedar fuera de ese horario.
8. **Turnos.** Se repiten cada semana. Al publicar una semana se reemplazan todos los turnos anteriores, y cada bloque debe tener al menos 2 entrenadores y 1 fisioterapeuta.
9. **Ranking del reporte deportivo.** Es el ELO actual por disciplina, de mayor a menor, con los partidos de práctica jugados y ganados en el periodo. El sistema no guarda el historial del ELO, así que no se muestra cuánto cambió.

## HU_S2. Comprar en la tienda (RF3)

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

## HU_S3. Consumir en la cafetería (RF4)

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

## HU_S4. Redimir puntos de fidelidad (RF5)

**Como** socio, **quiero** usar mis puntos como descuento, **para** pagar menos en mis compras.

- **Entradas:** en cualquiera de las dos compras, los puntos a redimir, de 0 al saldo actual.
- **Salidas:** el total con los puntos descontados y el nuevo saldo, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que tengo 9.000 puntos, cuando redimo 5.000 en una compra, entonces se descuentan $5.000 y mi saldo queda en 4.000 más los puntos que gane.
2. Dado que pido más puntos que mi saldo, cuando compro, entonces se rechaza y el saldo no cambia.
3. Dado que pido más puntos que el valor de la compra, cuando compro, entonces se rechaza y el saldo no cambia.

Cambios frente a la Entrega 1: se agregó el criterio 3, porque antes el total podía quedar negativo.

INVEST: depende de HU_S2 o HU_S3, pequeña y verificable.

Pruebas: `rf5RedimePuntosComoPesos`, `rf5RechazaMasPuntosQueElSaldo`, `rf5RechazaMasPuntosQueElValorDeLaCompra`, y en `VentaTest`, `losPuntosRedimidosSeRestanComoPesos`.

## HU_E4. Comprar con código de descuento de empleado (RF17)

**Como** entrenador, **quiero** comprar con un código de empleado, **para** recibir el descuento del personal.

- **Entradas:** desde la consola del empleado, Tienda y cafetería. Las mismas de la compra, con el número del código.
- **Salidas:** la compra con el descuento aplicado y el 2 % en puntos, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que uso mi propio código, cuando compro, entonces recibo el 15 % de descuento.
2. Dado que uso el código de otro empleado, cuando compro, entonces recibo el 8 %.
3. Dado que el código no existe o está inactivo, cuando compro, entonces se rechaza.

Cambios frente a la Entrega 1: el menú del empleado hereda del menú del socio porque la compra es igual; el porcentaje lo decide `CodigoDescuento`.

INVEST: depende de HU_S2, pequeña y verificable.

Pruebas: `rf17ElEmpleadoConSuCodigoRecibeQuincePorCientoYConElAjenoOcho`, `rf3RechazaCodigoInexistenteEInactivo`, y en `VentaTest`, `elCodigoDaQuincePorCientoASuDuenoYOchoAlResto`.

## HU_E7. Solicitar un cambio de turno (RF18)

**Como** entrenador, **quiero** pedir que me cambien un turno a otro día u hora, **para** ajustar mi horario cuando lo necesite.

- **Entradas:** desde la consola del empleado, Turnos y sugerencias, Solicitar un cambio de turno. Uno de mis turnos (elegido de la lista), el nuevo día y las nuevas horas de inicio y fin (HH:MM).
- **Salidas:** la solicitud en estado pendiente, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que el turno es mío y el nuevo horario está dentro del horario de atención, cuando lo pido, entonces la solicitud queda pendiente y el turno no cambia todavía.
2. Dado que el turno es de otro empleado, cuando lo pido, entonces se rechaza por permiso.
3. Dado que propongo un domingo o un horario fuera de 6:00 a 20:00, cuando lo pido, entonces se rechaza por dato inválido.

INVEST: independiente, valiosa para el personal, pequeña y verificable.

Pruebas: `rf18ElEntrenadorPideCambiarSuTurnoYQuedaPendiente`, `rf18RechazaTurnoAjenoDomingoYHorarioFueraDeAtencion`.

## HU_F1. Solicitar un cambio de turno y consultar su estado (RF19)

**Como** fisioterapeuta, **quiero** pedir un cambio de turno y ver si fue aprobado o rechazado, **para** organizar mi semana.

- **Entradas:** las mismas de HU_E7. Para consultar, ninguna (Ver mis solicitudes de cambio).
- **Salidas:** la lista de mis solicitudes con su estado: pendiente, aprobada o rechazada.

Criterios de aceptación:

1. Dado que ya tengo una solicitud pendiente sobre un turno, cuando pido otra sobre el mismo turno, entonces se rechaza por estado.
2. Dado que el administrador resolvió mi solicitud, cuando consulto mis solicitudes, entonces veo si fue aprobada o rechazada.

INVEST: depende de HU_E7 y de HU_A10, pequeña y verificable.

Pruebas: `rf19NoAceptaUnaSegundaSolicitudPendienteYMuestraElEstado`, y en `TurnoTest`, `unTurnoConSolicitudPendienteNoAdmiteOtraHastaQueSeResuelva`.

## HU_F2. Comprar con descuento de empleado (RF20)

**Como** fisioterapeuta, **quiero** comprar con mi código, **para** recibir el 15 % de descuento.

- **Entradas:** las mismas de HU_E4.
- **Salidas:** la compra con el descuento, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que uso mi código en la cafetería, cuando compro, entonces recibo el 15 %.
2. Dado que el código no es de ningún empleado, cuando compro, entonces se rechaza.

INVEST: depende de HU_S3, pequeña y verificable.

Pruebas: `rf20ElFisioterapeutaCompraEnLaCafeteriaConCodigoDeEmpleado`.

## HU_F3. Sugerir productos (RF21)

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

## HU_A6. Consultar el inventario (RF29)

**Como** administrador, **quiero** ver los productos y cantidades de cada ubicación, **para** controlar el inventario.

- **Entradas:** desde la consola del administrador, Inventario de tienda y cafetería, Consultar el inventario. La ubicación, elegida de la lista.
- **Salidas:** cada producto de la ubicación con sus unidades, o un aviso si no hay productos.

Criterios de aceptación:

1. Dado que la ubicación existe, cuando la consulto, entonces veo sus productos y cantidades.
2. Dado que la ubicación no existe, cuando la consulto, entonces se rechaza.
3. Dado que no soy administrador, cuando consulto, entonces se rechaza por permiso.

INVEST: independiente, pequeña y verificable.

Pruebas: `rf29ConsultaElInventarioDeUnaUbicacion`, `rf29RechazaUbicacionInexistenteYUsuariosSinPermiso`.

## HU_A7. Mover productos entre ubicaciones (RF30)

**Como** administrador, **quiero** trasladar productos de una ubicación a otra, **para** abastecer la tienda y la cafetería desde la bodega.

- **Entradas:** desde Inventario de tienda y cafetería, Mover productos. El producto, el origen y el destino (elegidos de listas) y la cantidad.
- **Salidas:** confirmación de lo movido, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que hay suficientes unidades en el origen, cuando muevo, entonces bajan en el origen y suben en el destino; si el destino no tenía el producto, se crea su existencia.
2. Dado que pido mover más de lo disponible, cuando muevo, entonces se rechaza y nada cambia.

INVEST: depende de HU_A6 para verificar, pequeña y verificable.

Pruebas: `rf30MueveProductosEntreUbicaciones`, `rf30RechazaMoverMasDeLoDisponible`.

## HU_A6c. Reabastecer y registrar productos (RF31)

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

## HU_A8. Consultar el historial global (RF28)

**Como** administrador, **quiero** ver todas las reservas, partidos y sesiones con filtros, **para** auditar la actividad del club.

- **Entradas:** desde la consola del administrador, Reportes y mensualidades, Consultar el historial global. Filtros opcionales: rango de fechas, socio (por su id), entrenador, equipo, deporte y categoría.
- **Salidas:** cuatro listas (partidos internos y de competencia, partidos de práctica, reservas y sesiones de entrenamiento), con un aviso en las que estén vacías.

Criterios de aceptación:

1. Dado que filtro por un socio, cuando consulto, entonces solo veo sus reservas, partidos y sesiones.
2. Dado que filtro por un rango de fechas sin actividad, cuando consulto, entonces las listas salen vacías.
3. Dado que no soy administrador, cuando consulto, entonces se rechaza por permiso.

INVEST: independiente, valiosa para la administración, verificable.

Pruebas: `rf28ElHistorialFiltraPorSocioYPorFechas`, `rf28SoloElAdministradorConsultaElHistorial`.

## HU_A9. Resolver sugerencias de productos (RF32)

**Como** administrador, **quiero** aprobar o rechazar las sugerencias del personal, **para** decidir qué productos incorporar.

- **Entradas:** desde Turnos y sugerencias del personal, Resolver sugerencias pendientes. La sugerencia (elegida de la lista de pendientes), si se aprueba (s/n) y el motivo si se rechaza.
- **Salidas:** la sugerencia con su nuevo estado; si se aprueba, el aviso de que ya se puede registrar el producto.

Criterios de aceptación:

1. Dado que apruebo una sugerencia, cuando la resuelvo, entonces queda aprobada.
2. Dado que rechazo una sugerencia con motivo, cuando la resuelvo, entonces queda rechazada y el empleado ve el motivo.
3. Dado que rechazo sin motivo, cuando la resuelvo, entonces se rechaza la acción.
4. Dado que la sugerencia ya fue resuelta, cuando intento resolverla otra vez, entonces se rechaza por estado.

INVEST: depende de HU_F3, pequeña y verificable.

Pruebas: `rf32ApruebaUnaYRechazaOtraConMotivo`, `rf32RechazaSinMotivoDosVecesYSinPermiso`.

## HU_A10. Gestionar turnos y solicitudes de cambio (RF33)

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

## HU_A11. Generar reportes (RF34)

**Como** administrador, **quiero** reportes financieros y deportivos por periodo, **para** tomar decisiones sobre el club.

- **Entradas:** desde Reportes y mensualidades, Generar reporte financiero o deportivo. El periodo (diario, semanal o mensual) y la fecha de inicio (AAAA-MM-DD).
- **Salidas:** el financiero con neto, impuesto y total de la tienda y de la cafetería y el total de mensualidades, con un aviso si no hubo movimientos; el deportivo con los resultados de los partidos, la asistencia a entrenamientos y el ranking ELO por disciplina con los partidos jugados y ganados en el periodo.

Criterios de aceptación:

1. Dado un mes con una venta en la tienda y un pago de mensualidad, cuando genero el reporte financiero mensual, entonces suma solo lo de ese mes por rubro.
2. Dado un periodo sin datos, cuando genero el reporte, entonces todo sale en cero.
3. Dado que hay socios inscritos en una disciplina, cuando genero el reporte deportivo, entonces aparecen ordenados por ELO con su actividad del periodo.
4. Dado que no soy administrador, cuando genero un reporte, entonces se rechaza por permiso.

Cambios frente a la Entrega 1: el ranking se ordena de mayor a menor y muestra los partidos del periodo (ver el supuesto 9).

INVEST: depende de que haya ventas, pagos y partidos, valiosa para la administración, verificable.

Pruebas: `rf34ReporteFinancieroSumaVentasYMensualidadesDelPeriodo`, `rf34PeriodoSinDatosDaReporteEnCeroYSoloLoGeneraElAdministrador`, `rf34ReporteDeportivoIncluyeLaAsistenciaYSoloLoGeneraElAdministrador`, `rf34ReporteDeportivoMuestraElRankingConLosPartidosDelPeriodo`.

## HU_A12. Registrar mensualidades (RF35)

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

## HU_T10 en la tienda. Usar el bono de torneo en una compra

La historia HU_T10 está en `historias-usuario-torneos.md`. El área C implementa la parte de usar el bono al comprar.

- **Entradas:** al comprar en la tienda o la cafetería, si el usuario tiene bonos sin usar, si quiere usar uno (s/n) y cuál (elegido de la lista). Para consultarlos, Tienda y cafetería, Ver mis bonos de torneos.
- **Salidas:** la compra con el porcentaje del bono como descuento y el bono marcado como usado, o el motivo del rechazo.

Criterios de aceptación:

1. Dado que tengo un bono del 20 % sin usar, cuando lo uso en una compra, entonces se descuenta el 20 % y el bono queda usado.
2. Dado que uso el bono y un código a la vez, cuando compro, entonces se rechaza porque no son acumulables.
3. Dado que el bono es de otra persona o ya lo usé, cuando intento usarlo, entonces se rechaza y no cambia nada.

Pruebas: `hut10ElSocioUsaSuBonoYQuedaUsado`, `hut10ElBonoNoSeSumaConElCodigoNiLoUsaOtroNiSeUsaDosVeces`, y en `VentaTest`, `elBonoReemplazaAlCodigoYNoSeSuman`.

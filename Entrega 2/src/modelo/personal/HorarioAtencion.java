package modelo.personal;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioAtencion {
    private List<DayOfWeek> dias;
    private LocalTime apertura;
    private LocalTime cierre;

    // Por defecto: lunes a sábado, de 6:00 a 20:00 (el supuesto del plan)
    public HorarioAtencion() {
        this.dias = new ArrayList<DayOfWeek>();
        for (DayOfWeek d : DayOfWeek.values()) {
            if (d != DayOfWeek.SUNDAY) {
                dias.add(d);
            }
        }
        this.apertura = LocalTime.of(6, 0);
        this.cierre = LocalTime.of(20, 0);
    }

    public List<DayOfWeek> getDias() { return dias; }
    public LocalTime getApertura() { return apertura; }
    public LocalTime getCierre() { return cierre; }

    // true si el rango queda completo dentro del horario de atención
    public boolean contiene(DayOfWeek dia, LocalTime inicio, LocalTime fin) {
        return dias.contains(dia)
                && !inicio.isBefore(apertura)
                && !fin.isAfter(cierre)
                && inicio.isBefore(fin);
    }

    // Bloques de una hora: devuelve la hora de inicio de cada uno (6:00, 7:00, ... 19:00)
    public List<LocalTime> getBloques() {
        List<LocalTime> bloques = new ArrayList<LocalTime>();
        LocalTime hora = apertura;
        while (hora.isBefore(cierre)) {
            bloques.add(hora);
            hora = hora.plusHours(1);
        }
        return bloques;
    }
}

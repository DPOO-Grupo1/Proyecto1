package logica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excepciones.NoEncontradoException;
import excepciones.PermisoDenegadoException;
import excepciones.VentaInvalidaException;
import excepciones.DatoInvalidoException;
import excepciones.DuplicadoException;
import modelo.tienda.Articulo;
import modelo.tienda.CodigoDescuento;
import modelo.tienda.Existencia;
import modelo.tienda.Producto;
import modelo.tienda.ProductoCafeteria;
import modelo.tienda.Snack;
import modelo.tienda.Ubicacion;
import modelo.tienda.VentaCafeteria;
import modelo.tienda.VentaTienda;
import modelo.usuarios.Cliente;
import modelo.usuarios.Usuario;

/**
 * Maneja las ventas de tienda y cafetería, descuentos, puntos e inventario.
 * Cubre RF3, RF4, RF5, RF17, RF20, RF29, RF30 y RF31.
 */
public class GestorTienda extends Gestor {

    public GestorTienda(Club club) {
        super(club);
    }
    
  //-----------RF3, RF17, RF20, RF5: comprar en la tienda
    public VentaTienda comprarEnTienda(Usuario quien, Map<String, Integer> items,
            Integer codigo, int puntosARedimir)
            throws PermisoDenegadoException, NoEncontradoException, VentaInvalidaException {

        // 1. permiso: solo socios y empleados (Cliente)
        if (!(quien instanceof Cliente)) {
            throw new PermisoDenegadoException("Solo un socio o empleado puede comprar");
        }
        Cliente cliente = (Cliente) quien;

        // 2. validar ítems
        if (items == null || items.isEmpty()) {
            throw new VentaInvalidaException("La compra no tiene productos");
        }
        Ubicacion tienda = club.buscarUbicacion("Tienda");
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            Producto p = club.buscarProducto(item.getKey());
            if (!(p instanceof Articulo)) {
                throw new VentaInvalidaException("'" + p.getNombre() + "' no se vende en la tienda");
            }
            if (item.getValue() <= 0) {
                throw new VentaInvalidaException("La cantidad de '" + p.getNombre() + "' debe ser mayor a 0");
            }
            if (club.buscarExistencia(p, tienda).getCantidad() < item.getValue()) {
                throw new VentaInvalidaException("No hay existencias suficientes de '" + p.getNombre() + "'");
            }
        }

        // 3. validar código y puntos
        CodigoDescuento cod = null;
        if (codigo != null) {
            cod = club.buscarCodigo(codigo);
            if (!cod.isActivo()) {
                throw new VentaInvalidaException("El código de descuento está inactivo");
            }
        }
        if (puntosARedimir < 0 || puntosARedimir > cliente.getPuntosFidelidad()) {
            throw new VentaInvalidaException("Puntos insuficientes: tiene " + cliente.getPuntosFidelidad());
        }

        // 4. crear la venta
        VentaTienda venta = new VentaTienda(cliente, LocalDateTime.now());
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            venta.agregarDetalle(club.buscarProducto(item.getKey()), item.getValue());
        }
        if (cod != null) {
            venta.aplicarCodigo(cod);
        }
        venta.redimirPuntos(puntosARedimir);
        venta.calcularTotal();

        // 5. aplicar cambios
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            Producto p = club.buscarProducto(item.getKey());
            club.buscarExistencia(p, tienda).descontar(item.getValue());
        }
        cliente.restarPuntos(puntosARedimir);
        cliente.sumarPuntos(venta.calcularPuntosGenerados());

        // 6. registrar
        club.agregarVenta(venta);
        cliente.agregarCompra(venta);
        return venta;
    }
    
  //-----------RF4, RF17, RF20, RF5: comprar en la cafetería
    public VentaCafeteria comprarEnCafeteria(Usuario quien, Map<String, Integer> items,
            Integer codigo, int puntosARedimir, int porcentajePropina)
            throws PermisoDenegadoException, NoEncontradoException, VentaInvalidaException, DatoInvalidoException {

        // 1. permiso: solo socios y empleados (Cliente)
        if (!(quien instanceof Cliente)) {
            throw new PermisoDenegadoException("Solo un socio o empleado puede comprar");
        }
        Cliente cliente = (Cliente) quien;

        // 2. validar propina e ítems
        if (porcentajePropina < 0) {
            throw new DatoInvalidoException("La propina no puede ser negativa");
        }
        if (items == null || items.isEmpty()) {
            throw new VentaInvalidaException("La compra no tiene productos");
        }
        Ubicacion cafeteria = club.buscarUbicacion("Cafetería");
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            Producto p = club.buscarProducto(item.getKey());
            if (!(p instanceof ProductoCafeteria)) {
                throw new VentaInvalidaException("'" + p.getNombre() + "' no se vende en la cafetería");
            }
            if (item.getValue() <= 0) {
                throw new VentaInvalidaException("La cantidad de '" + p.getNombre() + "' debe ser mayor a 0");
            }
            if (club.buscarExistencia(p, cafeteria).getCantidad() < item.getValue()) {
                throw new VentaInvalidaException("No hay existencias suficientes de '" + p.getNombre() + "'");
            }
            // TODO (Área B): si p es Bebida caliente y el cliente está en pádel o mesa techada,
            // lanzar VentaInvalidaException. Necesita cliente.estaEnInstalacionRestringida(...)
        }

        // 3. validar código y puntos
        CodigoDescuento cod = null;
        if (codigo != null) {
            cod = club.buscarCodigo(codigo);
            if (!cod.isActivo()) {
                throw new VentaInvalidaException("El código de descuento está inactivo");
            }
        }
        if (puntosARedimir < 0 || puntosARedimir > cliente.getPuntosFidelidad()) {
            throw new VentaInvalidaException("Puntos insuficientes: tiene " + cliente.getPuntosFidelidad());
        }

        // 4. crear la venta
        VentaCafeteria venta = new VentaCafeteria(cliente, LocalDateTime.now(), porcentajePropina);
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            venta.agregarDetalle(club.buscarProducto(item.getKey()), item.getValue());
        }
        if (cod != null) {
            venta.aplicarCodigo(cod);
        }
        venta.redimirPuntos(puntosARedimir);
        venta.calcularTotal();

        // 5. aplicar cambios
        for (Map.Entry<String, Integer> item : items.entrySet()) {
            Producto p = club.buscarProducto(item.getKey());
            club.buscarExistencia(p, cafeteria).descontar(item.getValue());
        }
        cliente.restarPuntos(puntosARedimir);
        cliente.sumarPuntos(venta.calcularPuntosGenerados());

        // 6. registrar
        club.agregarVenta(venta);
        cliente.agregarCompra(venta);
        return venta;
    }
    
  //-----------RF4: consultar alérgenos
    public List<String> consultarAlergenos(Usuario quien, Map<String, Integer> items)
            throws NoEncontradoException {
        List<String> alergenos = new ArrayList<String>();
        for (String nombre : items.keySet()) {
            Producto p = club.buscarProducto(nombre);
            if (p instanceof Snack) {
                for (String a : ((Snack) p).getAlergenos()) {
                    if (!alergenos.contains(a)) {
                        alergenos.add(a);
                    }
                }
            }
        }
        return alergenos;
    }
    
    //-----------RF31: reabastecer
    
    public void reabastecer(Usuario quien, String producto, String ubicacion, int cantidad)
            throws PermisoDenegadoException, NoEncontradoException, DatoInvalidoException {
        verificarAdministrador(quien);

        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad debe ser mayor a 0");
        }

        Producto p = club.buscarProducto(producto);
        Ubicacion u = club.buscarUbicacion(ubicacion);
        club.buscarExistencia(p, u).sumar(cantidad);
    }

    //-----------RF31: registrar producto nuevo
    public void registrarProducto(Usuario quien, Producto producto, String ubicacion, int cantidadInicial)
            throws PermisoDenegadoException, NoEncontradoException, DuplicadoException, DatoInvalidoException {
        verificarAdministrador(quien);

        // no puede existir otro producto con ese nombre
        boolean yaExiste = true;
        try {
            club.buscarProducto(producto.getNombre());
        } catch (NoEncontradoException e) {
            yaExiste = false;
        }
        if (yaExiste) {
            throw new DuplicadoException("Ya existe el producto '" + producto.getNombre() + "'");
        }

        if (producto.getPrecio() <= 0 || cantidadInicial < 0) {
            throw new DatoInvalidoException("El precio debe ser mayor a 0 y la cantidad inicial no puede ser negativa");
        }

        Ubicacion u = club.buscarUbicacion(ubicacion);
        club.agregarProducto(producto);
        club.agregarExistencia(new Existencia(producto, u, cantidadInicial));
    }

    //-----------RF29: consultar inventario
    public Map<Producto, Integer> consultarInventario(Usuario quien, String ubicacion)
            throws PermisoDenegadoException, NoEncontradoException {
        verificarAdministrador(quien);

        Ubicacion u = club.buscarUbicacion(ubicacion);
        Map<Producto, Integer> inventario = new HashMap<Producto, Integer>();
        for (Existencia e : club.getExistencias()) {
            if (e.getUbicacion() == u) {
                inventario.put(e.getProducto(), e.getCantidad());
            }
        }
        return inventario;
    }

    //-----------RF30: mover productos entre ubicaciones
    public void moverProductos(Usuario quien, String producto, String origen, String destino, int cantidad)
            throws PermisoDenegadoException, NoEncontradoException, DatoInvalidoException {
        verificarAdministrador(quien);

        Producto p = club.buscarProducto(producto);
        Ubicacion uOrigen = club.buscarUbicacion(origen);
        Ubicacion uDestino = club.buscarUbicacion(destino);

        Existencia exOrigen = club.buscarExistencia(p, uOrigen);
        if (cantidad <= 0 || cantidad > exOrigen.getCantidad()) {
            throw new DatoInvalidoException("La cantidad debe ser mayor a 0 y no superar las " + exOrigen.getCantidad() + " disponibles");
        }

        // si en el destino todavía no hay existencia de ese producto, se crea en 0
        Existencia exDestino;
        try {
            exDestino = club.buscarExistencia(p, uDestino);
        } catch (NoEncontradoException e) {
            exDestino = new Existencia(p, uDestino, 0);
            club.agregarExistencia(exDestino);
        }

        exOrigen.descontar(cantidad);
        exDestino.sumar(cantidad);
    }
    
    
}
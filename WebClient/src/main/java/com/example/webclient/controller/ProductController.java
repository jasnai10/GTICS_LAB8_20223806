package com.example.webclient.controller;

import com.example.webclient.dao.ProductDao;
import com.example.webclient.dto.ProductDTO;
import com.example.webclient.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/productos")
public class ProductController {
    @Autowired
    ProductDao productDao;

    @GetMapping(value = "")
    public String listarYBuscarProductos(Model model,
                                         @RequestParam(name = "id", required = false) String idStr){

        // Para mostrar el catalogo de inventario de productos
        try {
            List<Product> listaProductos = productDao.listarProductos();
            model.addAttribute("lista", listaProductos);
        } catch (Exception e) {
            model.addAttribute("errorLista", "Error al cargar la lista de productos: " + e.getMessage());
        }

        // Para mostrar los detalles del producto
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                ProductDTO prod = productDao.obtenerProductoById(id);

                if (prod.getEstado() != null && prod.getEstado().equals("success")) {
                    model.addAttribute("productoEncontrado", prod.getProducto());
                } else {
                    model.addAttribute("errorBusqueda", prod.getMsg());
                }

            } catch (NumberFormatException e) {
                model.addAttribute("errorBusqueda", "El ID debe ser un número entero");

            } catch (Exception e) {
                model.addAttribute("errorBusqueda", "Error al realizar la búsqueda: " + e.getMessage());
            }
        }

        return "lista";
    }
}

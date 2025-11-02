package com.example.apiserver.controller;

import com.example.apiserver.entity.Product;
import com.example.apiserver.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    // Implementación del método GET
    @GetMapping(value = "")
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    // Implementación del método GET buscar por id
    @GetMapping(value = "/{id}")
    public ResponseEntity<HashMap<String, Object>> getProductById(@PathVariable("id") String idStr){

        HashMap<String, Object> response = new HashMap<>();
        try {
            int id = Integer.parseInt(idStr);
            Optional<Product> producto =  productRepository.findById(id);

            if (producto.isPresent()){
                response.put("estado", "success");
                response.put("producto", producto.get());
                return ResponseEntity.ok(response);
            }
            else {
                response.put("msg", "producto no encontrado");
            }
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            response.put("msg", "el ID debe ser un valor entero");
        }
        return ResponseEntity.badRequest().body(response);
    }

    // Implementación del método crear producto
    @PostMapping(value = "")
    public ResponseEntity<HashMap<String, Object>> addProduct(
            @RequestBody Product product,
            @RequestParam(value="fetchId", required = false) boolean fetchId){

        HashMap<String, Object> response = new HashMap<>();

        productRepository.save(product);

        if (fetchId){
            response.put("id", product.getId());
        }
        response.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Si no se envía ningún producto, entonces lo gestionamos con un exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, Object>> handleHttpMessageNotReadableException(HttpServletRequest request){

        HashMap<String, Object> response = new HashMap<>();

        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            response.put("estado", "error");
            response.put("msg", "Debe enviar un producto");
        }

        return ResponseEntity.badRequest().body(response);
    }

    // Actualización del producto
    @PutMapping(value = "")
    public ResponseEntity<HashMap<String, Object>> updateProduct(@RequestBody Product product){
        HashMap<String, Object> response = new HashMap<>();

        if (product.getId() != null && product.getId() > 0){
            Optional<Product> producto = productRepository.findById(product.getId());
            if (producto.isPresent()){
                // Vamos a comparar con el producto que está en la base de datos
                Product productDb = producto.get();

                // Empezamos a validar
                if(product.getName() != null){
                    productDb.setName(product.getName());
                }

                if(product.getUnit() != null){
                    productDb.setUnit(product.getUnit());
                }

                if(product.getPrice() != null){
                    productDb.setPrice(product.getPrice());
                }

                if(product.getCategory() != null){
                    productDb.setCategory(product.getCategory());
                }

                if(product.getSupplier() != null){
                    productDb.setSupplier(product.getSupplier());
                }

                productRepository.save(productDb);

                response.put("estado", "actualizado");
                return ResponseEntity.ok(response);
            }
            else {
                response.put("estado", "error");
                response.put("msg", "producto no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
        }
        else{
            response.put("estado", "error");
            response.put("msg", "Tiene que enviar un ID del producto");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Borrar id de producto
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HashMap<String, Object>> deleteProduct(@PathVariable("id") String idStr){
        HashMap<String, Object> response = new HashMap<>();

        try{
            int id = Integer.parseInt(idStr);
            Optional<Product> producto =  productRepository.findById(id);
            if(producto.isPresent()){
                productRepository.deleteById(id);
                response.put("estado", "eliminado exitosamente");
                return ResponseEntity.ok(response);
            }
            else{
                response.put("estado", "error");
                response.put("msg", "No se ha encontrado el producto");
                return ResponseEntity.badRequest().body(response);
            }
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            response.put("msg", "el ID debe ser un valor entero");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

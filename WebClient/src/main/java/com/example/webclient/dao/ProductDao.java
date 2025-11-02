package com.example.webclient.dao;

import com.example.webclient.dto.ProductDTO;
import com.example.webclient.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class ProductDao {

    private static final String API_URL = "http://localhost:8080/product";

    public List<Product> listarProductos(){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Product[]> response = restTemplate.getForEntity(API_URL, Product[].class);
        return Arrays.asList(response.getBody());
    }

    public ProductDTO obtenerProductoById(int id){

        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + "/" + id;
        ProductDTO productDto = null; // Mueve la declaración aquí

        try {
            // Caso de Éxito: HTTP 200
            ResponseEntity<ProductDTO> response = restTemplate.getForEntity(url, ProductDTO.class);
            productDto = response.getBody();
            return productDto;

        } catch (HttpClientErrorException.BadRequest e) {
            productDto = new ProductDTO();
            productDto.setEstado("failure");

            try {
                String errorBody = e.getResponseBodyAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String, String> errorMap = objectMapper.readValue(errorBody, HashMap.class);
                productDto.setMsg(errorMap.get("msg"));

            } catch (JsonProcessingException jsonEx) {
                productDto.setMsg("Error 400: Solicitud incorrecta.");
            }
            return productDto;

        } catch (Exception e) {
            productDto = new ProductDTO();
            productDto.setEstado("failure");
            productDto.setMsg("Error de conexión con la API: " + e.getMessage());
            return productDto;
        }
    }
}

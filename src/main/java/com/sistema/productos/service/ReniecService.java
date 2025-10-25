package com.sistema.productos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReniecService {

    @Value("${reniec.api.url.dni}")
    private String reniecApiUrl;
    
    @Value("${reniec.api.url.ruc}")
    private String rucApiUrl;

    @Value("${reniec.api.token}")
    private String reniecApiToken;

    @SuppressWarnings("UseSpecificCatch")
    public Map<String, Object> buscarPorDni(String dni) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + reniecApiToken);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = reniecApiUrl + dni;

            ResponseEntity<Map> result = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class
            );

            response.put("success", true);
            response.put("data", result.getBody());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al conectar con la API de DNI");
            response.put("error", e.getMessage());
        }
        return response;
    }

    @SuppressWarnings("UseSpecificCatch")
    public Map<String, Object> buscarPorRuc(String ruc) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + reniecApiToken);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = rucApiUrl + ruc; 

            ResponseEntity<Map> result = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class
            );

            response.put("success", true);
            response.put("data", result.getBody());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al conectar con la API de RUC");
            response.put("error", e.getMessage());
        }
        return response;
    }
}

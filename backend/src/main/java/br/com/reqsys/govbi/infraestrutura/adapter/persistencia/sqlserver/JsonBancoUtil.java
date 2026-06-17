package br.com.reqsys.govbi.infraestrutura.adapter.persistencia.sqlserver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

public final class JsonBancoUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private JsonBancoUtil() {}

    public static String toJson(Object valor) {
        try {
            return MAPPER.writeValueAsString(valor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Falha ao serializar JSON operacional", e);
        }
    }

    public static Map<String, Object> toMap(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Falha ao desserializar mapa JSON operacional", e);
        }
    }

    public static List<String> toStringList(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Falha ao desserializar lista JSON operacional", e);
        }
    }
    public static List<Map<String, Object>> toListMap(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Falha ao desserializar lista de mapas JSON operacional", e);
        }
    }

}

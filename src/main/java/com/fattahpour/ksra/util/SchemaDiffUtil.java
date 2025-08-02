package com.fattahpour.ksra.util;

import com.fattahpour.ksra.model.SchemaDiff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility methods for computing diffs between two Avro schema definitions.
 * <p>
 * This implementation performs a shallow comparison of field names and
 * field definitions within the top-level {@code fields} array.
 * It is intended as a starting point for more advanced diff logic.
 */
public final class SchemaDiffUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SchemaDiffUtil() {
    }

    public static SchemaDiff diff(String schema1, String schema2) throws JsonProcessingException {
        JsonNode node1 = MAPPER.readTree(schema1);
        JsonNode node2 = MAPPER.readTree(schema2);

        Map<String, JsonNode> fields1 = mapFields(node1);
        Map<String, JsonNode> fields2 = mapFields(node2);

        Set<String> names1 = fields1.keySet();
        Set<String> names2 = fields2.keySet();

        List<String> added = names2.stream()
                .filter(n -> !names1.contains(n))
                .sorted()
                .collect(Collectors.toList());

        List<String> removed = names1.stream()
                .filter(n -> !names2.contains(n))
                .sorted()
                .collect(Collectors.toList());

        List<String> changed = names1.stream()
                .filter(names2::contains)
                .filter(n -> !Objects.equals(fields1.get(n), fields2.get(n)))
                .sorted()
                .collect(Collectors.toList());

        return new SchemaDiff(added, removed, changed);
    }

    private static Map<String, JsonNode> mapFields(JsonNode node) {
        Map<String, JsonNode> map = new HashMap<>();
        JsonNode fields = node.get("fields");
        if (fields != null && fields.isArray()) {
            for (JsonNode field : fields) {
                JsonNode nameNode = field.get("name");
                if (nameNode != null) {
                    map.put(nameNode.asText(), field);
                }
            }
        }
        return map;
    }
}

package com.medhir.rest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GeneratedId {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Generates a new ID based on the prefix and collection name
     * @param prefix The prefix for the ID (e.g., "DES" for Designation, "DEPT" for Department)
     * @param collectionName The name of the MongoDB collection
     * @param idFieldName The name of the ID field in the collection
     * @return A new unique ID with the format {prefix}{number}
     */
    public String generateId(String prefix, String collectionName, String idFieldName) {
        // Find all documents with IDs matching the prefix pattern
        Query query = new Query();
        query.addCriteria(Criteria.where(idFieldName).regex("^" + prefix + "\\d+$"));

        // Get all matching documents
        List<Object> documents = mongoTemplate.find(query, Object.class, collectionName);

        // Find the highest numeric ID
        int highestNumber = 100;
        Pattern pattern = Pattern.compile("^" + prefix + "(\\d+)$");

        for (Object doc : documents) {
            try {
                Field idField = doc.getClass().getDeclaredField(idFieldName);
                idField.setAccessible(true);
                String id = (String) idField.get(doc);

                Matcher matcher = pattern.matcher(id);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    if (number > highestNumber) {
                        highestNumber = number;
                    }
                }
            } catch (Exception e) {
                // Skip documents with invalid IDs
            }
        }

        // Generate new ID with the next number
        return prefix + (highestNumber + 1);
    }

    /**
     * Generates a new ID for a specific model class
     * @param prefix The prefix for the ID (e.g., "DES" for Designation, "DEPT" for Department)
     * @param modelClass The model class to generate ID for
     * @param idFieldName The name of the ID field in the model
     * @return A new unique ID with the format {prefix}{number}
     */
    public <T> String generateId(String prefix, Class<T> modelClass, String idFieldName) {
        String collectionName = modelClass.getAnnotation(Document.class).collection();
        return generateId(prefix, collectionName, idFieldName);
    }
}
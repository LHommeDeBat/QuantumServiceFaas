package de.unistuttgart.iaas.faas.quantumservice.model.entity.job;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.json.JSONException;
import org.json.JSONObject;

@Converter
public class JobResultConverter implements AttributeConverter<JSONObject, String> {
    @Override
    public String convertToDatabaseColumn(JSONObject attribute) {
        String json;
        try {
            json = attribute.toString();
        } catch (NullPointerException ex) {
            json = "";
        }
        return json;
    }

    @Override
    public JSONObject convertToEntityAttribute(String dbData) {
        JSONObject jsonData;
        try {
            jsonData = new JSONObject(dbData);
        } catch (JSONException ex) {
            jsonData = null;
        }
        return jsonData;
    }
}

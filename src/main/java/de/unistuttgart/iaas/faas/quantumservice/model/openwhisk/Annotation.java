
package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "key",
    "value"
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Annotation {
    private String key;
    private Object value;
}


package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "kind",
    "code",
    "image"
})
@Data
public class Exec {
    private String kind;
    private String code;
    private String image;
}

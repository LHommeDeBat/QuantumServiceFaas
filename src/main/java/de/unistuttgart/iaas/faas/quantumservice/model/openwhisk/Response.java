
package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import lombok.Data;

@Data
public class Response {

    private String status;
    private Integer statusCode;
    private Boolean success;
    private Object result;
}

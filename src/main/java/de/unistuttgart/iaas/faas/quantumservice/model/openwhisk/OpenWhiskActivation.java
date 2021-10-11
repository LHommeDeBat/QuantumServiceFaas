
package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OpenWhiskActivation {

    private String namespace;
    private String name;
    private String version;
    private String subject;
    private String activationId;
    private Long start;
    private Long end;
    private Long duration;
    private Integer statusCode;
    private Response response;
    private List<String> logs = new ArrayList<>();
    private List<Annotation> annotations = new ArrayList<>();
    private Boolean publish;
}

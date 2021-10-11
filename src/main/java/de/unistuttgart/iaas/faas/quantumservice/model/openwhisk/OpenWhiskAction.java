
package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OpenWhiskAction {

    private String namespace;
    private String name;
    private String version;
    private Boolean publish;
    private Exec exec;
    private List<Annotation> annotations = new ArrayList<>();

    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }
}

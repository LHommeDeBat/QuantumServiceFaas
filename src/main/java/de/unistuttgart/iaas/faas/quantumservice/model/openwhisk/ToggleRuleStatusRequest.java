package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToggleRuleStatusRequest {

    private String status;
}

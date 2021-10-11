package de.unistuttgart.iaas.faas.quantumservice.utils;

import de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger.ExecutionResultEventTriggerDto;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger.QueueSizeEventTriggerDto;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger.EventTriggerDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.ExecutionResultEventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.QueueSizeEventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import org.modelmapper.ModelMapper;

/**
 * This Utility-Class allows the translation between different objects.
 * It is usually used to transform Entity-Objects to DTO-Objects or vice versa.
 */
public class ModelMapperUtils {

    public static final ModelMapper mapper = initModelMapper();

    public static <D, T> D convert(final T entity, Class<D> outClass) {
        return mapper.map(entity, outClass);
    }

    private static void initializeConverters(ModelMapper mapper) {
        mapper.createTypeMap(QueueSizeEventTrigger.class, EventTriggerDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QueueSizeEventTriggerDto.class));
        mapper.createTypeMap(ExecutionResultEventTrigger.class, EventTriggerDto.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ExecutionResultEventTriggerDto.class));
        mapper.createTypeMap(QueueSizeEventTriggerDto.class, EventTrigger.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), QueueSizeEventTrigger.class));
        mapper.createTypeMap(ExecutionResultEventTriggerDto.class, EventTrigger.class)
                .setConverter(mappingContext -> mapper.map(mappingContext.getSource(), ExecutionResultEventTrigger.class));
    }

    private static ModelMapper initModelMapper() {
        ModelMapper mapper = new ModelMapper();
        initializeConverters(mapper);
        return mapper;
    }
}

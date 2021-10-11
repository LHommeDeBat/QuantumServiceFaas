package de.unistuttgart.iaas.faas.quantumservice.hateoas;

import java.util.Collection;
import java.util.stream.Collectors;

import de.unistuttgart.iaas.faas.quantumservice.utils.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

/**
 * This generic class is responsible for transforming Entity-Objects into DTO-Objects.
 * Hateoas-Links are added to the transformed DTO-Objects afterwards.
 */
public abstract class GenericLinkAssembler<T> {

    @Autowired
    private PagedResourcesAssembler<T> pagedResourcesAssembler;

    public abstract void addLinks(EntityModel<T> resource);

    public void addLinks(CollectionModel<EntityModel<T>> resources) {
        for (EntityModel<T> entity : resources.getContent()) {
            addLinks(entity);
        }
    }

    public T getContent(EntityModel<T> resource) {
        return resource.getContent();
    }

    public <U> EntityModel<T> toModel(U entity, Class<T> entityClass) {
        EntityModel<T> entityModel = EntityModel.of(ModelMapperUtils.convert(entity, entityClass));
        addLinks(entityModel);
        return entityModel;
    }

    public <U> CollectionModel<EntityModel<T>> toModel(Collection<U> collection, Class<T> entityClass) {
        final var entities = collection.stream().map(item ->
                EntityModel.of(ModelMapperUtils.convert(item, entityClass))).collect(Collectors.toUnmodifiableList());
        final var collectionModel = CollectionModel.of(entities);
        addLinks(collectionModel);
        return collectionModel;
    }

    /**
     * This method converts an Paged-Model to some other type (usually Entity -> DTO). It also adds HATEOAS-Links to
     * the target objects.
     *
     * @param page
     * @param entityClass
     * @param <U>
     * @return
     */
    public <U> PagedModel<EntityModel<T>> toModel(Page<U> page, Class<T> entityClass) {
        final var entities = page.map(item -> ModelMapperUtils.convert(item, entityClass));
        final var model = pagedResourcesAssembler.toModel(entities);

        for (EntityModel<T> entity: model.getContent()) {
            addLinks(entity);
        }

        return model;
    }
}

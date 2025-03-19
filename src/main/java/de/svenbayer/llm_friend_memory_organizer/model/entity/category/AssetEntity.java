package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Asset")
@EqualsAndHashCode(callSuper = true)
public class AssetEntity extends CategoryEntity {

    public AssetEntity() {
        super("Asset");
    }
}

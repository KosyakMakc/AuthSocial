package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByUUID.TABLE_NAME)
public class AssociationByUUID extends Association<UUID> {
    public static final String TABLE_NAME = "authsocial_association_uuid";

    public AssociationByUUID() {

    }

    public AssociationByUUID(UUID minecraftId, UUID socialPlatformId, UUID socialuserId) {
        super(minecraftId, socialPlatformId, socialuserId);
    }
}

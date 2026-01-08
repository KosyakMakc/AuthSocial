package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByLong.TABLE_NAME)
public class AssociationByLong extends Association<Long> {
    public static final String TABLE_NAME = "authsocial_association_long";

    public AssociationByLong() {

    }

    public AssociationByLong(UUID minecraftId, UUID socialPlatformId, Long socialuserId) {
        super(minecraftId, socialPlatformId, socialuserId);
    }
}

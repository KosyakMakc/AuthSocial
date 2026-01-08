package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByString.TABLE_NAME)
public class AssociationByString extends Association<String> {
    public static final String TABLE_NAME = "authsocial_association_string";

    public AssociationByString() {

    }

    public AssociationByString(UUID minecraftId, UUID socialPlatformId, String socialuserId) {
        super(minecraftId, socialPlatformId, socialuserId);
    }
}

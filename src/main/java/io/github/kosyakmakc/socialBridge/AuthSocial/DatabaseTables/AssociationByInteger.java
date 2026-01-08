package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByInteger.TABLE_NAME)
public class AssociationByInteger extends Association<Integer> {
    public static final String TABLE_NAME = "authsocial_association_integer";

    public AssociationByInteger() {

    }

    public AssociationByInteger(UUID minecraftId, UUID socialPlatformId, Integer socialuserId) {
        super(minecraftId, socialPlatformId, socialuserId);
    }
}

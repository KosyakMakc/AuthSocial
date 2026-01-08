package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByUUID.TABLE_NAME)
public class AssociationByUUID extends Association {
    public static final String TABLE_NAME = "authsocial_association_uuid";

    @DatabaseField(columnName = SOCIAL_USER_ID_FIELD_NAME, index = true)
    private UUID socialUserId;

    public AssociationByUUID() {

    }

    public AssociationByUUID(UUID minecraftId, UUID socialPlatformId, UUID socialUserId) {
        super(minecraftId, socialPlatformId);
        this.socialUserId = socialUserId;
    }

    @Override
    public UUID getSocialUserId() {
        return socialUserId;
    }
}

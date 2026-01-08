package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByLong.TABLE_NAME)
public class AssociationByLong extends Association {
    public static final String TABLE_NAME = "authsocial_association_long";

    @DatabaseField(columnName = SOCIAL_USER_ID_FIELD_NAME, index = true)
    private Long socialUserId;

    public AssociationByLong() {

    }

    public AssociationByLong(UUID minecraftId, UUID socialPlatformId, Long socialUserId) {
        super(minecraftId, socialPlatformId);
        this.socialUserId = socialUserId;
    }

    @Override
    public Long getSocialUserId() {
        return socialUserId;
    }
}

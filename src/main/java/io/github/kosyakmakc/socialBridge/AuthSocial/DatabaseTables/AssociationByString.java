package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByString.TABLE_NAME)
public class AssociationByString extends Association {
    public static final String TABLE_NAME = "authsocial_association_string";

    @DatabaseField(columnName = SOCIAL_USER_ID_FIELD_NAME, index = true)
    private String socialUserId;

    public AssociationByString() {

    }

    public AssociationByString(UUID minecraftId, UUID socialPlatformId, String socialUserId) {
        super(minecraftId, socialPlatformId);
        this.socialUserId = socialUserId;
    }

    @Override
    public String getSocialUserId() {
        return socialUserId;
    }
}

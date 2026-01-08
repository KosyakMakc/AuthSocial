package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = AssociationByInteger.TABLE_NAME)
public class AssociationByInteger extends Association {
    public static final String TABLE_NAME = "authsocial_association_integer";

    @DatabaseField(columnName = SOCIAL_USER_ID_FIELD_NAME, index = true)
    private Integer socialUserId;

    public AssociationByInteger() {

    }

    public AssociationByInteger(UUID minecraftId, UUID socialPlatformId, Integer socialUserId) {
        super(minecraftId, socialPlatformId);
        this.socialUserId = socialUserId;
    }

    @Override
    public Integer getSocialUserId() {
        return socialUserId;
    }
}

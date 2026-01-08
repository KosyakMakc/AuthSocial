package io.github.kosyakmakc.socialBridge.AuthSocial.DatabaseTables;

import com.j256.ormlite.field.DatabaseField;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.IDatabaseTable;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public abstract class Association implements IDatabaseTable {
    public static final String ID_FIELD_NAME = "id";
    public static final String MINECRAFT_ID_FIELD_NAME = "minecraft_id";
    public static final String SOCIAL_PLATFORM_ID_FIELD_NAME = "social_platform_id";
    public static final String SOCIAL_USER_ID_FIELD_NAME = "social_user_id";
    public static final String IS_DELETED_FIELD_NAME = "is_deleted";
    public static final String CREATED_AT_FIELD_NAME = "created_at";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int id;

    @DatabaseField(columnName = MINECRAFT_ID_FIELD_NAME, index = true)
    private UUID minecraftId;

    @DatabaseField(columnName = SOCIAL_PLATFORM_ID_FIELD_NAME, index = true)
    private UUID socialPlatformId;

    @DatabaseField(columnName = IS_DELETED_FIELD_NAME, index = true)
    private boolean isDeleted;

    @DatabaseField(columnName = CREATED_AT_FIELD_NAME)
    private Date createdAt;

    public Association() {

    }

    public Association(UUID minecraftId, UUID socialPlatformId) {
        this.minecraftId = minecraftId;
        this.socialPlatformId = socialPlatformId;
        this.isDeleted = false;

        var now = Instant.now();
        this.createdAt = Date.from(now);
    }

    public UUID getMinecraftId() {
        return this.minecraftId;
    }

    public UUID getSocialPlatformId() {
        return socialPlatformId;
    }

    public abstract Object getSocialUserId();

    public boolean isDeleted() {
        return isDeleted;
    }

    public void Delete() {
        this.isDeleted = true;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}

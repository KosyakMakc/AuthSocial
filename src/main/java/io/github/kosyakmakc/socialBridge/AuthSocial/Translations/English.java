package io.github.kosyakmakc.socialBridge.AuthSocial.Translations;

import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.ITranslationSource;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.LocalizationRecord;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.LocalizationService;

import java.util.List;

public class English implements ITranslationSource {
    @Override
    public String getLanguage() {
        return LocalizationService.defaultLocale;
    }

    @Override
    public List<LocalizationRecord> getRecords() {
        return List.of(
            new LocalizationRecord(AuthMessageKey.LOGIN_FROM_MINECRAFT_DESCRIPTION.key(), "Start new auth session and generate random 6-digit code."),
            new LocalizationRecord(AuthMessageKey.LOGIN_FROM_MINECRAFT.key(), "Your authorization code - <placeholder-code>. Please continue operation in social platform."),
            new LocalizationRecord(AuthMessageKey.COMMITED_LOGIN.key(), "You are <dark_green>successfully</dark_green> connected with account <gold><social-user-name></gold> of social <aqua><social-platform-name></aqua> platform."),

            new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_DESCRIPTION.key(), "Get list of connected yours social platforms."),
            new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_HEADER.key(), "You have active social sessions in:"),
            new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_RECORD.key(), "- <aqua><social-platform-name></aqua>(<gold><social-user-name></gold>)"),
            new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_EMPTY.key(), "No any active session."),

            new LocalizationRecord(AuthMessageKey.COMMITED_LOGIN_DESCRIPTION.key(), "Connect your social account with minecraft. Please provide also 6-digit auth code."),
            new LocalizationRecord(AuthMessageKey.UNSUPPORTED_PLATFORM.key(), "Sorry, but this platform is unsupported."),
            new LocalizationRecord(AuthMessageKey.SOCIAL_COMMITED_LOGIN.key(), "You are successfully connected."),
            new LocalizationRecord(AuthMessageKey.YOU_ARE_ALREADY_AUTHORIZED.key(), "You are already authorized on this platform."),
            new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_FAILED.key(), "Unable confirm authorization with this code."),

            new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_DESCRIPTION.key(), "Disconnect social platform from your Minecraft account."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_PLATFORM.key(), "<red>Not found social platform with this name."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_SOCIAL_USER.key(), "<red>Отсутствует связь вашего Minecraft аккаунта с социальной сетью <aqua><social-platform-name></aqua>."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_FAILED.key(), "<red>Unable to disconnect social platform from your Minecraft account, please try again later or contact with administrator."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_SUCCESS.key(), "<dark_green>success disconnecting social platform <aqua><social-platform-name></aqua>."),

            new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_DESCRIPTION.key(), "Check Minecraft nickname."),
            new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NO_REPLY.key(), "You forget about reply message of user, which nickname you want to check."),
            new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NOT_FOUND.key(), "This user doesn't have linked Minecraft account."),
            new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_SUCCESS.key(), "Minecraft nick: <reply-minecraft-user-name>."),

            new LocalizationRecord(AuthMessageKey.LOGOUT_DESCRIPTION.key(), "Disconnect current social platform from your minecraft account."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS.key(), "You(<social-user-name>) are successfully logout from profile(<minecraft-user-name>) on this platform."), // also available <social-platform-name>
            new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS_MINECRAFT.key(), "Your <aqua><social-platform-name></aqua> social account <gold><social-user-name></gold> has been disconnected from your gaming profile."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_FAILED.key(), "You(<social-user-name>) unable to logout - not authenticated."), // also available <social-platform-name>

                new LocalizationRecord(AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_LOGIN.key(), "Unable to execute command, you are not authorized."),
                new LocalizationRecord(AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_PERMISSION.key(), "Unable to execute command, no permissions.")
        );
    }
}

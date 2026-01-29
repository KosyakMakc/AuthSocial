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

            new LocalizationRecord(AuthMessageKey.LOGOUT_DESCRIPTION.key(), "Disconnect current social platform from your minecraft account."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS.key(), "You(<social-user-name>) are successfully logout from profile(<minecraft-user-name>) on this platform."), // also available <social-platform-name>
            new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS_MINECRAFT.key(), "Your <aqua><social-platform-name></aqua> social account <orange><social-user-name></orange> has been disconnected from your gaming profile."),
            new LocalizationRecord(AuthMessageKey.LOGOUT_FAILED.key(), "You(<social-user-name>) unable to logout - not authenticated.") // also available <social-platform-name>
        );
    }
}

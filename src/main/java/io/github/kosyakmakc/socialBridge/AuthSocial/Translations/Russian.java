package io.github.kosyakmakc.socialBridge.AuthSocial.Translations;

import java.util.List;

import io.github.kosyakmakc.socialBridge.AuthSocial.Utils.AuthMessageKey;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.ITranslationSource;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.LocalizationRecord;

public class Russian implements ITranslationSource {
    @Override
    public String getLanguage() {
        return "ru";
    }

    @Override
    public List<LocalizationRecord> getRecords() {
        return List.of(
                new LocalizationRecord(AuthMessageKey.LOGIN_FROM_MINECRAFT_DESCRIPTION.key(), "Начать новую сессию авторизации и сгенерировать случайный 6-значный код."),
                new LocalizationRecord(AuthMessageKey.LOGIN_FROM_MINECRAFT.key(), "Ваш код для авторизации - <placeholder-code>. Пожалуйста продолжите операцию в социальной сети."),
                new LocalizationRecord(AuthMessageKey.COMMITED_LOGIN.key(), "Вы были <dark_green>успешно</dark_green> подключены к аккаунту <gold><social-user-name></gold> социальной сети <aqua><social-platform-name></aqua>."),

                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_DESCRIPTION.key(), "Получить список подключенных социальных сетей."),
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_HEADER.key(), "Вы имеете активные сессии в:"),
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_RECORD.key(), "- <aqua><social-platform-name></aqua>(<gold><social-user-name></gold>)"),
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_EMPTY.key(), "Нет активных сессий."),

                new LocalizationRecord(AuthMessageKey.COMMITED_LOGIN_DESCRIPTION.key(), "Подключить ваш аккаунт к игровому профилю Minecraft. Пожалуйста также укажите 6-значный код авторизации."),
                new LocalizationRecord(AuthMessageKey.UNSUPPORTED_PLATFORM.key(), "К сожалению эта платформа не поддерживается ботом."),
                new LocalizationRecord(AuthMessageKey.SOCIAL_COMMITED_LOGIN.key(), "Вы были успешно подключены к игровому профилю."),
                new LocalizationRecord(AuthMessageKey.YOU_ARE_ALREADY_AUTHORIZED.key(), "Вы уже авторизованы."),
                new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_FAILED.key(), "Неудалось подтвердить авторизацию с этим кодом."),

                new LocalizationRecord(AuthMessageKey.LOGOUT_DESCRIPTION.key(), "Отключить текущий социальный аккаунт от игрового профиля."),
                new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS.key(), "Вы(<social-user-name>) были успешно отключены от игрового профиля(<minecraft-user-name>) на этой социальной платформе."), // also available <social-platform-name>
                new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS_MINECRAFT.key(), "От вашего игрового профиля был отключен аккаунт <gold><social-user-name></gold> социальной сети <aqua><social-platform-name></aqua>."), // also available <social-platform-name>
                new LocalizationRecord(AuthMessageKey.LOGOUT_FAILED.key(), "Вы(<social-user-name>) не можете отключить игровой профиль - отсутствует авторизация.") // also available <social-platform-name>
        );
    }
}

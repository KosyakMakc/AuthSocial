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
                
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_DESCRIPTION.key(), "Получить список подключенных социальных сетей."),
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_HEADER.key(), "Вы имеете активные сессии в:"),
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_RECORD.key(), "- <aqua><social-platform-name></aqua>(<gold><social-user-name></gold>)"),
                new LocalizationRecord(AuthMessageKey.STATUS_COMMAND_EMPTY.key(), "Нет активных сессий."),
                
                new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_DESCRIPTION.key(), "Подключить аккаунт к Minecraft c 6-значным кодом."),
                new LocalizationRecord(AuthMessageKey.UNSUPPORTED_PLATFORM.key(), "К сожалению эта платформа не поддерживается ботом."),
                new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_MINECRAFT_SUCCESS.key(), "Вы были <dark_green>успешно</dark_green> подключены к аккаунту <gold><social-user-name></gold> социальной сети <aqua><social-platform-name></aqua>."),
                new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_SOCIAL_SUCCESS.key(), "Вы были успешно подключены к игровому профилю."),
                new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_ALREADY_LOGGED.key(), "Вы уже авторизованы."),
                new LocalizationRecord(AuthMessageKey.COMMIT_LOGIN_FAILED.key(), "Не удалось подтвердить авторизацию с этим кодом."),

                new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_DESCRIPTION.key(), "Отключить социальную сеть от вашего Minecraft аккаунта."),
                new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_PLATFORM.key(), "<red>Неизвестная социальная сеть."),
                new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_SOCIAL_USER.key(), "<red>Отсутствует связь вашего Minecraft аккаунта с социальной сетью <aqua><social-platform-name></aqua>."),
                new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_FAILED.key(), "<red>Не удалось отключить социальную сеть от вашего Minecraft аккаунта, попробуйте позже или свяжитесь с администрацией."),
                new LocalizationRecord(AuthMessageKey.LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_SUCCESS.key(), "<dark_green>Успешно отключена социальная сеть <aqua><social-platform-name></aqua>."),

                new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_DESCRIPTION.key(), "Узнать игровой ник."),
                new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NO_REPLY.key(), "Вы забыли выделить сообщение пользователя, ник которого вы хотите узнать."),
                new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_NOT_FOUND.key(), "У этого пользователя нет связанного Minecraft аккаунта."),
                new LocalizationRecord(AuthMessageKey.SOCIAL_USER_INFO_COMMAND_SUCCESS.key(), "Игровой ник: <reply-minecraft-user-name>."),

                new LocalizationRecord(AuthMessageKey.LOGOUT_DESCRIPTION.key(), "Отключить текущий социальный аккаунт от игрового профиля."),
                new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS.key(), "Вы(<social-user-name>) были успешно отключены от игрового профиля(<minecraft-user-name>) на этой социальной платформе."), // also available <social-platform-name>
                new LocalizationRecord(AuthMessageKey.LOGOUT_SUCCESS_MINECRAFT.key(), "От вашего игрового профиля был отключен аккаунт <gold><social-user-name></gold> социальной сети <aqua><social-platform-name></aqua>."), // also available <social-platform-name>
                new LocalizationRecord(AuthMessageKey.LOGOUT_FAILED.key(), "Вы(<social-user-name>) не можете отключить игровой профиль - отсутствует авторизация."), // also available <social-platform-name>

                new LocalizationRecord(AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_LOGIN.key(), "Не удалось выполнить команду, вы не авторизованы."),
                new LocalizationRecord(AuthMessageKey.AUTHSOCIAL_BASE_COMMAND_NO_PERMISSION.key(), "Не удалось выполнить команду, недостаточно прав.")
            );
    }
}

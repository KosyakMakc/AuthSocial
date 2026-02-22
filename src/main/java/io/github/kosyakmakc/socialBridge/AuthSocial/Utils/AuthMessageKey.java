package io.github.kosyakmakc.socialBridge.AuthSocial.Utils;

import io.github.kosyakmakc.socialBridge.AuthSocial.AuthModule;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public class AuthMessageKey {
    public static final MessageKey UNSUPPORTED_PLATFORM = new MessageKey(AuthModule.ID, "unsupported_platform");

    public static final MessageKey LOGIN_FROM_MINECRAFT_DESCRIPTION = new MessageKey(AuthModule.ID, "login_from_minecraft_description");
    public static final MessageKey LOGIN_FROM_MINECRAFT = new MessageKey(AuthModule.ID, "login_from_minecraft");

    public static final MessageKey STATUS_COMMAND_DESCRIPTION = new MessageKey(AuthModule.ID, "status_command_description");
    public static final MessageKey STATUS_COMMAND_HEADER = new MessageKey(AuthModule.ID, "status_command_header");
    public static final MessageKey STATUS_COMMAND_RECORD = new MessageKey(AuthModule.ID, "status_command_record");
    public static final MessageKey STATUS_COMMAND_EMPTY = new MessageKey(AuthModule.ID, "status_command_empty");

    public static final MessageKey COMMIT_LOGIN_DESCRIPTION = new MessageKey(AuthModule.ID, "commit_login_description");
    public static final MessageKey COMMIT_LOGIN_MINECRAFT_SUCCESS = new MessageKey(AuthModule.ID, "commit_login_minecraft_success");
    public static final MessageKey COMMIT_LOGIN_SOCIAL_SUCCESS = new MessageKey(AuthModule.ID, "commit_login_social_success");
    public static final MessageKey COMMIT_LOGIN_ALREADY_LOGGED = new MessageKey(AuthModule.ID, "commit_login_already_logged");
    public static final MessageKey COMMIT_LOGIN_FAILED = new MessageKey(AuthModule.ID, "commit_login_failed");

    public static final MessageKey LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_DESCRIPTION = new MessageKey(AuthModule.ID, "logout_specific_social_platform_command_description");
    public static final MessageKey LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_PLATFORM = new MessageKey(AuthModule.ID, "logout_specific_social_platform_command_not_found_platform");
    public static final MessageKey LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_NOT_FOUND_SOCIAL_USER = new MessageKey(AuthModule.ID, "logout_specific_social_platform_command_not_found_social_user");
    public static final MessageKey LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_FAILED = new MessageKey(AuthModule.ID, "logout_specific_social_platform_command_failed");
    public static final MessageKey LOGOUT_SPECIFIC_SOCIAL_PLATFORM_COMMAND_SUCCESS = new MessageKey(AuthModule.ID, "logout_specific_social_platform_command_success");

    public static final MessageKey SOCIAL_USER_INFO_COMMAND_DESCRIPTION = new MessageKey(AuthModule.ID, "social_user_info_command_description");
    public static final MessageKey SOCIAL_USER_INFO_COMMAND_NO_REPLY = new MessageKey(AuthModule.ID, "social_user_info_command_no_reply");
    public static final MessageKey SOCIAL_USER_INFO_COMMAND_NOT_FOUND = new MessageKey(AuthModule.ID, "social_user_info_command_not_found");
    public static final MessageKey SOCIAL_USER_INFO_COMMAND_SUCCESS = new MessageKey(AuthModule.ID, "social_user_info_command_success");

    public static final MessageKey LOGOUT_DESCRIPTION = new MessageKey(AuthModule.ID, "logout_description");
    public static final MessageKey LOGOUT_SUCCESS = new MessageKey(AuthModule.ID, "logout_success");
    public static final MessageKey LOGOUT_SUCCESS_MINECRAFT = new MessageKey(AuthModule.ID, "logout_success_minecraft");
    public static final MessageKey LOGOUT_FAILED = new MessageKey(AuthModule.ID, "logout_failed");

    public static final MessageKey AUTHSOCIAL_BASE_COMMAND_NO_LOGIN = new MessageKey(AuthModule.ID, "authsocial_base_command_no_login");
    public static final MessageKey AUTHSOCIAL_BASE_COMMAND_NO_PERMISSION = new MessageKey(AuthModule.ID, "authsocial_base_command_no_permission");
}

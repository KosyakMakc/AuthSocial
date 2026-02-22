# AuthSocial
## It is module for [SocialBridge](https://github.com/KosyakMakc/social-bridge) minecraft plugin

### this module provide commands for authorization processes

### Supported social platforms:

- [Telegram](https://github.com/KosyakMakc/social-bridge-telegram)

### Commands for minecraft:

| Command literal                  | Permission node   | Description                                                              |
|----------------------------------|-------------------|--------------------------------------------------------------------------|
| /authsocial login                | AuthSocial.login  | Creates a short-life session for auth with 6-digit code                  |
| /authsocial status               | AuthSocial.status | Provide information about all connected social platform for user(sender) |
| /authsocial logout {social-name} | AuthSocial.logout | Drop authorize with specific social platform from database               |

### Commands for social platforms:

| Command literal                 | Permission node          | Description                                                                                      |
|---------------------------------|--------------------------|--------------------------------------------------------------------------------------------------|
| /authsocial_login auth-code     | -                        | Authorize social user with minecraft player using AUTH_CODE, authorize will be saved to database |
| /authsocial_logout              | -                        | Drop authorize with minecraft player from database                                               |
| /authsocial_info [replyMessage] | AuthSocial.checkUserInfo | Show Minecraft nickname if exist of replied social user                                          |

## API for developers

### You can connect API of this module for your purposes
```
repositories {
    maven {
        name = "gitea"
        url = "https://git.kosyakmakc.ru/api/packages/kosyakmakc/maven"
    }
}
dependencies {
    compileOnly "io.github.kosyakmakc:AuthSocial:0.10.+"
}
```

### via `ISocialBridge.getModule(AuthModule.class)` you can access this module and use AuthSocial API
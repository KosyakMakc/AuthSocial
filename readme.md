# AuthSocial
## It is module for [SocialBridge](https://github.com/KosyakMakc/social-bridge) minecraft plugin

### this module provide commands for authorization processes

### Supported social platforms:

- [Telegram](https://github.com/KosyakMakc/social-bridge-telegram)

### Commands for minecraft:

| Command literal    | Permission node   | Description                                                              |
|--------------------|-------------------|--------------------------------------------------------------------------|
| /auth login        | AuthSocial.login  | Creates a short-life session for auth with 6-digit code                  |
| /auth status       | AuthSocial.status | Provide information about all connected social platform for user(sender) |

### Commands for social platforms:

| Command literal             | Description                                                                                      |
|-----------------------------|--------------------------------------------------------------------------------------------------|
| /auth_login   auth-code     | Authorize social user with minecraft player using AUTH_CODE, authorize will be saved to database |
| /auth_logout                | Drop authorize with minecraft player from database                                               |

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
    compileOnly "io.github.kosyakmakc:AuthSocial:0.5.1"
}
```

### via `ISocialBridge.getModule(AuthModule.class)` you can access this module and enumerate platform handlers `AuthModule.getSocialHandlers()`, where you can interview every supported platform to get\check authorization
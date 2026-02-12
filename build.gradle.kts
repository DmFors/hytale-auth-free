version = project.findProperty("pluginVersion") as String? ?: "1.0.0"

repositories {
}

dependencies {
    // Пропатченный сервер Hytale Dual Auth (https://github.com/sanasol/hytale-auth-server/tree/master/patcher)
    compileOnly(files("build/libs/HytaleServer.jar"))
}
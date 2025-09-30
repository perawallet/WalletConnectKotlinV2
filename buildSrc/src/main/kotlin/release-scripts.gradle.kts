import java.util.Locale

enum class ReleaseType { LOCAL }

tasks.register("releaseAllSDKs") {
    val type = (findProperty("type") as? String)
        ?.uppercase(Locale.getDefault())
        ?.let { ReleaseType.valueOf(it) }
        ?: error("Missing Type parameter. Use -Ptype=local")

    val taskPaths = compileListOfSDKs().map { (parent, child, env) ->
        val taskName = when {
            env == "jvm" && type == ReleaseType.LOCAL -> "publishReleasePublicationToMavenLocal"
            env == "android" && type == ReleaseType.LOCAL -> "publishReleasePublicationToMavenLocal"
            else -> error("Only LOCAL publishing is supported for JitPack.")
        }
        if (child != null) ":$parent:$child:$taskName" else ":$parent:$taskName"
    }

    dependsOn(taskPaths)
}

fun compileListOfSDKs(): List<Triple<String, String?, String>> = buildList {
    add(Triple("foundation", null, "jvm"))
    add(Triple("core", "android", "android"))
    add(Triple("core", "modal", "android"))
    add(Triple("protocol", "sign", "android"))
    add(Triple("protocol", "auth", "android"))
    add(Triple("protocol", "chat", "android"))
    add(Triple("protocol", "notify", "android"))
    add(Triple("product", "web3wallet", "android"))
    add(Triple("product", "web3modal", "android"))
    add(Triple("product", "walletconnectmodal", "android"))
    add(Triple("bom", null, "jvm"))
}
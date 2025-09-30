import org.apache.tools.ant.taskdefs.condition.Os
import java.util.Locale
import kotlin.reflect.full.safeCast

// Example ./gradlew releaseAllSDKs -Ptype=local
tasks.register("releaseAllSDKs") {
    doLast {
        project.findProperty("type")
            ?.run(String::class::safeCast)
            ?.run {
                println("Converting parameter to a supported ReleaseType value")
                ReleaseType.valueOf(this.uppercase(Locale.getDefault()))
            }?.let { releaseType ->
                generateListOfModuleTasks(releaseType).forEach { task ->
                    println("Executing Task: $task")
                    exec {
                        val gradleCommand = if (Os.isFamily(Os.FAMILY_WINDOWS)) {
                            "gradlew.bat"
                        } else {
                            "./gradlew"
                        }
                        commandLine(gradleCommand, task.path)
                    }
                }
            } ?: throw Exception("Missing Type parameter")
    }
}

fun generateListOfModuleTasks(type: ReleaseType): List<Task> = compileListOfSDKs().extractListOfPublishingTasks(type)

// Triple consists of the root module name, the child module name, and if it's a JVM or Android module
fun compileListOfSDKs(): List<Triple<String, String?, String>> = mutableListOf(
    Triple("foundation", null, "jvm"),
    Triple("core", "android", "android"),
    Triple("core", "modal", "android"),
    Triple("protocol", "sign", "android"),
    Triple("protocol", "auth", "android"),
    Triple("protocol", "chat", "android"),
    Triple("protocol", "notify", "android"),
    Triple("product", "web3wallet", "android"),
    Triple("product", "web3modal", "android"),
    Triple("product", "walletconnectmodal", "android"),
).apply {
    // The BOM has to be last artifact
    add(Triple("bom", null, "jvm"))
}

// This extension function will determine which task to run based on the type passed
fun List<Triple<String, String?, String>>.extractListOfPublishingTasks(type: ReleaseType): List<Task> = map { (parentModule, childModule, env) ->
    // Updated publication names to match new setup
    val task = when {
        env == "jvm" && type == ReleaseType.LOCAL -> "publishReleasePublicationToMavenLocal"
        env == "android" && type == ReleaseType.LOCAL -> "publishReleasePublicationToMavenLocal"
        else -> throw Exception("Only LOCAL publishing is supported for JitPack. Use JitPack for remote publishing.")
    }

    val module = if (childModule != null) {
        subprojects.first { it.name == parentModule }.subprojects.first { it.name == childModule }
    } else {
        subprojects.first { it.name == parentModule }
    }

    module.tasks.getByName(task)
}

enum class ReleaseType {
    LOCAL  // Only LOCAL is supported now - JitPack handles remote publishing
}
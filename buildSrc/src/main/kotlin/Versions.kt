import org.gradle.api.JavaVersion

//Latest versions
const val BOM_VERSION = "1.0.6"
const val FOUNDATION_VERSION = BOM_VERSION
const val CORE_VERSION =  BOM_VERSION
const val SIGN_VERSION =  BOM_VERSION
const val AUTH_VERSION =  BOM_VERSION
const val CHAT_VERSION =  BOM_VERSION
const val NOTIFY_VERSION =  BOM_VERSION
const val WEB_3_WALLET_VERSION =  BOM_VERSION
const val WEB_3_MODAL_VERSION =  BOM_VERSION
const val WC_MODAL_VERSION =  BOM_VERSION
const val MODAL_CORE_VERSION =  BOM_VERSION

//Artifact ids
const val ANDROID_BOM = "android-bom"
const val FOUNDATION = "foundation"
const val ANDROID_CORE = "android-core"
const val SIGN = "sign"
const val AUTH = "auth"
const val CHAT = "chat"
const val NOTIFY = "notify"
const val WEB_3_WALLET = "web3wallet"
const val WEB_3_MODAL = "web3modal"
const val WC_MODAL = "walletconnect-modal"
const val MODAL_CORE = "modal-core"

val jvmVersion = JavaVersion.VERSION_11
const val MIN_SDK: Int = 23
const val TARGET_SDK: Int = 36
const val COMPILE_SDK: Int = TARGET_SDK
val SAMPLE_VERSION_CODE = BOM_VERSION.replace(".", "").toInt()
const val SAMPLE_VERSION_NAME = BOM_VERSION
package presentation.splash

data class SplashState(
    val progress: Int = 0,
    val statusText: String = "Initializing...",
    val isFinished: Boolean = false
)

package dev.ridill.oar.onboarding.domain.model

enum class SignInAndDataRestoreState { SIGN_IN, DATA_RESTORE }

enum class DataRestoreState {
    IDLE,
    CHECKING_FOR_BACKUP,
    DOWNLOADING_DATA,
    PASSWORD_VERIFICATION,
    RESTORE_IN_PROGRESS,
    COMPLETED,
    FAILED
}
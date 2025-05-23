package dev.ridill.oar.onboarding.presentation

interface OnboardingActions {
    fun onGivePermissionsClick()
    fun onSkipPermissionsClick()
    fun onSignInClick()
    fun onSkipSignInClick()
    fun onCheckOrRestoreClick()
    fun onDataRestoreSkip()
    fun onEncryptionPasswordInputDismiss()
    fun onEncryptionPasswordSubmit(password: String)
    fun onStartBudgetingClick()
}
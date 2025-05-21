package dev.ridill.oar.account.data

import com.google.firebase.auth.FirebaseUser
import dev.ridill.oar.account.domain.model.UserAccount

fun FirebaseUser.toUserAccount(): UserAccount = UserAccount(
    email = email.orEmpty(),
    displayName = displayName.orEmpty(),
    photoUrl = photoUrl?.toString().orEmpty()
)
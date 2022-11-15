package ru.perekrestok.android.extension

import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import ru.perekrestok.android.R

fun Context.arePermissionsGranted(vararg permissions: String): Boolean {
    return permissions.all {
        PermissionChecker.checkSelfPermission(this, it) == PermissionChecker.PERMISSION_GRANTED
    }
}

fun Fragment.checkPermissions(
    vararg permissions: String,
    onPermissionChecked: () -> Unit
) {
    val isPermissionsGranted = requireContext().arePermissionsGranted(*permissions) // onPermissionsGranted()

    val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
            val hasNonGranted = permissionResult.any { it.value.not() }
            if (hasNonGranted) {
                requireContext().showAlertDialog(
                    title = getString(R.string.text_attention),
                    message = getString(R.string.text_permissions_explain),
                    positiveButton = AlertDialogButton(getString(R.string.text_ok)),
                    onDismissListener = { onPermissionChecked() }
                )
            } else {
                onPermissionChecked()
            }
        }

    when {
        isPermissionsGranted -> onPermissionChecked()
        else -> {
            requireContext().showAlertDialog(
                title = getString(R.string.text_attention),
                message = getString(R.string.text_permissions_rationales),
                positiveButton = AlertDialogButton(getString(R.string.text_ok)),
                onDismissListener = { permissionResultLauncher.launch(arrayOf(*permissions)) }
            )
        }
    }
}

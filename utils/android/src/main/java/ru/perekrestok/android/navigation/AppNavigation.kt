package ru.perekrestok.android.navigation

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Back
import com.github.terrakok.cicerone.BackTo
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Forward
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.snackbar.Snackbar
import ru.perekrestok.android.extension.isIntentResolved
import ru.perekrestok.android.extension.showErrorDetailsSnackBar
import ru.perekrestok.android.extension.showSnackBar
import ru.perekrestok.kotlin.StringPatterns
import timber.log.Timber

private data class Add(val screen: Screen) : Command

private data class TryToStartActivity(
    val primaryActivityScreen: ActivityScreen,
    val defaultFragmentScreen: FragmentScreen
) : Command

private object DismissAllDialogs : Command

private data class ShowSnackBar(
    val text: String,
    val duration: Int
) : Command

private data class ShowToast(val text: String, val length: Int) : Command

private data class ShowErrorSnackBar(
    val errorText: String,
    val errorDetails: String,
    val duration: Int
) : Command

interface NoAnimationFragment

/*
    Этот навигатор используется при add fragment и для использования DialogFragment/BottomSheetDialogFragment
 */
open class SupportAppNavigator(
    activity: FragmentActivity,
    fragmentManager: FragmentManager,
    @IdRes containerId: Int = -1
) : AppNavigator(activity, containerId, fragmentManager) {

    private val handler = Handler(Looper.getMainLooper())

    override fun applyCommands(commands: Array<out Command>) {
        try {
            super.applyCommands(commands)
        } catch (exception: IllegalStateException) {
            Timber.e(exception)
            /**
             * Исправляет ошибку "FragmentManager is already executing transactions"
             * https://github.com/terrakok/Cicerone/issues/104
             */
            handler.post { super.applyCommands(commands) }
        }
    }

    override fun applyCommand(command: Command) = when (command) {
        is Add -> add(command)
        is Replace -> replace(command)
        is DismissAllDialogs -> dismissAllDialogs()
        is TryToStartActivity -> tryToStartActivity(command)
        is ShowSnackBar -> showSnackBar(command)
        is ShowToast -> showToast(command)
        is ShowErrorSnackBar -> showErrorSnackBar(command)
        else -> super.applyCommand(command)
    }

    private fun setupFragmentTransaction(
        command: Command,
        nextFragment: Fragment?,
        fragmentTransaction: FragmentTransaction
    ) {
        when (command) {
            is Back,
            is BackTo -> fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)

            is Forward,
            is Replace,
            is Add -> {
                if (nextFragment is NoAnimationFragment) {
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE)
                } else {
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                }
            }
        }
    }

    override fun replace(command: Replace) {
        if (command.screen is FragmentScreen) {
            val screen = command.screen as FragmentScreen
            val fragment = screen.createFragment(fragmentFactory)

            if (fragment is DialogFragment) {
                forwardDialogFragment(fragment, screen)
            } else {
                super.replace(command)
            }
        } else {
            super.replace(command)
        }
    }

    private fun add(command: Add) {
        if (command.screen is FragmentScreen) {
            val screen = command.screen
            val fragment = screen.createFragment(fragmentFactory)

            if (fragment is DialogFragment) {
                forwardDialogFragment(fragment, screen)
            } else {
                forwardFragmentBase(fragment, command, screen)
            }
        }
    }

    private fun dismissAllDialogs() {
        fragmentManager.fragments.filterIsInstance<DialogFragment>().forEach { fragment ->
            fragment.dismiss()
        }
    }

    private fun tryToStartActivity(command: TryToStartActivity) {
        val primaryActivityScreen = command.primaryActivityScreen
        val primaryActivityIntent = primaryActivityScreen.createIntent(activity)

        if (activity.isIntentResolved(primaryActivityIntent)) {
            activity.startActivity(primaryActivityIntent, primaryActivityScreen.startActivityOptions)
        } else {
            commitNewFragmentScreen(command.defaultFragmentScreen, true)
        }
    }

    private fun showSnackBar(command: ShowSnackBar) {
        fragmentManager.findFragmentById(containerId)?.showSnackBar(
            text = command.text,
            duration = command.duration
        )
    }

    private fun showToast(command: ShowToast) {
        fragmentManager.findFragmentById(containerId)?.let { fragment ->
            Toast.makeText(fragment.requireContext(), command.text, command.length).show()
        }
    }

    private fun showErrorSnackBar(command: ShowErrorSnackBar) {
        fragmentManager.findFragmentById(containerId)?.showErrorDetailsSnackBar(
            errorText = command.errorText,
            errorDetails = command.errorDetails,
            duration = command.duration
        )
    }

    private fun forwardDialogFragment(
        fragment: DialogFragment,
        screen: Screen
    ) {
        if (fragmentManager.findFragmentByTag(screen.screenKey) == null) {
            fragment.show(fragmentManager, screen.screenKey)
        }
    }

    private fun forwardFragmentBase(
        fragment: Fragment?,
        command: Add,
        screen: Screen
    ) {

        val fragmentTransaction = fragmentManager.beginTransaction()

        setupFragmentTransaction(
            command,
            fragment,
            fragmentTransaction
        )

        fragment?.let {
            fragmentTransaction
                .add(containerId, it)
                .addToBackStack(screen.screenKey)
                .commit()
            localStackCopy.add(screen.screenKey)
        }
    }
}

abstract class AppRouter(qualifierName: String) : Router() {

    val name = qualifierName

    fun addTo(screen: Screen) {
        executeCommands(Add(screen))
    }

    fun backToRoot() {
        executeCommands(BackTo(null))
    }

    fun dismissAllDialogs() {
        executeCommands(DismissAllDialogs)
    }

    fun showSnackBar(text: String, duration: Int = Snackbar.LENGTH_LONG) {
        executeCommands(ShowSnackBar(text, duration))
    }

    fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        executeCommands(ShowToast(text, length))
    }

    fun showErrorSnackbar(
        errorText: String,
        errorDetails: String = StringPatterns.EMPTY_SYMBOL,
        duration: Int = Snackbar.LENGTH_LONG
    ) {
        executeCommands(ShowErrorSnackBar(errorText, errorDetails, duration))
    }

    /**
     * Попытка запуска вненшего приложения [primaryActivityScreen]. Если запуск [primaryActivityScreen] не удался -
     * выполняется запуск [defaultFragmentScreen]. Функция сделана для возможности навигации из ViewModel с целью
     * уменьшения дублирования кода.
     */
    fun tryToStartActivity(primaryActivityScreen: ActivityScreen, defaultFragmentScreen: FragmentScreen) {
        executeCommands(TryToStartActivity(primaryActivityScreen, defaultFragmentScreen))
    }
}

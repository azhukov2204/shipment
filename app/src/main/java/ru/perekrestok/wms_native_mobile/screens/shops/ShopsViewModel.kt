package ru.perekrestok.wms_native_mobile.screens.shops

import kotlinx.coroutines.launch
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.Event
import ru.perekrestok.domain.interactor.ShopsInteractor
import ru.perekrestok.wms_native_mobile.activity.MainActivityRouter
import ru.perekrestok.wms_native_mobile.screens.nav_drawer_container.NavDrawerScreen
import timber.log.Timber

class ShopsViewModel(
    private val afterChoseShopNavigation: ShopsFragment.Companion.AfterChoseShopRouting,
    private val shopsInteractor: ShopsInteractor,
    private val router: MainActivityRouter
) : BaseViewModel<ShopsViewState>() {

    init {
        viewModelScopeIO.launch {
            shopsInteractor.getCachedShopsFlow().collect { shops ->
                processDataEvent(
                    ShopsDataEvent.OnShopsReceived(shops)
                )
            }
        }

        viewModelScopeIO.launch {
            shopsInteractor.getCurrentShopFlow().collect { shop ->
                processDataEvent(
                    ShopsDataEvent.OnChosenShopReceived(shop)
                )
            }
        }
    }

    override fun initialViewState(): ShopsViewState = ShopsViewState()

    override suspend fun reduce(event: Event, currentState: ShopsViewState): ShopsViewState = when (event) {
        is LifecycleEvent -> dispatchLifecycleEvent(event, currentState)
        is ShopsDataEvent -> dispatchShopsDataEvent(event, currentState)
        is ShopsUiEvent -> dispatchShopsUiEvent(event, currentState)
        else -> currentState
    }

    private fun dispatchLifecycleEvent(
        event: LifecycleEvent,
        currentState: ShopsViewState
    ): ShopsViewState = when (event) {
        LifecycleEvent.OnLifecycleOwnerCreate -> currentState.also {
            fetchShopsContent()
        }
        else -> currentState
    }

    private fun dispatchShopsDataEvent(event: ShopsDataEvent, currentState: ShopsViewState): ShopsViewState =
        when (event) {
            is ShopsDataEvent.OnShopsReceived -> currentState.copy(shops = event.shops)
            is ShopsDataEvent.OnChosenShopReceived -> currentState.copy(currentShop = event.shop)
        }

    private fun dispatchShopsUiEvent(event: ShopsUiEvent, currentState: ShopsViewState): ShopsViewState = when (event) {
        ShopsUiEvent.OnCloseButtonClicked -> dispatchBackEvent(router, currentState)
        is ShopsUiEvent.OnSearchQueryFilled -> currentState.copy(
            searchQuery = event.searchQuery
        )
        is ShopsUiEvent.OnShopClicked -> currentState.also {
            handleOnShopClicked(currentState, event)
        }
        ShopsUiEvent.RefreshShopList -> currentState.also {
            fetchShopsContent()
        }
        ShopsUiEvent.OnImeSearchClicked -> currentState.also {
            handleOnImeSearchClicked()
        }
    }

    /**
     * Эта функция инициирует обновление кеша складов.
     * Список складов для отображения на UI берется из кега через Flow (см. блок init)
     */
    private fun fetchShopsContent() = viewModelScopeIO.launch {
        shopsInteractor.obtainShops()
            .onFailure { error ->
                Timber.e(error)
                router.showErrorSnackbar(
                    errorText = error.message.orEmpty(),
                    errorDetails = error.cause?.message.orEmpty()
                )
            }
    }

    private fun handleOnShopClicked(currentState: ShopsViewState, event: ShopsUiEvent.OnShopClicked) =
        viewModelScopeIO.launch {
            currentState.shops.find { shop -> shop.id == event.shopId }
                ?.let { shop ->
                    shopsInteractor.setCurrentShop(shop).fold(
                        onSuccess = {
                            doRouting()
                        },
                        onFailure = { error ->
                            Timber.e(error)
                            router.showErrorSnackbar(error.message.orEmpty(), error.cause?.message.orEmpty())
                        }
                    )
                }
        }

    private fun doRouting() {
        when (afterChoseShopNavigation) {
            ShopsFragment.Companion.AfterChoseShopRouting.NEW_ROOT_SCREEN -> router.newRootScreen(NavDrawerScreen)
            ShopsFragment.Companion.AfterChoseShopRouting.EXIT -> router.exit()
        }
    }

    private fun handleOnImeSearchClicked() {
        processSingleEvent(ShopsSingleEvent.ClearSearchFieldFocus)
    }
}

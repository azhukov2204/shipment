package ru.perekrestok.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface WireInteractor {
    val commandFlow: Flow<WireCommand>
    suspend fun sendCommand(wireCommand: WireCommand)
}

sealed interface WireCommand {
    object ClearCache : WireCommand
}

class WireInteractorImpl : WireInteractor {
    private val _commandMutableFlow: MutableSharedFlow<WireCommand> = MutableSharedFlow(replay = 1)

    override val commandFlow: Flow<WireCommand> = _commandMutableFlow.asSharedFlow()

    override suspend fun sendCommand(wireCommand: WireCommand) {
        _commandMutableFlow.emit(wireCommand)
    }
}

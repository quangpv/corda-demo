package com.template.webserver.models

import com.template.states.TransferState
import net.corda.core.contracts.StateAndRef
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.OK)
data class TransferBO(
        val value: Int,
        val sender: String,
        val receiver: String,
        val txHash: String,
        val index: Int
) {
    companion object {
        operator fun get(it: StateAndRef<TransferState>): TransferBO {
            val state = it.state.data
            return TransferBO(
                    state.value,
                    state.sender.toString(),
                    state.receiver.toString(),
                    it.ref.txhash.toString(),
                    it.ref.index
            )
        }
    }
}
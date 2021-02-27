package com.template.webserver.models

import net.corda.core.transactions.SignedTransaction
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CREATED)
data class TransferTransactionBO(
        val id: String
) {
    companion object {
        operator fun get(it: SignedTransaction) = TransferTransactionBO(
                it.id.toString()
        )
    }
}
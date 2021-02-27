package com.template.core

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

object EnforceContract : Contract {

    override fun verify(tx: LedgerTransaction) {
        for (command in tx.commands) {
            (command.value as? CommandVerifiable)?.verify(command, tx)
        }
    }
}

val ContractState.publicKeys: List<PublicKey>
    get() = participants.map { it.owningKey }
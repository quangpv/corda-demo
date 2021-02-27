package com.template.core

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.CommandWithParties
import net.corda.core.transactions.LedgerTransaction

interface CommandVerifiable {
    fun verify(command: CommandWithParties<CommandData>, tx: LedgerTransaction)
}
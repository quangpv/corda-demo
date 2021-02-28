package com.template.contracts

import com.template.core.contracts.CommandVerifiable
import com.template.core.contracts.VerifyCommandContract
import com.template.core.throws
import com.template.states.TransferState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.CommandWithParties
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

class TransferContract : Contract by VerifyCommandContract {

    companion object {
        @JvmStatic
        val ID = "com.template.contracts.TransferContract"
    }

    class Create : CommandData, CommandVerifiable {
        override fun verify(command: CommandWithParties<CommandData>, tx: LedgerTransaction) {
            tx.inputs.isNotEmpty() throws "No input in create transfer contract"
            (tx.outputs.size != 1) throws "Need only one output in create transfer contract"

            val out = tx.outputsOfType(TransferState::class.java).single()
            (out.sender == out.receiver) throws "The lender and the borrower cannot be the same entity."

            !command.signers.containsAll(out.participants.map { it.owningKey }) throws "All of the participants must be signers."

            (out.value <= 0) throws "The value must be non-negative."
        }
    }
}

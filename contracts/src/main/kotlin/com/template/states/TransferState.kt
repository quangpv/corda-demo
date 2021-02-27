package com.template.states

import com.template.contracts.TransferContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(TransferContract::class)
data class TransferState(
        val value: Int,
        val sender: Party,
        val receiver: Party
) : ContractState {
    override val participants: List<AbstractParty> = listOf(sender, receiver)
}

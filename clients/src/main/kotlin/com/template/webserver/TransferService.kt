package com.template.webserver

import com.template.flows.TransferRequester
import com.template.states.TransferState
import com.template.webserver.models.NodeInfoBO
import com.template.webserver.models.TransferBO
import com.template.webserver.models.TransferTransactionBO
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

val SERVICE_NAMES = listOf("Notary", "Network Map Service")

interface TransferService {
    fun getAllTransfers(): List<TransferBO>
    fun getMyTransfers(): List<TransferBO>
    fun createTransfer(value: Int, receiverName: String): TransferTransactionBO

    val otherPeers: List<NodeInfoBO>
    val myLegalName: NodeInfoBO
}

class MockTransferServiceImpl : TransferService {
    override fun getAllTransfers(): List<TransferBO> = emptyList()
    override fun getMyTransfers(): List<TransferBO> = emptyList()
    override fun createTransfer(value: Int, receiverName: String) = TransferTransactionBO("000")
    override val otherPeers: List<NodeInfoBO> = emptyList()
    override val myLegalName: NodeInfoBO = NodeInfoBO("Empty", "Empty")
}

@Service("DemoService")
class TransferServiceImpl(@Autowired private val rpc: NodeRPCConnection) : TransferService {
    private val mLegalName = proxy.nodeInfo().legalIdentities.first().name
    private val proxy get() = rpc.proxy

    override val otherPeers: List<NodeInfoBO>
        get() {
            val nodeInfo = proxy.networkMapSnapshot()
            return nodeInfo
                    .map { NodeInfoBO[it.legalIdentities.first().name] }
                    .filter { it.organisation !in (SERVICE_NAMES + mLegalName.organisation) }
        }
    override val myLegalName: NodeInfoBO
        get() = NodeInfoBO[mLegalName]

    override fun getAllTransfers(): List<TransferBO> {
        return proxy.vaultQueryBy<TransferState>().states.map {
            TransferBO[it]
        }
    }

    override fun getMyTransfers(): List<TransferBO> {
        return proxy.vaultQueryBy<TransferState>().states.filter {
            it.state.data.sender == proxy.nodeInfo().legalIdentities.first()
        }.map {
            TransferBO[it]
        }
    }

    override fun createTransfer(value: Int, receiverName: String): TransferTransactionBO {

        val receiver = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(receiverName))
                ?: throw IllegalAccessException("Party named $receiverName cannot be found.\n")

        val signedTx = proxy.startTrackedFlow(::TransferRequester, value, receiver)
                .returnValue.getOrThrow()

        return TransferTransactionBO[signedTx]
    }
}
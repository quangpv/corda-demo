package com.template.webserver

import com.template.webserver.models.NodeInfoBO
import com.template.webserver.models.TransferBO
import com.template.webserver.models.TransferTransactionBO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class TransferController {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    @Autowired
    private lateinit var transferService: TransferService

    @GetMapping(value = ["me"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun whoAmI(): NodeInfoBO = transferService.myLegalName

    @GetMapping(value = ["peers"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPeers(): List<NodeInfoBO> = transferService.otherPeers

    @GetMapping(value = ["transfers"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTransfers(): List<TransferBO> {
        return transferService.getAllTransfers()
    }

    @GetMapping(value = ["me/transfers"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMyTransfers(): List<TransferBO> {
        return transferService.getMyTransfers()
    }

    @PostMapping(value = ["transfers"], produces = [MediaType.APPLICATION_JSON_VALUE], headers = ["Content-Type=application/x-www-form-urlencoded"])
    fun createTransfer(@RequestParam value: Int, @RequestParam receiverName: String): TransferTransactionBO {
        if (value <= 0) throw ParameterInvalidException("Query parameter 'value' must be non-negative.\n")

        return try {
            transferService.createTransfer(value, receiverName)
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            throw ex
        }
    }
}
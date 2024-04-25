package me.sammyvision.invoicegenerator.data

class ModelInvoiceInfo(
    val customerDetails : ModelCustomerInfo = ModelCustomerInfo(),
    val invoiceNumber : String = "",
    val gstNumber : String = "",
    val invoiceDate : String = "",
    val invoiceTotal : String = ""

) {
    data class ModelCustomerInfo(
        val name: String = "",
        val addressLine1: String = "",
    )
}

package me.sammyvision.invoicegenerator.data

data class ModelInvoiceHeader(
    val companyName: String = "",
    val emailAddress: String = "",
    val websiteURL: String = "",
    val address: ModelAddress = ModelAddress()
) {
    data class ModelAddress(
        val addressLine1: String = "",
        val addressLine2: String = "",
        val addressLine3: String = ""
    )
}
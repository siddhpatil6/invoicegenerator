package sammyvision.invoicegenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.sammyvision.invoicegenerator.data.ModelInvoiceHeader
import sammyvision.invoicegenerator.databinding.ActivityCompanyInformationBinding

class CompanyInformationActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCompanyInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(CustomSharedPreferences.hasInvoiceHeader()){
            val header = CustomSharedPreferences.getInvoiceHeader()
            binding.editTextCompanyName.setText(header.companyName)
            binding.editTextEmail.setText(header.emailAddress)
            binding.editTextWebsite.setText(header.websiteURL)
            binding.editTextAddressLine1.setText(header.address.addressLine1)
            binding.editTextAddressLine2.setText(header.address.addressLine2)
            binding.editTextAddressLine3.setText(header.address.addressLine3)
        }
        binding.buttonSave.setOnClickListener {
            val companyName = binding.editTextCompanyName.text.toString()
            val emailAddress = binding.editTextEmail.text.toString()
            val websiteURL = binding.editTextWebsite.text.toString()
            val addressLine1 = binding.editTextAddressLine1.text.toString()
            val addressLine2 = binding.editTextAddressLine2.text.toString()
            val addressLine3 = binding.editTextAddressLine3.text.toString()

            val invoiceAddress =
                ModelInvoiceHeader.ModelAddress(
                    addressLine1,
                    addressLine2,
                    addressLine3
                )
            val headerData = ModelInvoiceHeader(
                companyName,
                emailAddress,
                websiteURL,invoiceAddress
            )

            CustomSharedPreferences.setInvoiceHeader(headerData)
            finish()
        }
    }
}
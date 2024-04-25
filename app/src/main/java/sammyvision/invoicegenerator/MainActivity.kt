package sammyvision.invoicegenerator

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import me.sammyvision.invoicegenerator.data.*
import me.sammyvision.invoicegenerator.utils.InvoiceGenerator
import sammyvision.invoicegenerator.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var invoiceColor: String
    private var currency = "Rs."
    val internetSpeeds = arrayOf("10 Mbps","20 Mbps","30 Mbps","50 Mbps","100 Mbps","300 Mbps")
    val billPeriods = arrayOf("Monthly","Quarterly","Semi Yearly","Annually")

    var selectedPeriod =  "Monthly"
    var selectedPlan = "50 Mbps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        invoiceColor = resources.getString(0 + R.color.purple_500)



        binding.btnSaveCompanyInfo.setOnClickListener {
            val companyIntent = Intent(this, CompanyInformationActivity::class.java)
            startActivity(companyIntent)
        }
        setPlansOnSpinner()
        setPlanTypeOnSpinner()
    }


    fun generatePDF(view: View) {
        Utils.checkStoragePermission(this, {
            if (CustomSharedPreferences.hasInvoiceHeader()) {
                createPDFFile()
            } else {
                Toast.makeText(applicationContext, "Please save company details", Toast.LENGTH_LONG)
                    .show()
            }

        }, { isPermenentlyDenied ->
            toast("permissions missing :(")
        })


    }

    private fun setPlansOnSpinner(){
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, internetSpeeds)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.spinnerPlan.adapter = adapter
        // Set a listener to be called when an item is selected
        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Handle item selection
                selectedPlan = internetSpeeds[position]
                // Do something with the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }


    private fun setPlanTypeOnSpinner(){
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, billPeriods)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.spinnerBillPeriod.adapter = adapter
        // Set a listener to be called when an item is selected
        binding.spinnerBillPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Handle item selection
                selectedPlan = internetSpeeds[position]
                // Do something with the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    private fun createPDFFile() {

        if (!isUIValid()) return

        //data for invoice header


        val headerData = CustomSharedPreferences.getInvoiceHeader()
        //data for invoice
        val customerInfo =
            ModelInvoiceInfo.ModelCustomerInfo(
                binding.edtCustomerName.text.toString(),
                binding.edtCustomerAddress1.text.toString()
            )
        val invoiceInfo = ModelInvoiceInfo(
            customerInfo,
            binding.edtInvoiceNumber.text.toString(),
            binding.edtGstNumber.text.toString(),
            binding.edtInvoiceDate.text.toString(),
            binding.edtGrandTotal.text.toString()
        )
        val tableHeader =
            ModelTableHeader(
                "Speed",
                "Package",
                "Validity",
                "Discount",
                "Amount"
            )
        val totalAmount = binding.edtGrandTotal.text.toString().toDouble()
        val actualAmount : Double = totalAmount.toDouble() * (100.00 / 118.00).toDouble()

        val tableData = ModelInvoiceItem(
            firstItem = selectedPlan,
            firstItemDescription = "",
            secondsItem = "Unlimited",
            thirdItem = selectedPeriod,
            fourthItem = "0",
            fifthItem= String.format("%.2f", actualAmount)
        )
        val cGST :String= showDecimal(actualAmount.toDouble() * (9.00/100.00))
        val sGST :String= showDecimal(actualAmount.toDouble() * (9.00/100.00))
        val invoicePriceInfo = ModelInvoicePriceInfo(
            cGST.toString(),
            sGST.toString(),
            binding.edtGrandTotal.text.toString()
        )
        val footerData = ModelInvoiceFooter(
            "Terms and Conditions \n" +
                    "1. Cheques to be in favour of "+headerData.companyName+" \n" +
                    "2. In case of cheque bounce,100rs penalty will be applicable.\n" +
                    "3. Sammy Vision Pvt Ltd Shall levy late fee charge in case the bill is paid after the due date \n" +
                    "4. In case of overdue, the right to deactivate your services, is reserved.\n" +
                    "5. This Invoice is system generated hence signature and stamp is not required\n" +
                    " "
        )
        val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceLogo(R.drawable.invoice_icon)
            setCurrency(currency)
            setInvoiceColor(invoiceColor)
            setInvoiceHeaderData(headerData)
            setInvoiceInfo(invoiceInfo)
            setInvoiceTableHeaderDataSource(tableHeader)
            setInvoiceTableData(
                mutableListOf(
                    tableData,
                ) as List<ModelInvoiceItem>
            )
            setPriceInfoData(invoicePriceInfo)
            setInvoiceFooterData(footerData)
        }

        val fileUri = pdfGenerator.generatePDF("${(0..99999).random()}.pdf")
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "There is no PDF Viewer", Toast.LENGTH_SHORT).show()
        }
    }


    fun showDecimal(amount: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val roundoff = df.format(amount)
        return roundoff
    }
    fun showColorPicker(view: View) {
        MaterialColorPickerDialog
            .Builder(this)                            // Pass Activity Instance
            .setTitle("Pick Color")                // Default "Choose Color"
            .setColorShape(ColorShape.SQAURE)    // Default ColorShape.CIRCLE
            .setColorSwatch(ColorSwatch._300)    // Default ColorSwatch._500
            .setColorListener { color, colorHex ->
                invoiceColor = colorHex
                binding.viewColorPicker.setCardBackgroundColor(Color.parseColor(colorHex))
            }
            .show()
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun isUIValid(): Boolean {

        if (binding.edtCustomerName.text.toString().isEmpty()) {
            toast("Customer name required")
            return false
        }
        if (binding.edtCustomerAddress1.text.toString().isEmpty()) {
            toast("customer address line 1 required")
            return false
        }
        if (binding.edtInvoiceNumber.text.toString().isEmpty()) {
            toast("Invoice number required")
            return false
        }
        if (binding.edtInvoiceDate.text.toString().isEmpty()) {
            toast("Invoice date required")
            return false
        }


        if (binding.spinnerPlan.selectedItem.toString().isEmpty()) {
            toast("Enter Plan")
            return false
        }

        if (binding.edtGrandTotal.text.toString().isEmpty()) {
            toast("Grand total required")
            return false
        }

        return true
    }
}
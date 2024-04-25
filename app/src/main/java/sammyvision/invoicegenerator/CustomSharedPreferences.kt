package sammyvision.invoicegenerator

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.sammyvision.invoicegenerator.data.ModelInvoiceHeader

object CustomSharedPreferences {
    private const val PREFS_NAME = "CustomPrefs"
    private const val KEY_INVOICE_HEADER = "invoice_header"

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setInvoiceHeader(invoiceHeader: ModelInvoiceHeader) {
        val json = gson.toJson(invoiceHeader)
        sharedPreferences.edit().putString(KEY_INVOICE_HEADER, json).apply()
    }

    fun getInvoiceHeader(): ModelInvoiceHeader {
        val json = sharedPreferences.getString(KEY_INVOICE_HEADER, null)
        return if (json != null) {
            gson.fromJson(json, ModelInvoiceHeader::class.java)
        } else {
            ModelInvoiceHeader()
        }
    }

    fun hasInvoiceHeader(): Boolean {
        return sharedPreferences.contains(KEY_INVOICE_HEADER)
    }


    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}

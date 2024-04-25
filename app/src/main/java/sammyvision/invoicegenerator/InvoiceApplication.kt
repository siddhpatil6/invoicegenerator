package sammyvision.invoicegenerator

import android.app.Application

class InvoiceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CustomSharedPreferences.init(this)
    }
}

package a.logic.app.extension

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class G : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }
}
package uz.tayanch.app

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uz.tayanch.app.core.LocaleUtil
import uz.tayanch.app.di.appModule

class TayanchApp : Application() {
    override fun attachBaseContext(base: Context) {
        // Force Uzbek; the wrapped context is what Koin hands to ResourceProvider.
        super.attachBaseContext(LocaleUtil.wrap(base))
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TayanchApp)
            modules(appModule)
        }
    }
}

package space.taran.arkrate.utils

import android.content.Context
import com.simplemobiletools.commons.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var crashReport:Boolean
        get() = prefs.getBoolean(CRASH_REPORT_ENABLE, true)
        set(isEnable) = prefs.edit().putBoolean(CRASH_REPORT_ENABLE, isEnable)
            .apply()
}
package org.example.project

import android.content.Context
import java.lang.ref.WeakReference

object ContextHolder {
    private var contextRef: WeakReference<Context>? = null

    fun setContext(context: Context) {
        contextRef = WeakReference(context)
    }

    fun getContext(): Context? {
        return contextRef?.get()
    }
}

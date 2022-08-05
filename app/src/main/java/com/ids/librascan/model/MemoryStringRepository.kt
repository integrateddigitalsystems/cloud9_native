package com.ids.librascan.model

import dev.b3nedikt.restring.PluralKeyword
import dev.b3nedikt.restring.Restring.stringRepository
import dev.b3nedikt.restring.StringRepository
import java.util.*

class MemoryStringRepository : StringRepository {
    fun setUp() {
        stringRepository = MemoryStringRepository()
    }

    override val quantityStrings: Map<Locale, Map<String, Map<PluralKeyword, CharSequence>>>
        get() = TODO("Not yet implemented")
    override val stringArrays: Map<Locale, Map<String, Array<CharSequence>>>
        get() = TODO("Not yet implemented")
    override val strings: Map<Locale, Map<String, CharSequence>>
        get() = TODO("Not yet implemented")
    override val supportedLocales: Set<Locale>
        get() = TODO("Not yet implemented")
}
package com.afi.sales.importer

inline fun <O> given(block: () -> O): O {
    return block()
}

inline infix fun <I, O> I.whenever(block: (I) -> O): O {
    return block(this)
}

inline infix fun <I, O> I.then(block: (I) -> O): O {
    return block(this)
}

package br.com.astrosoft.framework.viewmodel

class DataViewThread<T> {
  var block: (() -> T)? = null
  var update: ((T) -> Unit)? = null
}

fun <T> DataViewThread<T>.block(block: () -> T) {
  this.block = block
}

fun <T> DataViewThread<T>.update(update: (T) -> Unit) {
  this.update = update
}
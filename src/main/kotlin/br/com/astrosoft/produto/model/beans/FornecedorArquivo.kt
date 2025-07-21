package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci

class FornecedorArquivo {
  var seq: Int? = null
  var vendno: Int? = null
  var filename: String? = null
  var file: ByteArray? = null

  val filesize: String
    get() = formatFileSize(file?.size ?: 0)

  private fun formatFileSize(sizeInBytes: Int): String {
    val kilobyte = 1024.0
    val megabyte = kilobyte * 1024
    val gigabyte = megabyte * 1024
    return when {
      sizeInBytes >= gigabyte -> "${(sizeInBytes / gigabyte).format()} GB"
      sizeInBytes >= megabyte -> "${(sizeInBytes / megabyte).format()} MB"
      sizeInBytes >= kilobyte -> "${(sizeInBytes / kilobyte).format()} KB"
      else                    -> "$sizeInBytes B"
    }
  }

  fun remove() {
    saci.fornecedorArquivoDelete(this)
  }

  companion object {
    fun find(vendno: Int): List<FornecedorArquivo> {
      return saci.fornecedorArquivo(vendno)
    }

    fun save(vendno: Int, filename: String, file: ByteArray) {
      val file = FornecedorArquivo().apply {
        this.vendno = vendno
        this.filename = filename
        this.file = file
      }
      saci.fornecedorArquivoSave(file)
    }
  }
}
package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class InvFileDev(
  var invno: Int?,
  var numero: Int?,
  var tipoDevolucao: Int?,
  var seq: Int?,
  var date: LocalDate?,
  var fileName: String?,
  var file: ByteArray?,
) {
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

  fun update() {
    saci.updateInvFile(this)
  }

  fun delete() {
    saci.deleteInvFile(this)
  }

  companion object {
    fun findAll(invno: Int, tipo: ETipoDevolucao, numero: Int): List<InvFileDev> {
      return saci.findInvFile(invno, tipo, numero)
    }
  }
}
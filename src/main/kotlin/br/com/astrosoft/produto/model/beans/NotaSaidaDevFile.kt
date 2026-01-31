package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaSaidaDevFile(
  var seq: Int?,
  var loja: Int,
  var pdvno: Int,
  var xano: Long,
  var tipo: String?,
  var date: LocalDate?,
  var filename: String?,
  var file: ByteArray?
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

  fun save() {
    saci.notaSaidaDevolucaoSave(this)
  }

  fun delete() {
    if (tipo == "S") {
      saci.notaSaidaDevolucaoDelete(this)
    } else {
      val notaFile = InvFileDev(
        invno = 0,
        numero = 0,
        tipoDevolucao = 0,
        seq = seq,
        date = LocalDate.now(),
        fileName = filename,
        file = file,
      )
      notaFile.delete()
    }
  }
}
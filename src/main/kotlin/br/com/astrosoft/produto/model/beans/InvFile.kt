package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class InvFile(
  var seq: Int?,
  var invno: Int?,
  var title: String?,
  var date: LocalDate?,
  var fileName: String?,
  var file: ByteArray?,
) {
  fun update() {
    saci.updateInvFile(this)
  }

  fun delete() {
    saci.deleteInvFile(this)
  }

  companion object {
    fun findAll(invno: Int): List<InvFile> {
      return saci.findInvFile(invno)
    }
  }
}
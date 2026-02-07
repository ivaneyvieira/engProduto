package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class EmailDevolucao(
  var id: Int = 0,
  var chave: String = "",
  var fromEmail: String = "",
  var toEmail: String = "",
  var ccEmail: String = "",
  var bccEmail: String = "",
  var subject: String = "",
  var enviado: Boolean = false,
  var htmlContent: String = ""
) {
  val anexos = mutableListOf<AnexoEmail>()

  fun addAnexo(anexo: AnexoEmail) {
    anexos.add(anexo)
  }

  fun removeAnexo(anexo: AnexoEmail) {
    anexos.remove(anexo)
  }

  var toEmailList: Set<String>
    get() = toEmail.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
    set(value) {
      toEmail = value.joinToString(",")
    }

  var ccEmailList: Set<String>
    get() = ccEmail.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
    set(value) {
      ccEmail = value.joinToString(",")
    }

  var bccEmailList: Set<String>
    get() = bccEmail.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
    set(value) {
      bccEmail = value.joinToString(",")
    }

  fun save() {
    val last = saci.emailSave(this)
    this.id = last?.id ?: 0
    anexos.forEach {
      anexoSave(it)
    }
  }

  fun delete() {
    anexos.forEach {
      anexoDelete(it)
    }
    saci.emailDelete(this)
  }

  fun anexoSave(anexo: AnexoEmail) {
    saci.emailAnexoSave(anexo)
  }

  fun anexoDelete(anexo: AnexoEmail) {
    saci.emailAnexoDelete(anexo)
  }

  fun anexoSelect(): List<AnexoEmail> {
    return saci.emailAnexoSelect(this)
  }

  companion object {
    fun findAll(chave: String): List<EmailDevolucao> {
      return saci.emailSelect(chave)
    }
  }
}


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
  var toEmailList: List<String>
    get() = toEmail.split(",").map { it.trim() }.filter { it.isNotBlank() }
    set(value) {
      toEmail = value.joinToString(",")
    }

  var ccEmailList: List<String>
    get() = ccEmail.split(",").map { it.trim() }.filter { it.isNotBlank() }
    set(value) {
      ccEmail = value.joinToString(",")
    }

  var bccEmailList: List<String>
    get() = bccEmail.split(",").map { it.trim() }.filter { it.isNotBlank() }
    set(value) {
      bccEmail = value.joinToString(",")
    }

  fun save() {
    saci.emailSave(this)
  }

  fun delete() {
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


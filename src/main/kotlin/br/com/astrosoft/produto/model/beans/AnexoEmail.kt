package br.com.astrosoft.produto.model.beans

class AnexoEmail(
  var id: Int = 0,
  var idEmail: Int = 0,
  var nomeArquivo: String = "",
  var mimeType: String = "",
  var conteudo: ByteArray = byteArrayOf()
)
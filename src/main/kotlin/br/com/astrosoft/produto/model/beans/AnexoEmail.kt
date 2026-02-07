package br.com.astrosoft.produto.model.beans

class AnexoEmail(
  var id: Int = 0,
  var idEmail: Int = 0,
  var nomeArquivo: String = "",
  var conteudo: ByteArray = byteArrayOf()
) {
  val mimeType: String
    get() {
      return when {
        nomeArquivo.endsWith(".pdf")  -> "application/pdf"
        nomeArquivo.endsWith(".doc")  -> "application/msword"
        nomeArquivo.endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        nomeArquivo.endsWith(".jpg")  -> "image/jpeg"
        nomeArquivo.endsWith(".png")  -> "image/png"
        else                          -> "application/octet-stream"
      }
    }

  val nomeArquivoSimples: String
    get() {
      if (nomeArquivo.contains("/")) {
        return nomeArquivo.substringAfterLast("/")
      }
      return nomeArquivo
    }
}
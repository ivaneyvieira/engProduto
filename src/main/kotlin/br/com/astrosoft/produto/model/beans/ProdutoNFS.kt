package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci

class ProdutoNFS(
  var loja: Int,
  var pdvno: Int,
  var xano: Long,
  var nota: String?,
  var prdno: String?,
  var codigo: String?,
  var grade: String?,
  var barcodeProd: String?,
  var barcodeStrList: String?,
  var descricao: String?,
  var vendno: Int?,
  var fornecedor: String?,
  var typeno: Int?,
  var typeName: String?,
  var clno: String?,
  var clname: String?,
  var altura: Int?,
  var comprimento: Int?,
  var largura: Int?,
  var precoCheio: Double?,
  var ncm: String?,
  var local: String?,
  var quantidade: Int?,
  var preco: Double?,
  var total: Double?,
  var gradeAlternativa: String?,
  var marca: Int?,
  var marcaImpressao: Int?,
  var usernoExp: Int?,
  var usuarioExp: String?,
  var dataHoraExp: String?,
  var usernoCD: Int?,
  var usuarioCD: String?,
  var dataHoraCD: String?,
  var usuarioSep: String?,
  var tipoNota: Int?,
  var estoque: Int?,
  var temProduto: Boolean?,
  var quantDev: Int?,
) {

  override fun toString(): String {
    return "ProdutoNFS(loja=$loja, pdvno=$pdvno, xano=$xano, nota=$nota, codigo=$codigo, grade=$grade, barcodeProd=$barcodeProd, barcodeStrList=$barcodeStrList, descricao=$descricao, vendno=$vendno, fornecedor=$fornecedor, typeno=$typeno, typeName=$typeName, clno=$clno, clname=$clname, altura=$altura, comprimento=$comprimento, largura=$largura, precoCheio=$precoCheio, ncm=$ncm, local=$local, quantidade=$quantidade, preco=$preco, total=$total, gradeAlternativa=$gradeAlternativa, marca=$marca, marcaImpressao=$marcaImpressao, usernoExp=$usernoExp, usuarioExp=$usuarioExp, dataHoraExp=$dataHoraExp, usernoCD=$usernoCD, usuarioCD=$usuarioCD, dataHoraCD=$dataHoraCD, usuarioSep='$usuarioSep', tipoNota=$tipoNota)"
  }

  val gradeEfetiva: String
    get() {
      return if (grade.isNullOrBlank()) {
        gradeAlternativa ?: ""
      } else {
        if (gradeAlternativa.isNullOrBlank()) {
          grade ?: ""
        } else {
          gradeAlternativa ?: ""
        }
      }.trim()
    }

  var selecionado: Boolean = false

  val espaco: String
    get() = ""

  val estoqueStr: String
    get() = "Estoque: ${estoque?.format() ?: "0"}"

  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  private fun splitExp(index: Int) = dataHoraExp?.split("-")?.getOrNull(index) ?: ""

  val barcodes: List<String>
    get() {
      val barcodeList = barcodeStrList?.split(",")?.map { it.trim() }.orEmpty()
      val barprd = listOfNotNull(barcodeProd)
      return barprd + barcodeList
    }

  val dataExp
    get() = splitExp(1)
  val horaExp
    get() = splitExp(2)

  private fun splitCD(index: Int) = dataHoraCD?.split("-")?.getOrNull(index) ?: ""

  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val statusStr = EMarcaNota.entries.firstOrNull { it.num == marca }?.descricao ?: ""

  fun salva() {
    saci.salvaProdutosNFS(this)
    val prd = saci.findProdutoNF(this).firstOrNull()
    prd?.let {
      this.dataHoraExp = it.dataHoraExp
      this.dataHoraCD = it.dataHoraCD
      this.usuarioExp = it.usuarioExp
      this.usuarioCD = it.usuarioCD
    }
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo ?: "")
  }

  fun marcaImpressao() {
    val user = AppConfig.userLogin() as? UserSaci
    this.marcaImpressao = 1
    this.usuarioSep = user?.login ?: ""
    saci.salvaProdutosNFS(this)
  }

  fun updateQuantDev() {
    saci.updateQuantDev(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ProdutoNFS

    if (loja != other.loja) return false
    if (pdvno != other.pdvno) return false
    if (xano != other.xano) return false
    if (prdno != other.prdno) return false
    if (grade != other.grade) return false

    return true
  }

  override fun hashCode(): Int {
    var result = loja
    result = 31 * result + pdvno
    result = 31 * result + xano.hashCode()
    result = 31 * result + (prdno?.hashCode() ?: 0)
    result = 31 * result + (grade?.hashCode() ?: 0)
    return result
  }
}

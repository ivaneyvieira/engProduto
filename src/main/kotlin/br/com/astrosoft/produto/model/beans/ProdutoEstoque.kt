package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userRessuprimentoLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoEstoque(
  var loja: Int?,
  var lojaSigla: String?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var unidade: String?,
  var grade: String?,
  var tipo: Int?,
  var cl: Int?,
  var embalagem: Int?,
  var qtdEmbalagem: Double?,
  var estoque: Int?,
  var locNerus: String?,
  var locApp: String?,
  var codForn: Int?,
  var fornecedor: String?,
  var fornecedorAbrev: String?,
  var cnpjFornecedor: String?,
  var saldo: Int?,
  var saldoVarejo: Int?,
  var saldoAtacado: Int?,
  var dataInicial: LocalDate?,
  var dataUpdate: LocalDate?,
  var kardec: Int? = null,
  //var dataConferencia: LocalDate? = null,
  var qtConfEditLoja: Int? = null,
  var qtConfEdit: Int? = null,
  var qtConferencia: Int? = null,
  var preco: Double? = null,
  var estoqueUser: Int? = null,
  var estoqueLogin: String? = null,
  var estoqueData: LocalDate? = null,
  var estoqueCD: Int? = null,
  var estoqueLoja: Int? = null,
  var barcode: String? = null,
  var ref: String? = null,
  var numeroAcerto: Int? = null,
  var processado: Boolean? = false,
) {
  fun isUpdated(): Boolean {
    return dataUpdate.toSaciDate() == LocalDate.now().toSaciDate()
  }

  val qtdDif: Double
    get() {
      val sistema = saldo?.toDouble() ?: 0.0
      val cd = kardec?.toDouble() ?: 0.0
      val diferenca = sistema - cd
      return if (descricao?.startsWith("SVS E") == true) {
        diferenca * 900.0 / 5800.0
      } else {
        diferenca
      }
    }

  val saldoBarraRef: String
    get() {
      return "${barcode ?: ""}   |   ${ref ?: ""}"
    }

  var marcadoConfProp: Boolean = false

  fun marcadoConf(userNo: Int, data: LocalDate): Boolean {
    this.marcadoConfProp = (estoqueUser == userNo) && (estoqueData == data)
    return this.marcadoConfProp
  }

  val estoqueDif: Int?
    get() {
      if (estoqueCD == null && estoqueLoja == null) {
        return null
      }

      if (saldo == null) {
        return null
      }

      val estLoja = estoqueLoja ?: 0
      val estCD = estoqueCD ?: 0
      val estSaldo = saldo ?: 0

      return estLoja + estCD - estSaldo
    }

  val diferenca: Int
    get() {
      val estCD = kardec ?: 0
      val estSis = saldo ?: 0
      return estSis - estCD
    }

  fun dataInicialDefault(): LocalDate {
    return dataInicial ?: LocalDate.now().withDayOfMonth(1)
  }

  val codigoStr
    get() = this.codigo?.toString() ?: ""

  val kardecEmb: Double?
    get() {
      return when {
        descricao?.startsWith("SVS E-COLOR") == true -> {
          if (kardec == null) null else (kardec ?: 0) / 5800.0
        }

        descricao?.startsWith("VRC COLOR") == true   -> {
          if (kardec == null) null else (kardec ?: 0) / 1000.0
        }

        else                                         -> {
          if (kardec == null) null else (kardec ?: 0) / ((embalagem ?: 0) * 1.00)
        }
      }
    }

  fun update() {
    saci.updateProdutoEstoque(this)
  }

  fun recebimentos(dataInicial: LocalDate): List<ProdutoKardec> {
    val filtro = FiltroNotaRecebimentoProduto(
      loja = 4,
      pesquisa = "",
      marca = EMarcaRecebimento.RECEBIDO,
      dataFinal = null,
      dataInicial = dataInicial,
      localizacao = listOf("TODOS"),
      prdno = prdno ?: "",
      grade = grade ?: "",
      tipoNota = EListaContas.TODOS,
    )
    return saci.findNotaRecebimentoProduto(filtro).map { nota ->
      ProdutoKardec(
        loja = nota.loja ?: 0,
        prdno = prdno ?: "",
        grade = grade ?: "",
        data = nota.data,
        doc = nota.nfEntrada ?: "",
        tipo = ETipoKardec.RECEBIMENTO,
        vencimento = nota.vencimento,
        qtde = nota.quant ?: 0,
        saldo = 0,
        userLogin = nota.login ?: "",
        observacao = nota.observacaoNota
      )
    }
  }

  private fun filtroRessuprimento(marca: EMarcaRessuprimento, dataInicial: LocalDate): FiltroRessuprimento {
    return FiltroRessuprimento(
      numero = 0,
      pesquisa = "",
      dataNotaInicial = dataInicial,
      dataPedidoInicial = dataInicial,
      marca = marca,
      temNota = ETemNota.TEM_NOTA,
      lojaRessu = 0,
      prdno = this.prdno ?: "",
      grade = grade ?: "",
    )
  }

  private fun ressuprimento(marca: EMarcaRessuprimento, dataInicial: LocalDate): List<ProdutoKardec> {
    val filtro = filtroRessuprimento(marca, dataInicial)
    val ressuprimentos = saci.findRessuprimento(filtro, userRessuprimentoLocais())
    return ressuprimentos.flatMap { ressuprimento ->
      val prdno = prdno ?: return emptyList()
      val grade = grade ?: return emptyList()
      val mult = when (marca) {
        EMarcaRessuprimento.REC -> -1
        EMarcaRessuprimento.ENT -> -1
        else                    -> 0
      }
      ressuprimento.produtos(prdno, grade).map { produto ->
        ProdutoKardec(
          loja = ressuprimento.loja ?: 0,
          prdno = prdno,
          grade = grade,
          data = ressuprimento.dataBaixa,
          doc = ressuprimento.notaBaixa ?: "",
          tipo = ETipoKardec.RESSUPRIMENTO,
          qtde = (produto.qtQuantNF ?: 0) * mult,
          saldo = 0,
          userLogin = ressuprimento.entregueSPor ?: "",
          observacao = ressuprimento.observacao
        )
      }
    }
  }

  fun ressuprimento(dataInicial: LocalDate): List<ProdutoKardec> {
    val listaKardec =
        ressuprimento(EMarcaRessuprimento.ENT, dataInicial) + ressuprimento(EMarcaRessuprimento.REC, dataInicial)
    return listaKardec.filter {
      it.loja == 4
    }
  }

  fun expedicao(dataInicial: LocalDate): List<ProdutoKardec> {
    val filtro = FiltroNota(
      marca = EMarcaNota.ENT,
      tipoNota = ETipoNotaFiscal.TODOS,
      loja = 0,
      pesquisa = "",
      prdno = prdno ?: "",
      grade = "",
      dataInicial = dataInicial.minusDays(30),
      dataEntregaInicial = null,
      dataFinal = null,
      dataNotas = LocalDate.now().minusDays(180),
      numero = 0,
      localizacaoNota = listOf("TODOS"),
    )
    val notasEnt = saci.findNotaSaida(filtro = filtro.copy(marca = EMarcaNota.ENT))
    val notas = notasEnt.filter {
      it.cancelada != "S"
    }
    return notas.flatMap { nota ->
      val tipo = if (nota.tipoNotaSaida == ETipoNotaFiscal.ENTRE_FUT.name) {
        ETipoKardec.ENTREGA
      } else {
        ETipoKardec.EXPEDICAO
      }

      val usuario = if (nota.tipoNotaSaida == ETipoNotaFiscal.ENTRE_FUT.name) {
        (nota.usuarioCD ?: "").split("-").firstOrNull() ?: ""
      } else {
        (nota.usuarioExp ?: "").split("-").firstOrNull() ?: ""
      }

      //Validações
      val data = nota.dataEntrega ?: nota.data ?: return@flatMap emptyList()
      if (data < dataInicial) return@flatMap emptyList()

      val produtosEnt = nota.produtos(marca = EMarcaNota.ENT, prdno = prdno ?: "", grade = "", todosLocais = true)

      val nota1 = nota.notaEntrega ?: ""
      val nota2: String = "${nota.numero}/${nota.serie}"

      val notaFutura = when {
        nota1.endsWith("/1") -> nota1
        nota2.endsWith("/1") -> nota2
        nota1.isNotEmpty()   -> nota1
        nota2.isNotEmpty()   -> nota2
        else                 -> ""
      }

      val notaEntrega = when {
        nota1.endsWith("/3") -> nota1
        nota2.endsWith("/3") -> nota2
        nota1.isNotEmpty()   -> nota2
        else                 -> ""
      }

      produtosEnt.filter { produto ->
        produto.gradeEfetiva == (grade ?: "")
      }.map { produto ->
        ProdutoKardec(
          loja = nota.loja,
          prdno = prdno ?: "",
          grade = produto.gradeEfetiva,
          data = data,
          doc = notaFutura,
          nfEnt = notaEntrega,
          tipo = tipo,
          qtde = -(produto.quantidade ?: 0),
          saldo = 0,
          userLogin = nota.usuarioSingCD ?: usuario,
          observacao = nota.observacao
        )
      }.distinctBy { "${it.loja} ${it.doc} ${it.nfEnt}" }
    }
  }

  fun reposicao(dataInicial: LocalDate): List<ProdutoKardec> {
    val user = AppConfig.userLogin() as? UserSaci
    val localizacao = if (user?.admin == true) {
      listOf("TODOS")
    } else {
      user?.localizacaoRepo.orEmpty().toList()
    }
    val filtro = FiltroReposicao(
      loja = 4,
      pesquisa = "",
      marca = EMarcaReposicao.ENT,
      localizacao = localizacao,
      dataInicial = dataInicial,
      dataFinal = null,
      prdno = prdno ?: "",
      grade = grade ?: "",
      metodo = EMetodo.TODOS,
    )
    return saci.findResposicaoProduto(filtro).mapNotNull { produto ->
      if (produto.marca != EMarcaReposicao.ENT.num) return@mapNotNull null

      val tipo = when (produto.metodo) {
        431  -> ETipoKardec.REPOSICAO
        432  -> ETipoKardec.RETORNO
        433  -> ETipoKardec.ACERTO
        else -> return@mapNotNull null
      }

      val mult = when (produto.metodo) {
        431  -> -1
        432  -> 1
        433  -> produto.multAcerto ?: 0
        else -> return@mapNotNull null
      }

      ProdutoKardec(
        loja = produto.loja ?: 0,
        prdno = produto.prdno ?: "",
        grade = produto.grade ?: "",
        data = produto.data,
        doc = produto.numero.toString(),
        tipo = tipo,
        qtde = mult * (produto.quantidade ?: 0),
        saldo = 0,
        userLogin = produto.entregueSNome ?: "",
        observacao = produto.observacao
      )
    }
  }

  fun saldoInicial(dataInicial: LocalDate): List<ProdutoKardec> {
    if (qtConferencia == null) {
      val list = saci.findSaldoData(
        loja = 4,
        codigo = codigo.toString(),
        grade = grade ?: "",
        dataInicial = dataInicial
      )
      return list.map { saldo ->
        ProdutoKardec(
          loja = saldo.storeno,
          prdno = saldo.prdno,
          grade = saldo.grade,
          data = dataInicial,
          doc = "Estoque",
          tipo = ETipoKardec.INICIAL,
          qtde = saldo.quant,
          saldo = 0,
          userLogin = "ADM",
          observacao = ""
        )
      }
    }

    val produtoKardec = ProdutoKardec(
      loja = 4,
      prdno = prdno,
      grade = grade ?: "",
      data = dataInicial,
      doc = "Estoque",
      tipo = ETipoKardec.INICIAL,
      qtde = qtConferencia,
      saldo = 0,
      userLogin = "ADM",
      observacao = ""
    )
    return listOf(produtoKardec)
  }

  fun acertoEstoque(dataInicial: LocalDate): List<ProdutoKardec> {
    val list = saci.findAcertoEstoque(
      loja = 4,
      codigo = codigo.toString(),
      grade = grade ?: "",
      dataInicial = dataInicial
    )
    return list.map { saldo ->
      ProdutoKardec(
        loja = saldo.loja,
        prdno = saldo.prdno,
        grade = saldo.grade,
        data = saldo.data,
        doc = saldo.pedido.toString(),
        tipo = ETipoKardec.ACERTO_ESTOQUE,
        qtde = saldo.quantidade,
        saldo = 0,
        userLogin = "ADM",
        observacao = saldo.observacao
      )
    }
  }

  fun limpaAcerto() {
    saci.removeAcertoProduto(this)
  }

  companion object {
    fun findProdutoEstoque(filter: FiltroProdutoEstoque): List<ProdutoEstoque> {
      return saci.findProdutoEstoque(filter)
    }

    fun update(produtos: List<ProdutoEstoque>) {
      saci.updateProdutoEstoque(produtos)
    }
  }
}

data class FiltroProdutoEstoque(
  val loja: Int = 4,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val localizacao: String?,
  val fornecedor: String,
  val centroLucro: Int = 0,
  val pedido: Int = 0,
  val estoque: EEstoque = EEstoque.TODOS,
  val saldo: Int = 0,
  val inativo: EInativo,
  val uso: EUso = EUso.TODOS,
  val listaUser: List<String>,
) {
  val prdno = if (codigo == 0) "" else codigo.toString().lpad(16, " ")
}
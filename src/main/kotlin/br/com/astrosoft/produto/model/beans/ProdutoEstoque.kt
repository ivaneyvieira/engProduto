package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userRessuprimentoLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoEstoque(
  var loja: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var unidade: String?,
  var grade: String?,
  var embalagem: Int?,
  var qtdEmbalagem: Int?,
  var estoque: Int?,
  var locSaci: String?,
  var locApp: String?,
  var codForn: Int,
  var fornecedor: String,
  var saldo: Int?,
  var dataInicial: LocalDate?,
) {
  fun update() {
    saci.updateProdutoEstoque(this)
  }

  fun recebimentos(dataInicial: LocalDate): List<ProdutoKardec> {
    val filtro = FiltroNotaRecebimentoProduto(
      loja = 0,
      pesquisa = "",
      marca = EMarcaRecebimento.RECEBIDO,
      dataFinal = null,
      dataInicial = dataInicial,
      localizacao = listOf("TODOS"),
      prdno = prdno ?: "",
      grade = grade ?: "SEM GRADE"
    )
    return saci.findNotaRecebimentoProduto(filtro).map { nota ->
      ProdutoKardec(
        loja = nota.loja ?: 0,
        prdno = prdno ?: "",
        grade = grade ?: "",
        data = nota.data,
        doc = nota.nfEntrada ?: "",
        tipo = "Recebimento",
        vencimento = nota.vencimento,
        qtde = nota.quant ?: 0,
        saldo = 0,
        userLogin = nota.login ?: ""
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
      codigo = this.codigo?.toString() ?: "",
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
          tipo = "Ressuprimento",
          qtde = (produto.qtQuantNF ?: 0) * mult,
          saldo = 0,
          userLogin = ressuprimento.login ?: ""
        )
      }
    }
  }

  fun ressuprimento(dataInicial: LocalDate): List<ProdutoKardec> {
    return ressuprimento(EMarcaRessuprimento.ENT, dataInicial) + ressuprimento(EMarcaRessuprimento.REC, dataInicial)
  }

  fun expedicao(dataInicial: LocalDate): List<ProdutoKardec> {
    val filtro = FiltroNota(
      marca = EMarcaNota.ENT,
      tipoNota = ETipoNotaFiscal.TODOS,
      loja = 0,
      pesquisa = "",
      notaEntrega = "T",
      prdno = prdno ?: "",
      grade = grade ?: "",
      dataInicial = dataInicial.minusDays(30),
      dataEntregaInicial = null,
      dataFinal = null
    )
    val notas = saci.findNotaSaida(filtro = filtro)
    return notas.flatMap { nota ->
      val tipo = if (nota.tipoNotaSaida == ETipoNotaFiscal.ENTRE_FUT.name) {
        "Entrega"
      } else {
        "Expedição"
      }

      //Validações
      val data = nota.dataEntrega ?: nota.data ?: return@flatMap emptyList()
      if (data < dataInicial) return@flatMap emptyList()

      nota.produtos(EMarcaNota.ENT, prdno ?: "", grade ?: "").map { produto ->
        ProdutoKardec(
          loja = nota.loja,
          prdno = prdno ?: "",
          grade = grade ?: "",
          data = data,
          doc = if (nota.notaEntrega.isNullOrBlank()) "${nota.numero}/${nota.serie}" else nota.notaEntrega ?: "",
          tipo = tipo,
          qtde = -(produto.quantidade ?: 0),
          saldo = 0,
          userLogin = (nota.usuarioExp ?: "").split("-").firstOrNull() ?: "",
        )
      }.distinctBy { it.doc }
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
      loja = 0,
      pesquisa = "",
      marca = EMarcaReposicao.ENT,
      localizacao = localizacao,
      dataInicial = dataInicial,
      dataFinal = null,
      codigo = codigo?.toString() ?: "",
      grade = grade ?: "",
    )
    return saci.findResposicaoProduto(filtro).mapNotNull { produto ->
      if(produto.marca == EMarcaReposicao.ENT.num) return@mapNotNull null
      ProdutoKardec(
        loja = produto.loja ?: 0,
        prdno = produto.prdno ?: "",
        grade = produto.grade ?: "",
        data = produto.data,
        doc = produto.numero.toString(),
        tipo = "Reposição Loja",
        qtde = -(produto.quantidade ?: 0),
        saldo = 0,
        userLogin = produto.entregueSNome ?: ""
      )
    }
  }

  fun saldoAnterior(dataInicial: LocalDate): List<ProdutoKardec> {
    val list = saci.findSaldoData(loja = 4, codigo = codigo.toString(), grade = grade ?: "", dataInicial = dataInicial)
    return list.map { saldo ->
      ProdutoKardec(
        loja = saldo.storeno,
        prdno = saldo.prdno,
        grade = saldo.grade,
        data = saldo.date,
        doc = "Estoque",
        tipo = "Inicial",
        qtde = saldo.quant,
        saldo = 0,
        userLogin = "ADM"
      )
    }
  }

    fun acertoEstoque(dataInicial: LocalDate): List<ProdutoKardec> {
    val list = saci.findAcertoEstoque(loja = 4, codigo = codigo.toString(), grade = grade ?: "", dataInicial = dataInicial)
    return list.map { saldo ->
      ProdutoKardec(
        loja = saldo.loja,
        prdno = saldo.prdno,
        grade = saldo.grade,
        data = saldo.data,
        doc = saldo.pedido.toString(),
        tipo = "Acerto Estoque",
        qtde = saldo.quantidade,
        saldo = 0,
        userLogin = "ADM"
      )
    }
  }

  companion object {
    fun findProdutoEstoque(filter: FiltroProdutoEstoque): List<ProdutoEstoque> {
      return saci.findProdutoEstoque(filter)
    }
  }
}

data class FiltroProdutoEstoque(
  val loja: Int,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val localizacao: String?,
  val fornecedor: String,
  val estoque: EEstoque = EEstoque.TODOS,
  val saldo: Int = 0,
)
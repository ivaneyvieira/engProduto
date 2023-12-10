package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class NotaEntradaVO(
  var id: Int,
  var numero: Int,
  var cancelado: String,
  var serie: Int,
  var dataEmissao: LocalDate?,
  var cnpjEmitente: String,
  var nomeFornecedor: String,
  var cnpjDestinatario: String,
  var ieEmitente: String,
  var ieDestinatario: String,
  var baseCalculoIcms: Double,
  var baseCalculoSt: Double,
  var valorTotalProdutos: Double,
  var valorTotalIcms: Double,
  var valorTotalSt: Double,
  var valorNota: Double?,
  var baseCalculoIssqn: Double,
  var chave: String,
  var status: String,
  var xmlAut: String,
  var xmlCancelado: String,
  var xmlNfe: String,
  var xmlDadosAdicionais: String
)
package br.com.astrosoft.devolucao.model

import java.time.LocalDate

class NotaEntradaVO(val id: Int,
                    val numero: Int,
                    val cancelado: String,
                    val serie: Int,
                    val dataEmissao: LocalDate?,
                    val cnpjEmitente: String,
                    val nomeFornecedor: String,
                    val cnpjDestinatario: String,
                    val ieEmitente: String,
                    val ieDestinatario: String,
                    val baseCalculoIcms: Double,
                    val baseCalculoSt: Double,
                    val valorTotalProdutos: Double,
                    val valorTotalIcms: Double,
                    val valorTotalSt: Double,
                    val valorNota: Double?,
                    val baseCalculoIssqn: Double,
                    val chave: String,
                    val status: String,
                    val xmlAut: String,
                    val xmlCancelado: String,
                    val xmlNfe: String,
                    val xmlDadosAdicionais: String)
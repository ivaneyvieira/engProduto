package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.TermoRecebimento

class PrintTermoCupom() : PrintText<TermoRecebimento>() {
  override fun printTitle(bean: TermoRecebimento) {
    writeln("Termo de Recebimento de Volume - NI ${bean.ni}", negrito = true, center = true)
    writeln("")
    writeln("    Declaramos para devidos fins quer recebemos os volumes dados")
    writeln("abaixo da nota fiscal de origem e Cte para posterior conferência")
    writeln("sujeito a notificações de irregularidades no  recebimento, como:")
    writeln("Avaria no Transporte, Falha  no Transporte,  Falta  de  Fabrica,")
    writeln("Validade, Defeito de fabricação,")
    writeln("")
    writeln("Dados Cadastrais", negrito = true, center = true)
    writeln("")
    writeln("Fornecedor: ${bean.dadosFornecedor.nome}")
    writeln("CNPJ: ${bean.dadosFornecedor.cnpj}")
    writeln("Endo: ${bean.dadosFornecedor.endereco}")
    writeln("Bairro: ${bean.dadosFornecedor.bairro}")
    writeln("Cidade: ${bean.dadosFornecedor.cidade}")
    writeln("Estado: ${bean.dadosFornecedor.uf}")
    writeln("")
    writeln("Transportadora: ${bean.dadosTransportadora.nome}")
    writeln("CNPJ: ${bean.dadosTransportadora.cnpj}")
    writeln("End: ${bean.dadosTransportadora.endereco}")
    writeln("Bairro: ${bean.dadosTransportadora.bairro}")
    writeln("Cidade: ${bean.dadosTransportadora.cidade}")
    writeln("Estado: ${bean.dadosTransportadora.uf}")
    writeln("")
    writeln("Cliente: ${bean.dadosCliente.nome}")
    writeln("CNPJ: ${bean.dadosCliente.cnpj}")
    writeln("End: ${bean.dadosCliente.endereco}")
    writeln("Bairro: ${bean.dadosCliente.bairro}")
    writeln("Cidade: ${bean.dadosCliente.cidade}")
    writeln("Estado: ${bean.dadosCliente.uf}")
    writeln("")
    writeln("Dados Fiscais", negrito = true, center = true)
    writeln("")
    writeln("Nota Fiscal: ${bean.dadosFornecedor.notaFiscal}")
    writeln("Emissão: ${bean.dadosFornecedor.emissao?.format() ?: ""}")
    writeln("Recebimento: ${bean.dadosFornecedor.recebimento?.format() ?: ""}")
    writeln("Valor: ${bean.dadosFornecedor.valor.format()}")
    writeln("Volumes: ${bean.dadosFornecedor.volumes?.format() ?: ""}")
    writeln("Peso Bruto: ${bean.dadosFornecedor.pesoBruto?.format() ?: ""}")
    writeln("")
    writeln("CT-e: ${bean.dadosTransportadora.cte}")
    writeln("Emissão: ${bean.dadosTransportadora.emissao?.format() ?: ""}")
    writeln("Recebimento: ${bean.dadosTransportadora.recebimento?.format() ?: ""}")
    writeln("Valor: ${bean.dadosTransportadora.valor.format()}")
    writeln("Volumes: ${bean.dadosTransportadora.volumes?.format() ?: ""}")
    writeln("Peso Bruto: ${bean.dadosTransportadora.pesoBruto?.format() ?: ""}")
    printLine()
  }

  override fun printSumary(bean: TermoRecebimento?) {
    writeln("")
    writeln("")
    writeln("________________________________")
    writeln("Nome:")
    writeln("CPF:")
    writeln("")
    writeln("")
  }
}
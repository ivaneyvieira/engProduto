package br.com.astrosoft.produto.model.beans

data class Iprd2Parameters(
  // Primary Keys and related fields
  val invno: Int,
  val prdno: String,
  val grade: String,

  // Quantities and costs
  val qtty: Int,
  val fob: Long,
  val cost: Long,
  val fob4: Long,
  val cost4: Long,
  val dfob: Double,

  // Impostos - ICMS
  val icms: Long,
  val icmsAliq: Int,
  val baseIcms: Long,
  val baseIcmsSubst: Long,
  val icmsSubst: Long,
  val reducaoBaseIcms: Long,
  val amtCredIcmsSN: Long,
  val amtIcmsDeson: Long,
  val amtIcmsDifer: Long,
  val amtIcmsEfet: Long,
  val aliqIcmsDifer: Int,
  val aliqIcmsEfet: Int,
  val aliqIcmsInter: Int,
  val aliqIcmsPart: Int,
  val aliqIcmsUfDest: Int,

  // Impostos - FCP
  val amtFcpSt: Long,
  val amtFcpStRet: Long,
  val amtFcpUfDest: Long,
  val baseFcp: Long,
  val baseFcpSt: Long,
  val baseFcpStRet: Long,
  val baseFcpUfDest: Long,
  val aliqFcp: Int,
  val aliqFcpSt: Int,
  val aliqFcpStRet: Int,
  val aliqFcpUfDest: Int,

  // Impostos - IPI
  val ipi: Int,
  val ipiAmt: Long,
  val baseIpi: Long,

  // Discount and other fields
  val discount: Long,
  val lucroTributado: Int,

  // Store and Sequence
  val storeno: Int,
  val seq: Int,

  // Flags
  val motIcmsDeson: Int,
  val percBaseOper: Int,
  val percCredSN: Int,
  val percRedIcmsEfet: Int,
  val percRedIcmsSt: Int,

  // Product and classification fields
  val cstIcms: String,
  val cstIpi: String
)


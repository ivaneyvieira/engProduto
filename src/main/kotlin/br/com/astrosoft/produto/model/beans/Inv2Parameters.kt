package br.com.astrosoft.produto.model.beans

data class Inv2Parameters(
    // Primary Keys and Related Fields
    val invno: Int,
    val vendno: Int,
    val ordno: Int,

    // Dates and Time Information
    val issueDate: Int,
    val dataSaida: Int,

    // Costs and Calculations
    val freight: Long,
    val baseCalculo: Long,
    val grossamt: Long,
    val substTrib: Long,
    val discount: Long,
    val prdamt: Long,
    val despesas: Long,
    val weight: Double,

    // Taxes and Related Fields
    val ipi: Long,
    val icm: Long,
    val baseIpi: Long,
    val aliq: Int,
    val cfo: Int,
    val icmsUfRemet: Long,
    val icmsDese: Long,

    // Shipping and Logistics
    val conhecimentoFrete: Int,
    val carrno: Int,
    val packages: Int,

    // Store Information
    val storeno: Int,
    val bookBits: Int,

    // Miscellaneous
    val l3: Int,
    val nfname: String,
    val invse: String
)

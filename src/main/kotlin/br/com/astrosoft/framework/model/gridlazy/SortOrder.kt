package br.com.astrosoft.framework.model.gridlazy

data class SortOrder(val field: String, val desc: Boolean) {
  fun sql() = if (desc) "$field DESC" else "$field ASC"
}
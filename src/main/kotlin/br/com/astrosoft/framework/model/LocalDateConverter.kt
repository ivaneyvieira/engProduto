package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.util.toLocalDate
import org.sql2o.converters.Converter
import org.sql2o.converters.ConverterException
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneOffset

class LocalDateConverter : Converter<LocalDate?> {
  @Throws(ConverterException::class)
  override fun convert(value: Any?): LocalDate? {
    return when (value) {
      is Date      -> value.toLocalDate()
      is Timestamp -> value.toLocalDate()
      else         -> null
    }
  }

  override fun toDatabaseParam(value: LocalDate?): Any? {
    value ?: return null
    return Date(value.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
  }
}
package stats.core

import org.joda.time.{DateTime, DateTimeZone}


trait Time {
  def tsNow: Long
}

object TimeImpl extends Time {
  def tsNow: Long = DateTime.now(DateTimeZone.UTC).getMillis
}

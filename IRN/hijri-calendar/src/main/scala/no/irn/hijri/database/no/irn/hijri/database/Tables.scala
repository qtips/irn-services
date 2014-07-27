package no.irn.hijri.database
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Changelog.ddl ++ HijriMonths.ddl
  
  /** Entity class storing rows of table Changelog
   *  @param changelogId Database column changelog_id AutoInc
   *  @param columnName Database column column_name 
   *  @param valueBefore Database column value_before 
   *  @param valueAfter Database column value_after 
   *  @param changestamp Database column changestamp  */
  case class ChangelogRow(changelogId: Int, columnName: String, valueBefore: Option[String], valueAfter: Option[String], changestamp: java.sql.Timestamp)
  /** GetResult implicit for fetching ChangelogRow objects using plain SQL queries */
  implicit def GetResultChangelogRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[java.sql.Timestamp]): GR[ChangelogRow] = GR{
    prs => import prs._
    ChangelogRow.tupled((<<[Int], <<[String], <<?[String], <<?[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table changelog. Objects of this class serve as prototypes for rows in queries. */
  class Changelog(_tableTag: Tag) extends Table[ChangelogRow](_tableTag, "changelog") {
    def * = (changelogId, columnName, valueBefore, valueAfter, changestamp) <> (ChangelogRow.tupled, ChangelogRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (changelogId.?, columnName.?, valueBefore, valueAfter, changestamp.?).shaped.<>({r=>import r._; _1.map(_=> ChangelogRow.tupled((_1.get, _2.get, _3, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column changelog_id AutoInc */
    val changelogId: Column[Int] = column[Int]("changelog_id", O.AutoInc)
    /** Database column column_name  */
    val columnName: Column[String] = column[String]("column_name")
    /** Database column value_before  */
    val valueBefore: Column[Option[String]] = column[Option[String]]("value_before")
    /** Database column value_after  */
    val valueAfter: Column[Option[String]] = column[Option[String]]("value_after")
    /** Database column changestamp  */
    val changestamp: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("changestamp")
    
    /** Primary key of Changelog (database name changelog_PK) */
    val pk = primaryKey("changelog_PK", (changelogId, changestamp))
  }
  /** Collection-like TableQuery object for table Changelog */
  lazy val Changelog = new TableQuery(tag => new Changelog(tag))
  
  /** Entity class storing rows of table HijriMonths
   *  @param hijriYear Database column hijri_year 
   *  @param hijriMonth Database column hijri_month 
   *  @param irnCalc Database column IRN_calc 
   *  @param irnAgreed Database column IRN_agreed 
   *  @param turkishCalc Database column turkish_calc 
   *  @param ummulQuran Database column ummul_quran 
   *  @param saAgreed Database column SA_agreed 
   *  @param changelogId Database column changelog_id  */
  case class HijriMonthsRow(hijriYear: Int, hijriMonth: Int, irnCalc: Option[java.sql.Date], irnAgreed: Option[java.sql.Date], turkishCalc: Option[java.sql.Date], ummulQuran: Option[java.sql.Date], saAgreed: Option[java.sql.Date], changelogId: Option[Int])
  /** GetResult implicit for fetching HijriMonthsRow objects using plain SQL queries */
  implicit def GetResultHijriMonthsRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[Int]]): GR[HijriMonthsRow] = GR{
    prs => import prs._
    HijriMonthsRow.tupled((<<[Int], <<[Int], <<?[java.sql.Date], <<?[java.sql.Date], <<?[java.sql.Date], <<?[java.sql.Date], <<?[java.sql.Date], <<?[Int]))
  }
  /** Table description of table hijri_months. Objects of this class serve as prototypes for rows in queries. */
  class HijriMonths(_tableTag: Tag) extends Table[HijriMonthsRow](_tableTag, "hijri_months") {
    def * = (hijriYear, hijriMonth, irnCalc, irnAgreed, turkishCalc, ummulQuran, saAgreed, changelogId) <> (HijriMonthsRow.tupled, HijriMonthsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (hijriYear.?, hijriMonth.?, irnCalc, irnAgreed, turkishCalc, ummulQuran, saAgreed, changelogId).shaped.<>({r=>import r._; _1.map(_=> HijriMonthsRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column hijri_year  */
    val hijriYear: Column[Int] = column[Int]("hijri_year")
    /** Database column hijri_month  */
    val hijriMonth: Column[Int] = column[Int]("hijri_month")
    /** Database column IRN_calc  */
    val irnCalc: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("IRN_calc")
    /** Database column IRN_agreed  */
    val irnAgreed: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("IRN_agreed")
    /** Database column turkish_calc  */
    val turkishCalc: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("turkish_calc")
    /** Database column ummul_quran  */
    val ummulQuran: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("ummul_quran")
    /** Database column SA_agreed  */
    val saAgreed: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("SA_agreed")
    /** Database column changelog_id  */
    val changelogId: Column[Option[Int]] = column[Option[Int]]("changelog_id")
    
    /** Primary key of HijriMonths (database name hijri_months_PK) */
    val pk = primaryKey("hijri_months_PK", (hijriYear, hijriMonth))
    
    /** Foreign key referencing Changelog (database name hijri_months_ibfk_1) */
    lazy val changelogFk = foreignKey("hijri_months_ibfk_1", changelogId, Changelog)(r => r.changelogId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table HijriMonths */
  lazy val HijriMonths = new TableQuery(tag => new HijriMonths(tag))
}
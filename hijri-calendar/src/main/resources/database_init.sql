CREATE DATABASE IF NOT EXISTS islamic_calendar;
USE islamic_calendar;

CREATE TABLE IF NOT EXISTS changelog (
  changelog_id INT         NOT NULL AUTO_INCREMENT,
  column_name  VARCHAR(32) NOT NULL,
  value_before VARCHAR(32),
  value_after  VARCHAR(32),
  changestamp  DATETIME    NOT NULL,
  PRIMARY KEY (changelog_id, changestamp),
  CONSTRAINT column_name CHECK (column_name IN (SELECT
                                                  `COLUMN_NAME`
                                                FROM `INFORMATION_SCHEMA`.`COLUMNS`
                                                WHERE `TABLE_SCHEMA` = 'qadeer'
                                                      AND `TABLE_NAME` = 'hijri_months'))
);

CREATE TABLE IF NOT EXISTS hijri_months (
  hijri_year   INT NOT NULL,
  hijri_month  INT NOT NULL,
  IRN_calc     DATE,
  IRN_agreed   DATE,
  turkish_calc DATE,
  ummul_quran  DATE,
  SA_agreed    DATE,
  changelog_id INT,
  CONSTRAINT hijri_year CHECK (hijri_year > 0),
  CONSTRAINT hijri_month CHECK (hijri_month BETWEEN 1 AND 12),
  PRIMARY KEY (hijri_year, hijri_month),
  FOREIGN KEY (changelog_id) REFERENCES changelog (changelog_id)
);

COMMIT;

select s7.`hijri_year`, s7.`hijri_month`, s7.`IRN_calc`, s7.`IRN_agreed`, s7.`turkish_calc`, s7.`ummul_quran`, s7.`SA_agreed`, s7.`changelog_id` from (select s27.`IRN_calc` as `IRN_calc`, s27.`hijri_year` as `hijri_year`, s27.`ummul_quran` as `ummul_quran`, s27.`changelog_id` as `changelog_id`, s27.`hijri_month` as `hijri_month`, s27.`IRN_agreed` as `IRN_agreed`, s27.`turkish_calc` as `turkish_calc`, s27.`SA_agreed` as `SA_agreed` from `hijri_months` s27 where s27.`IRN_calc` <= {d '2012-02-01'} order by s27.`IRN_calc` desc limit 1) s7
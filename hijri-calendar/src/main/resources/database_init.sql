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

SELECT
  s7.`hijri_year`,
  s7.`hijri_month`,
  s7.`IRN_calc`,
  s7.`IRN_agreed`,
  s7.`turkish_calc`,
  s7.`ummul_quran`,
  s7.`SA_agreed`,
  s7.`changelog_id`
FROM (SELECT
        s27.`IRN_calc`     AS `IRN_calc`,
        s27.`hijri_year`   AS `hijri_year`,
        s27.`ummul_quran`  AS `ummul_quran`,
        s27.`changelog_id` AS `changelog_id`,
        s27.`hijri_month`  AS `hijri_month`,
        s27.`IRN_agreed`   AS `IRN_agreed`,
        s27.`turkish_calc` AS `turkish_calc`,
        s27.`SA_agreed`    AS `SA_agreed`
      FROM `hijri_months` s27
      WHERE s27.`IRN_calc` <= {d '2012-02-01'}
      ORDER BY s27.`IRN_calc` DESC
      LIMIT 1) s7


SELECT
  s9.`hijri_year`,
  s9.`hijri_month`,
  s9.`IRN_calc`,
  s9.`IRN_agreed`,
  s9.`turkish_calc`,
  s9.`ummul_quran`,
  s9.`SA_agreed`,
  s9.`changelog_id`
FROM (SELECT
        s3.`IRN_calc`     AS `IRN_calc`,
        s3.`hijri_year`   AS `hijri_year`,
        s3.`ummul_quran`  AS `ummul_quran`,
        s3.`changelog_id` AS `changelog_id`,
        s3.`hijri_month`  AS `hijri_month`,
        s3.`IRN_agreed`   AS `IRN_agreed`,
        s3.`turkish_calc` AS `turkish_calc`,
        s3.`SA_agreed`    AS `SA_agreed`
      FROM (SELECT
              s29.`IRN_calc`     AS `IRN_calc`,
              s29.`hijri_year`   AS `hijri_year`,
              s29.`ummul_quran`  AS `ummul_quran`,
              s29.`changelog_id` AS `changelog_id`,
              s29.`hijri_month`  AS `hijri_month`,
              s29.`IRN_agreed`   AS `IRN_agreed`,
              s29.`turkish_calc` AS `turkish_calc`,
              s29.`SA_agreed`    AS `SA_agreed`
            FROM `hijri_months` s29
            WHERE s29.`IRN_calc` <= {d '2016-05-01'}
            ORDER BY s29.`IRN_calc` DESC
            LIMIT 1) s3
      WHERE s3.`IRN_calc` >= {d '2016-06-01'}
      ORDER BY s3.`IRN_calc`
      LIMIT 1) s9;

SELECT
  *
FROM hijri_months h1
WHERE h1.irn_calc >= (SELECT
                        h2.IRN_calc
                      FROM hijri_months h2
                      WHERE h2.irn_calc <= '2011-01-04'
                      ORDER BY h2.irn_calc DESC
                      LIMIT 1) AND h1.IRN_calc <= (SELECT
                                                     h3.irn_calc
                                                   FROM hijri_months h3
                                                   WHERE h3.irn_calc >= '2012-07-03'
                                                   ORDER BY h3.irn_calc ASC
                                                   LIMIT 1);
SELECT
  h3.irn_calc
FROM hijri_months h3
WHERE h3.irn_calc >= '2012-07-03'
ORDER BY h3.irn_calc ASC
LIMIT 1;
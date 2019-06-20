@GrabConfig(systemClassLoader=true)
@Grab(group='mysql', module='mysql-connector-java', version='5.1.6')

def sql = groovy.sql.Sql.newInstance('jdbc:mysql://localhost:3306/sq?allowMultiQueries=true',
                                     'root',
                                     'test',
                                     'com.mysql.jdbc.Driver')

setSchema = """
    DROP TABLE IF EXISTS squirrel_tag;
    CREATE TABLE squirrel_tag (`id` INT NOT NULL AUTO_INCREMENT,
                               `regex` VARCHAR(128) NULL,
                               `tag` VARCHAR(64) NOT NULL,
                               `species` ENUM('DEVICE', 'COUNTRY', 'APP', 'RESELLER',
                                              'PLAN', 'PLAN_GROUP', 'MERCHANT_GROUP') NOT NULL,
                               PRIMARY KEY(`id`));

    DROP TABLE IF EXISTS squirrel_cage;
    CREATE TABLE squirrel_cage (`order` INT NOT NULL,
                                `if` VARCHAR(1024) NOT NULL,
                                `do` VARCHAR(1024) NOT NULL,
                                `break` TINYINT NOT NULL DEFAULT 0,
                                PRIMARY KEY (`order`) );
"""
sql.execute(setSchema);

seedTags = """
    INSERT INTO squirrel_tag(tag, regex, species)
         VALUES ('station_pro'         , 'C[0-9A-Z]*53'  , 'DEVICE')
              , ('station_2018'        , 'C[0-9A-Z]*50'  , 'DEVICE')
              , ('station'             , 'C[0-9A-Z]*10'  , 'DEVICE')
              , ('wireless_manager'    , 'JBCS0CPRQW9WM', 'APP')
              , ('station_pro_friendly', 'FKNKX0SAATWAG', 'PLAN')
              , ('station_pro_friendly', 'R9HQKT7HRB3Y2', 'PLAN')
              , ('station_pro_friendly', 'JG7ZCS18WESD0', 'PLAN')
              , ('station_pro_friendly', 'ZDXCS8P3EFPAT', 'PLAN')
              , ('station_pro_friendly', 'VFT5SMK0KWE98', 'PLAN')
              , ('station_pro_friendly', 'TV771VD339C1C', 'PLAN')
              , ('station_friendly'    , 'Q94B0SXGP7JJ6', 'PLAN')
              , ('station_friendly'    , '6QXM5AF0P81VJ', 'PLAN')
              , ('station_friendly'    , 'Y68MS951FTNVA', 'PLAN')
              , ('station_friendly'    , '4BFG6MKK2TMJC', 'PLAN')
              , ('upsell_pitch'        , 'TY4PVSPS6YBW2', 'RESELLER')
              , ('bill_wm_anyway'      , 'DE'           , 'COUNTRY')
    ;
"""
sql.execute(seedTags);

seedSquirrels = """
    INSERT INTO squirrel_cage (`order`, `if`
                                      , `do`)
         VALUES (100 , "anyDevicesTagged('station')"
                     , "restrictPlans('station_friendly')")
              , (200 , "anyDevicesTagged('station_pro')"
                     , "restrictPlans('station_pro_friendly')")
              , (300 , "reseller.tags.contains('upsell_pitch')"
                     , "showPlanSelectionScreen = false")
              , (400 , "anyAppsTagged('wireless_manager') && country.tags.contains('bill_wm_anyway')"
                     , "apps.find { it.tags.contains('bill_wm_anyway') }.suppressed = false")
    ;
"""
sql.execute(seedSquirrels);
sql.close()

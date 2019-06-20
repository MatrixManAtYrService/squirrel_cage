@GrabConfig(systemClassLoader=true)
@Grab(group='mysql', module='mysql-connector-java', version='5.1.6')
import groovy.transform.ToString
import groovy.util.Eval
import static groovy.json.JsonOutput.*


def sql = groovy.sql.Sql.newInstance('jdbc:mysql://localhost:3306/sq',
                                     'root',
                                     'test',
                                     'com.mysql.jdbc.Driver')

@ToString(includeNames=true)
class T{

    def thing;
    def tags = [];
    T(def _thing){
        thing = _thing;
    }
}

preSquirrel = [ 'show_plan_selection_screen'    : true,
                'show_app_confirmation_screens' : [ 'com.clover.whatever.wirelessManager.areYouSure' ],
                'available_plans'               : [ 'R9HQKT7HRB3Y2', 'WRVQCQGGWJ9QE', '7M6XAHBW29Q94', 'TV771VD339C1C' ],
                'billable_devices'              : [ 'C010UQ72930043', 'C030UQ71050253' ],
                'installed_apps'                : [ 'JBCS0CPRQW9WM', 'X1QY38CCAQY8E', '2GYRXXRZDQFNT' ],
                'country'                       : 'US',
                'reseller'                      : 'TY4PVSPS6YBW2'
              ]
println "PRE SQUIRREL PARAMETERS"
println prettyPrint(toJson(preSquirrel))
println ""

// PLAN
// stage squirrels from legacy
plans = []
preSquirrel['available_plans'].each{ planUuid ->
    plans.add(new T(planUuid))
}

// tag them if applicable
sql.eachRow("SELECT tag, regex FROM squirrel_tag WHERE species = 'PLAN';"){ row ->
    plans.each { plan ->
        if (plan.thing ==~ row.regex){
            plan.tags.add(row.tag);
        }
    }
}

// DEVICE
// stage squirrels from legacy
devices = []
preSquirrel['billable_devices'].each{ deviceSerial ->
    devices.add(new T(deviceSerial))
}

// tag them if applicable
sql.eachRow("SELECT tag, regex FROM squirrel_tag WHERE species = 'DEVICE';"){ row ->
    devices.each { device ->
        if (device.thing ==~ row.regex){
            device.tags.add(row.tag);
        }
    }
}

// APP
// stage squirrels from legacy
apps = []
preSquirrel['installed_apps'].each{ appUuid ->
    apps.add(new T(appUuid))
}

// tag them if applicable
sql.eachRow("SELECT tag, regex FROM squirrel_tag WHERE species = 'APP';"){ row ->
    apps.each { app ->
        if (app.thing ==~ row.regex){
            app.tags.add(row.tag)
        }
    }
}

// COUNTRY

// stage squirrel from legacy
country = new T(preSquirrel['country'])

// tag it if applicable
sql.eachRow("SELECT tag, regex FROM squirrel_tag WHERE species = 'COUNTRY';"){ row ->
        if (country.thing ==~ row.regex){
            country.tags.add(row.tag)
    }
}

// stage squirrel from legacy
reseller = new T(preSquirrel['reseller'])

// tag it if applicable
sql.eachRow("SELECT tag, regex FROM squirrel_tag WHERE species = 'RESELLER';"){ row ->
        if (reseller.thing ==~ row.regex){
            reseller.tags.add(row.tag)
    }
}

// TAGGING DONE
// show progress
println "INPUT WITH TAGS"
input = [ "plans" : plans,
          "devices" : devices,
          "apps" : apps ,
          "reseller" : reseller,
          "country" : country ]
println prettyPrint(toJson(input))
println ""

// functions needed during squirrel mixing
preamble = """
    def anyDevicesTagged(tag){
        return devices.any{ it.tags.any { it == tag } }
    }

    def anyAppsTagged(tag){
        return apps.any{ it.tags.any { it == tag } }
    }

    def restrictPlans(tag){
        newPlans = []
        plans.each{
            if (it.tags.contains(tag)) {
                newPlans.add(it)
            }
        }
        plans = newPlans
    }
"""


// inject context into the squirrelcage
def substrate = new Binding(plans: plans,
                            devices: devices,
                            apps: apps,
                            reseller: reseller,
                            country: country,
                            showPlanSelectionScreen: preSquirrel['show_plan_selection_screen'],
                            showAppConfirmationScreens: preSquirrel['show_app_confirmation_screens'])

def cage = new GroovyShell(substrate)

plan = [ preamble ]

sql.eachRow("SELECT `if`, `do` FROM squirrel_cage ORDER BY `order`;"){ row ->
    condition = row.if
    action = row.do
    entry = """
    if ( $condition ) {
        $action
    }
"""
    plan.add(entry)
}

scriptStr = plan.join('\n')

println "RUN PLAN:"
println scriptStr

println "COMMENCE SQUIRREL MIXING...."
script = cage.parse(scriptStr)
script.run()
println ""

// recover context from cage
plans = substrate.getProperty('plans')
devices = substrate.getProperty('devices')
apps = substrate.getProperty('apps')
reseller = substrate.getProperty('reseller')
country = substrate.getProperty('country')
showPlanSelectionScreen = substrate.getProperty('showPlanSelectionScreen')
showAppConfirmationScreens = substrate.getProperty('showAppConfirmationScreens')

postSquirrel = [ 'show_plan_selection_screen'    : showPlanSelectionScreen,
                'show_app_confirmation_screens' : showAppConfirmationScreens,
                'available_plans'               : plans.thing,
                'billable_devices'              : devices.thing,
                'installed_apps'                : apps.thing,
                'country'                       : country.thing,
                'reseller'                      : reseller.thing
              ]
println "POST SQUIRREL PARAMETERS"
println prettyPrint(toJson(postSquirrel))

import com.trinary.ExcelDB.State
import com.trinary.ExcelDB.ExcelDBConfig
import com.trinary.ExcelDB.User

class BootStrap {

    def init = { servletContext ->
        if (!ExcelDBConfig.findByConfigKey("markupPercentage")) {
            new ExcelDBConfig(configKey: "markupPercentage", configValue: "0.0").save()
        }
        
        if (!User.findByUsername("admin")) {
            new User(username: "admin", password: "admin").save()
        }
		
		if (!State.findByKey("outdated")) {
			new State(key: "outdated", value: "false").save()
		}
		
		if (!State.findByKey("lastGenerated")) {
			new State(key: "lastGenerated", value: "").save()
		}
    }
    def destroy = {
    }
}

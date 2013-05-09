import com.trinary.ExcelDB.State
import com.trinary.ExcelDB.ExcelDBConfig
import com.trinary.ExcelDB.User
import com.trinary.ExcelDB.Role
import com.trinary.ExcelDB.UserRole

class BootStrap {

    def init = { servletContext ->
        if (!ExcelDBConfig.findByConfigKey("markupPercentage")) {
            new ExcelDBConfig(configKey: "markupPercentage", configValue: "0.0").save()
        }
		
		def adminRole = Role.findByAuthority("ROLE_ADMIN")
		def userRole  = Role.findByAuthority("ROLE_USER")
		def adminUser = User.findByUsername("admin")
		
		if (!adminRole) {
			adminRole = new Role(authority: "ROLE_ADMIN").save()
		}
		
		if (!userRole) {
			new Role(authority: "ROLE_USER").save()
		}
        
        if (!adminUser) {
            adminUser = new User(username: "admin", password: "password", enabled: "true").save()
        }
		
		UserRole.create adminUser, adminRole, true
    }
    def destroy = {
    }
}

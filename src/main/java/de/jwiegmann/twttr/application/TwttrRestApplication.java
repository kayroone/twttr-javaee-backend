package de.jwiegmann.twttr.application;

import org.eclipse.microprofile.auth.LoginConfig;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("service")

@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"user-role", "USER"})
@OpenAPIDefinition(info =
@Info(title = "Twttr Application", description = "Provides access to the API operations", version = "1.0",
        contact = @Contact(email = "jw@jwiegmann.de"),
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")),
        servers = @Server(url = "http://{host}:{port}", variables = {
                @ServerVariable(name = "host", defaultValue = "localhost"),
                @ServerVariable(name = "port", defaultValue = "8081"),
        }))
public class TwttrRestApplication extends Application {
}

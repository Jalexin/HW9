import javax.servlet.annotation.WebServlet;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.io.IOException;


@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;
    private final TimeZoneParams queryParams = new TimeZoneParams();
    public void init() {
        ServletContext servletContext = getServletContext();
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(false);
        engine = new TemplateEngine();
        resolver.setOrder(engine.getTemplateResolvers().size());
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        ZoneId zoneId = ZoneId.of(queryParams.parseTimeZone(req));
        Clock clock = Clock.system(zoneId);
        String time = LocalDateTime.now(clock).format(DateTimeFormatter.
                ofPattern("yyyy-MM-dd hh:mm:ss ")) + zoneId;
        resp.setContentType("text/html; charset=utf-8");
        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", time));
        resp.addCookie(new Cookie("lastTimezone", zoneId.toString()));
        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
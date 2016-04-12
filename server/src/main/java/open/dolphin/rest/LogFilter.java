package open.dolphin.rest;

import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import open.dolphin.mbean.UserCache;
import open.dolphin.session.UserServiceBean;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@WebFilter(urlPatterns = {"/resources/*"}, asyncSupported = true)
public class LogFilter implements Filter {

    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    private static final String UNAUTHORIZED_USER = "Unauthorized user: ";
    
    private static final String SYSAD_USER_ID = "1.3.6.1.4.1.9414.10.1:dolphin";
    private static final String SYSAD_PASSWORD = "36cdf8b887a5cffc78dcd5c08991b993";
    private static final String SYSAD_PATH = "dolphin";

    @Inject
    private UserServiceBean userService;
    
    @Inject
    private UserCache userCache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        
        if (req.getRequestURI().endsWith("identityToken")) {
            chain.doFilter(request, response);
            return;
        }
        
        String userName;
        String password;
        boolean authentication;
        
        // Headerから取得する
        userName = req.getHeader(USER_NAME);
        password = req.getHeader(PASSWORD);
        authentication = password.equals(userCache.getMap().get(userName));
        
        if (!authentication) {
            
            String requestURI = req.getRequestURI();
            authentication = authentication || (SYSAD_USER_ID.equals(userName) && SYSAD_PASSWORD.equals(password) && requestURI.endsWith(SYSAD_PATH));
            
            if (!authentication) {
                
                authentication = userService.authenticate(userName, password);
                
                if (!authentication) {
                    HttpServletResponse res = (HttpServletResponse)response;
                    StringBuilder sbd = new StringBuilder();
                    sbd.append(UNAUTHORIZED_USER);
                    sbd.append(userName).append(": ").append(req.getRequestURI());
                    String msg = sbd.toString();
                    Logger.getLogger("open.dolphin").warning(msg);
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                } else {
                    userCache.getMap().put(userName, password);
                }
            }
        } 

        BlockWrapper wrapper = new BlockWrapper(req);
        wrapper.setRemoteUser(userName);
        
        StringBuilder sb = new StringBuilder();
        sb.append(wrapper.getRemoteAddr()).append(" ");
        sb.append(wrapper.getShortUser()).append(" ");
        sb.append(wrapper.getMethod()).append(" ");
//minagawa^ VisitTouch logを分ける        
        String uri = wrapper.getRequestURIForLog();
        sb.append(uri);
        if (uri.startsWith("/jtouch")) {
            Logger.getLogger("visit.touch").info(sb.toString());
        } else {
            Logger.getLogger("open.dolphin").info(sb.toString());
        }
//minagawa 

        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {
    }
}

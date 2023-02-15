package com.apidocprotector.filter;

import com.apidocprotector.library.ApiDocProtectorLibrary;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
public class DisableUrlSessionFilter extends ApiDocProtectorLibrary implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {

        if (!(servletRequest instanceof HttpServletRequest)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if (httpRequest.isRequestedSessionIdFromURL()) {
            HttpSession session = httpRequest.getSession();
            if (session != null) {
                session.invalidate();
            }
        }

        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
            @Override
            public String encodeRedirectURL(String url) {
                return url;
            }
            @Override
            public String encodeURL(String url) {
                return url;
            }
        };

        chain.doFilter(servletRequest, wrappedResponse);
    }

    public void init(FilterConfig config) throws ServletException {}

    public void destroy() {}
}

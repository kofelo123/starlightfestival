package com.thearc.interceptor;

import com.thearc.domain.UserVO;
import com.thearc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  private UserService service;
  
  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response, Object handler) throws Exception {
    
    HttpSession session = request.getSession();   
  
    if(session.getAttribute("login") == null){
    
      log.info("current user is not logined");
      
      saveDest(request);
      
      Cookie loginCookie = WebUtils.getCookie(request, "loginCookie");
      
      if(loginCookie != null) { 
        
        UserVO userVO = service.checkLoginBefore(loginCookie.getValue());
        
        log.info("USERVO: " + userVO);
        
        if(userVO != null){
          session.setAttribute("login", userVO);
          
          authorization(request,response);
          return true;
        }
        
      }

      response.sendRedirect("/thearc/user/login");
      return false;
    }
    authorization(request,response);

    return true;
  }  
  

  private void saveDest(HttpServletRequest req) {///인증필요에 의한 로그인 이후 가려했던 목적지 저장위한것.

    String uri = req.getRequestURI();

    String query = req.getQueryString();

    if (query == null || query.equals("null")) {
      query = "";
    } else {
      query = "?" + query;
    }

    if (req.getMethod().equals("GET")) {
      log.info("dest: " + (uri + query));
      req.getSession().setAttribute("dest", uri + query);
    }
  }

  private void authorization(HttpServletRequest request,HttpServletResponse response) throws Exception{
	
	  	String[] url = request.getRequestURI().split("/");
	    String pathName = url[url.length-1];
	    System.out.println("Test:"+pathName); //마지막 path값 얻어오기 -> 이걸로 권한 체크할것..
	    HttpSession session = request.getSession();
	    
/*	    if(pathName.equals("supporter")){
	    	UserVO vo = (UserVO) session.getAttribute("login");
	    	if(vo.getAuthority().equals("user")){
	    		response.sendRedirect("/thearc/error403");
	    	}
	    }
	    
	    pathName=url[url.length-2]+"/"+url[url.length-1];
	    if(pathName.equals("register/notice")){
	    	UserVO vo = (UserVO) session.getAttribute("login");
	    	if(vo.getAuthority().equals("user")||vo.getAuthority().equals("supporter")){
	    		response.sendRedirect("/thearc/error403");
	    	}
	    		
	    }*/
	    
	  
	  
  }

}


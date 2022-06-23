package com.kh.mvc.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kh.mvc.member.model.dto.Member;
import com.kh.mvc.member.model.dto.MemberRole;

/**
 * Servlet Filter implementation class AdminFilter
 */
// admin으로 시작하는 모든 필터
@WebFilter("/admin/*")
public class AdminFilter implements Filter {
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
//	로그인한 사용자가 관리자일때만 member보여주기
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		HttpSession session = httpReq.getSession();
		
		Member loginMember = (Member) session.getAttribute("loginMember");
		
		if(loginMember == null || loginMember.getMemberRole() != MemberRole.A ) {
			session.setAttribute("msg", "관리자만 이용 가능합니다.");
			httpRes.sendRedirect(httpReq.getContextPath() + "/");
			
			return;
		}
		chain.doFilter(request, response);
	}


}

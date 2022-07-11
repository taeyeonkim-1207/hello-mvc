package com.kh.mvc.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.member.model.dto.Member;
import com.kh.mvc.member.model.service.MemberService;

/**
 * Servlet implementation class CheckIdDuplicateServlet
 */
@WebServlet("/member/checkIdDuplicate")
public class CheckIdDuplicateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService = new MemberService();
	/**
	 * select * from member where member_id = ?
	 * - member객체반환 -> 해당id 사용불가
	 * - null -> 해당id 사용가능
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. 인코딩처리
		request.setCharacterEncoding("utf-8");
		// 2. 사용자입력값 처리
		String memberId = request.getParameter("memberId");
		System.out.println("memberId = " + memberId);
		// 3. 업무로직
		Member member = memberService.findById(memberId);
		boolean available = member == null;
		System.out.println("available = " + available);
		// 4. 응답처리

		request.setAttribute("available", available); //이래야 jsp에서 꺼내쓸 수 있음
		
		request.getRequestDispatcher("/WEB-INF/views/member/checkIdDuplicate.jsp")
			.forward(request, response);
		
	}

}

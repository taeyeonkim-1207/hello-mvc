package com.kh.mvc.board.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.board.model.service.BoardService;

/**
 * Servlet implementation class BoardCommentDeleteServlet
 */
@WebServlet("/board/boardCommentDelete")
public class BoardCommentDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardService boardService = new BoardService();
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			// 1.사용자입력값 처리(삭제하고싶은 게시글번호)
			int no = Integer.parseInt(request.getParameter("no"));
			
			// 2.업무로직
			int result = boardService.deleteBoardComment(no);
			
			// 3.리다이렉트
			response.sendRedirect(request.getHeader("Referer"));
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			
		}
	
	}

}

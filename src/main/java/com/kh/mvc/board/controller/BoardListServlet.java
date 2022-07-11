package com.kh.mvc.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.board.model.dto.Board;
import com.kh.mvc.board.model.service.BoardService;
import com.kh.mvc.common.HelloMvcUtils;


/**
 * Servlet implementation class BoardServlet
 */
@WebServlet("/board/boardList")
public class BoardListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardService boardService = new BoardService();
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * 사용자입력값처리 cPage
	 * 서비스단 요청 - 반환타입?
	 * 예외처리
	 * 포워딩
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//1. 사용자입력값(페이징바)
			int numPerPage = 5;
			int cPage = 1; //기본값설정
			
			try {
				cPage = Integer.parseInt(request.getParameter("cPage"));
			}catch(NumberFormatException e) {}
		
			int start = (cPage - 1) * numPerPage + 1;
			int end = cPage * numPerPage;
			
			Map<String, Object> param = new HashMap<>();
			param.put("start", start);
			param.put("end", end);
			
			//2. 업무로직
			//a.content 영역
			List<Board> list = boardService.findAll(param);
			System.out.println("list = " + list);
			
			
			//b.pagebar 영역
			int totalContent = boardService.getTotalContent();
			String url = request.getRequestURI(); // /mvc/board/boardList
			String pagebar = 
					HelloMvcUtils.getPagebar(cPage, numPerPage, totalContent, url);
					
			//3. view단처리
			request.setAttribute("list", list);
			request.setAttribute("pagebar", pagebar);
			request.getRequestDispatcher("/WEB-INF/views/board/boardList.jsp")
				.forward(request, response);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}

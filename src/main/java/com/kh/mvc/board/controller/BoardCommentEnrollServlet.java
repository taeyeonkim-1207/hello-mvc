package com.kh.mvc.board.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.board.model.dto.Board;
import com.kh.mvc.board.model.dto.BoardComment;
import com.kh.mvc.board.model.dto.CommentLevel;
import com.kh.mvc.board.model.service.BoardService;
import com.kh.mvc.notification.model.service.NotificationService;

/**
 * Servlet implementation class BoardCommentEnrollServlet
 */
@WebServlet("/board/boardCommentEnroll")
public class BoardCommentEnrollServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardService boardService = new BoardService();
	//댓글알림
	private NotificationService notificationService = new NotificationService();
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//1.사용자입력값 처리
			//BoardComment객체 생성
			CommentLevel commentLevel = CommentLevel.valueOf(Integer.parseInt(request.getParameter("commentLevel")));
			String writer = request.getParameter("writer");
			String content = request.getParameter("content");
			int boardNo = Integer.parseInt(request.getParameter("boardNo"));
			int commentRef = Integer.parseInt(request.getParameter("commentRef"));
			BoardComment boardComment = new BoardComment(0, commentLevel, writer, content, boardNo, commentRef, null);  
			System.out.println("boardComment = " + boardComment);
			
			//2.업무로직
			int result = boardService.insertBoardComment(boardComment);
			
			//게시글 작성자에게 새 댓글알림 
			Board board = boardService.findByNo(boardNo);
			result = notificationService.notifyNewComment(board);
			
			//3.redirect 응답
			response.sendRedirect(request.getContextPath()+ "/board/boardView?no=" + boardNo);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}

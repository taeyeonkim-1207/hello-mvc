package com.kh.mvc.board.controller;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.board.model.dto.Attachment;
import com.kh.mvc.board.model.service.BoardService;

/**
 * Servlet implementation class BoardDeleteServlet
 */
@WebServlet("/board/boardDelete")
public class BoardDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardService boardService = new BoardService();
	/**
	 * 1. 저장된 첨부파일 조회 및 삭제 (java.io.File#delete)
	 * 2. board 삭제(on delete cascade에 의해서 attachment레코드도 자동으로 연쇄삭제 될 것임)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			// 1.사용자입력값 처리(삭제하고싶은 게시글번호)
			int no = Integer.parseInt(request.getParameter("no"));
			System.out.println("no= " + no);
			
			// 2.업무로직
//			 a.저장된 첨부파일 조회 및 삭제 (java.io.File#delete)
			List<Attachment> attachments = boardService.findAttachmentByBoardNo(no);
			System.out.println("attachments = " + attachments);
			if(attachments != null && !attachments.isEmpty()) {
				
				String saveDirectory = getServletContext().getRealPath("/upload/board");
				for(Attachment attach : attachments) {
					File delFile = new File(saveDirectory, attach.getRenamedFilename());
					boolean hasDeleted = delFile.delete();
					
//					파일이동
//					File destFile = new File(newDirectory, attach.getRenamedFilename());
//					delFile.renameTo(destFile);
					System.out.println("[파일삭제 " + attach.getRenamedFilename() + " : " + hasDeleted);
				}
			}
//			  b.board 삭제(on delete cascade에 의해서 attachment레코드도 자동으로 연쇄삭제 될 것임)
			int result = boardService.deleteBoard(no);
		
			// 3.리다이렉트
			request.getSession().setAttribute("msg", "게시글을 성공적으로 삭제했습니다.");
			response.sendRedirect(request.getContextPath()+"/board/boardList");
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			
		}
	}

}

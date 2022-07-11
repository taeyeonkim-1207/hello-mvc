package com.kh.mvc.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.board.model.dto.Attachment;
import com.kh.mvc.board.model.dto.Board;
import com.kh.mvc.board.model.dto.BoardExt;
import com.kh.mvc.board.model.service.BoardService;
import com.kh.mvc.common.HelloMvcFileRenamePolicy;
import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class BoardUpateServlet
 */
@WebServlet("/board/boardUpdate")
public class BoardUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardService boardService = new BoardService();
	/**
	 * GET 수정폼 요청
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//사용자입력값을 바탕으로 게시글 내용 조회
		try {
			int no = Integer.parseInt(request.getParameter("no"));
			Board board = boardService.findByNo(no);
			request.setAttribute("board", board);
			request.getRequestDispatcher("/WEB-INF/views/board/boardUpdate.jsp")
			.forward(request, response);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
			
		}
	}


	/**
	 * POST db에 update요청
	 * fileupload는 두가지로 나뉜다.
	 * (첨부파일이 포함된 게시글 수정)
	 * - 1.서버컴퓨터에 파일저장 - cos.jar를이용
	 * 		-MultipartRequest객체 생성 이때 준비물↓
	 * 			-HttpServletRequest를 제공해야함
	 * 			-saveDirectory
	 * 			-maxPostSize (용량제한)
	 * 			-encoding
	 * 			-FileRenamePolicy 객체 - DefaultFileRenamePolicy(기본)
	 * 		*기존 request객체가 아닌 MultipartRequest객체에서 모든 사용자 입력값을 가져와야 한다.
	 * 
	 * - 2.저장된 파일 정보를 attachment 레코드로 등록(DB에 insert)
	 *  	board insert,(중간에 select) attachment insert를 같이 처리해줘야함(transaction)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			// 1.서버컴퓨터에 파일 저장
			ServletContext application = getServletContext();
			String saveDirectory = application.getRealPath("/upload/board");
			System.out.println("saveD" + saveDirectory);
			int maxPostSize = 1024 * 1024 * 10; //10MB
			String encoding = "utf-8";
			HelloMvcFileRenamePolicy policy = new HelloMvcFileRenamePolicy();
			
			MultipartRequest multiReq = new MultipartRequest(
					request, saveDirectory, maxPostSize, encoding, policy);
			
			// 2.db update처리
			
			// 삭제파일 처리
			String[] delFiles = multiReq.getParameterValues("delFile");
			if(delFiles != null) {
				for(String temp : delFiles) {
					int attachNo = Integer.parseInt(temp);
					
					//Attachment만 찾기(조회)
					//첨부파일 삭제
					Attachment attach = boardService.findAttachmentByNo(attachNo);
					File delFile = new File(saveDirectory, attach.getRenamedFilename());
					delFile.delete();
					
					//db레코드 삭제 
					int result = boardService.deleteAttachment(attachNo);
					System.out.println("첨부파일" + attachNo + "번 삭제 !! " + attach.getRenamedFilename());
				}
			}
			
			
			int no = Integer.parseInt(multiReq.getParameter("no"));
			String title = multiReq.getParameter("title");
			String writer = multiReq.getParameter("writer");
			String content = multiReq.getParameter("content");
			BoardExt board = new BoardExt(no, title, writer, content, 0, null); //사용자입력한 값만 채우는 형태
		
			Enumeration<String> filenames = multiReq.getFileNames();
			while(filenames.hasMoreElements()) {
				String filename = filenames.nextElement();
				File upFile = multiReq.getFile(filename);
				if(upFile != null) {
					Attachment attach = new Attachment();
					attach.setBoardNo(no); // 게시글 번호 
					attach.setOriginalFilename(multiReq.getOriginalFileName(filename));
					attach.setRenamedFilename(multiReq.getFilesystemName(filename));
					board.addAttachment(attach);
				}
			}
		System.out.println("board" + board);
			
			// 2.업무로직
			int result = boardService.updateBoard(board);

			// 3.redirect
			request.getSession().setAttribute("msg", "게시글을 성공적으로 수정했습니다");
			response.sendRedirect(request.getContextPath() + "/board/boardView?no=" + no);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}


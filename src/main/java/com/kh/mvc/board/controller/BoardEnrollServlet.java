package com.kh.mvc.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.board.model.dto.Attachment;
import com.kh.mvc.board.model.dto.BoardExt;
import com.kh.mvc.board.model.service.BoardService;
import com.kh.mvc.common.HelloMvcFileRenamePolicy;
import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class BoardEnrollServlet
 */
@WebServlet("/board/boardEnroll")
public class BoardEnrollServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardService boardService = new BoardService();
	/**
	 * GET 게시글 등록폼 요청
	 * form 페이지에 연결
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.getRequestDispatcher("/WEB-INF/views/board/boardEnroll.jsp")
			.forward(request, response);
	
	}

	/**
	 * POST db에 insert 요청(게시글 등록하기 버튼 눌렀을시 작동할)
	 * fileupload는 두가지로 나뉜다.
	 * (첨부파일이 포함된 게시글 등록)
	 * - 1.서버컴퓨터에 파일저장 - cos.jar를이용
	 * 		-MultipartRequest객체 생성 이때 준비물↓(파일 요청응답)
	 * 			-HttpServletRequest를 제공해야함
	 * 			-saveDirectory(문자열로 저장경로)
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
			// 0.첨부파일처리
			ServletContext application = getServletContext();
			String saveDirectory = application.getRealPath("/upload/board");
			System.out.println("saveD" + saveDirectory);
			int maxPostSize = 1024 * 1024 * 10; //10MB
			String encoding = "utf-8";
			HelloMvcFileRenamePolicy policy = new HelloMvcFileRenamePolicy();
			
			MultipartRequest multiReq = new MultipartRequest(
					request, saveDirectory, maxPostSize, encoding, policy);
			
			// 저장된 파일
//			String originalFilename = multiReq.getOriginalFileName("upFile1"); 
//			String renamedFilename = multiReq.getFilesystemName("upFile1"); 
//			System.out.println("ori="+originalFilename);
//			System.out.println("ren="+renamedFilename);

			
			// 1.사용자입력값처리
			String title = multiReq.getParameter("title");
			String writer = multiReq.getParameter("writer");
			String content = multiReq.getParameter("content");
			BoardExt board = new BoardExt(0, title, writer, content, 0, null); //사용자입력한 값만 채우는 형태
//			Board board = new Board(0, title, writer, content, 0, null)
			Enumeration<String> filenames = multiReq.getFileNames();
			while(filenames.hasMoreElements()) {
				String filename = filenames.nextElement();
				File upFile = multiReq.getFile(filename);
				if(upFile != null) {
					Attachment attach = new Attachment();
					attach.setOriginalFilename(multiReq.getOriginalFileName(filename));
					attach.setRenamedFilename(multiReq.getFilesystemName(filename));
					board.addAttachment(attach);
				}
			}
	
			System.out.println("board" + board);
			
			// 2.업무로직
			int result = boardService.insertBoard(board);

			// 3.redirect(오류나지 않는이상 무조건 성공)
			//Session에 등록해야 리다이렉트 후에 내용이 나온다.
			request.getSession().setAttribute("msg", "게시글을 성공적으로 등록했습니다");
			response.sendRedirect(request.getContextPath() + "/board/boardList");
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	
	}

}

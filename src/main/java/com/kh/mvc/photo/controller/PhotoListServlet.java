package com.kh.mvc.photo.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.mvc.member.model.service.MemberService;
import com.kh.mvc.photo.model.service.PhotoService;

/**
 * Servlet implementation class PhotoListServlet
 */
@WebServlet("/photo/photoList")
public class PhotoListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PhotoService photoService = new PhotoService();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.업무로직
		int totalContent = photoService.getTotalContent();
		int numPerPage = 5;
		int totalPage = (int)Math.ceil((double) totalContent / numPerPage);
		
		//2.view단 위임
		request.setAttribute("totalPage",totalPage);
		request.getRequestDispatcher("/WEB-INF/views/photo/photoList.jsp")
			.forward(request, response);
	}

}

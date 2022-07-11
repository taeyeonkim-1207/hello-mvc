package com.kh.mvc.board.model.service;
import static com.kh.mvc.common.JdbcTemplate.close;
import static com.kh.mvc.common.JdbcTemplate.commit;
import static com.kh.mvc.common.JdbcTemplate.getConnection;
import static com.kh.mvc.common.JdbcTemplate.rollback;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.kh.mvc.board.model.dao.BoardDao;
import com.kh.mvc.board.model.dto.Attachment;
import com.kh.mvc.board.model.dto.Board;
import com.kh.mvc.board.model.dto.BoardComment;
import com.kh.mvc.board.model.dto.BoardExt;

/**
 * 
 * DML
 * 
 * DQL
 * - Connection생성/반환
 * - Dao요청
 *
 */
public class BoardService {
	private BoardDao boardDao = new BoardDao();
	
	public List<Board> findAll(Map<String, Object> param) {
		Connection conn = getConnection();
		List<Board> list = boardDao.findAll(conn,param);
		close(conn);
		return list;
		
	}

	public int getTotalContent() {
		Connection conn = getConnection();
		int totalContent = boardDao.getTotalContent(conn);
		close(conn);
		return totalContent;
	}

	public int insertBoard(Board board) {
		Connection conn = getConnection();
		int result = 0;
		
		try {
			//board테이블에 insert(한행)
			result = boardDao.insertBoard(conn, board);
			
			//방금 등록된 board.no컬럼값을 조회 (attach)
			int boardNo = boardDao.getLastBoardNo(conn);
			System.out.println("boardNo= " + boardNo);
			
			//attachment테이블에 insert
			List<Attachment> attachments = ((BoardExt) board).getAttachments();
			if(attachments != null && !attachments.isEmpty()) {
				
				for(Attachment attach : attachments){
					attach.setBoardNo(boardNo);
					result = boardDao.insertAttachment(conn, attach);
				}
			}
			commit(conn);
			
		}catch(Exception e) {
			rollback(conn);
			throw e;
		}finally {
			close(conn);
		}
		return result;
	}
	
	public Board findByNo(int no) {
		return findByNo(no,true);
	}
			

	public Board findByNo(int no, boolean hasRead) {
		Connection conn = getConnection();
		Board board = null;
		
		try {
			if(!hasRead) {
				int result = boardDao.updateReadCount(conn,no);
			}
			
			//board테이블에서 조회,
			board = boardDao.findByNo(conn, no);
			
			//attachment테이블에서 조회 List<Attachment>
			List<Attachment> attachments = boardDao.findAttachmentByBoardNo(conn,no);
			((BoardExt) board).setAttachments(attachments);
			
			commit(conn);
		}catch(Exception e){
			rollback(conn);
			throw e;
		}finally {
			close(conn);
		}

		return board;
	}

	public Attachment findAttachmentByNo(int no) {
		Connection conn = getConnection();
		Attachment attach = boardDao.findAttachmentByNo(conn, no);
		close(conn);
		return attach;
	}

	public List<Attachment> findAttachmentByBoardNo(int boardNo) {
		Connection conn = getConnection();
		List<Attachment> attachments = boardDao.findAttachmentByBoardNo(conn, boardNo);
		close(conn);
		return attachments;
	}

	public int deleteBoard(int no) {
		Connection conn = getConnection();
		int result = 0;
		try {
			result = boardDao.deleteBoard(conn, no);
			commit(conn);
		}catch(Exception e) {
			rollback(conn);
			throw e;
		}finally {
			close(conn);
		}
		return result;
	}

	public int updateBoard(BoardExt board) {
		Connection conn = getConnection();
		int result = 0;
		try {
			//1.게시글 수정
			result = boardDao.updateBoard(conn, board);
			//2.첨부파일 등록
			List<Attachment> attachments = board.getAttachments();
			if(attachments != null || !attachments.isEmpty()) {
				for(Attachment attach : attachments) {
					result = boardDao.insertAttachment(conn, attach);
				}
			}
			commit(conn);
			
		}catch(Exception e) {
			rollback(conn);
			throw e;
			
		}finally {
			close(conn);
		}
		return result;
	}

	public int deleteAttachment(int attachNo) {
		Connection conn = getConnection();
		int result = 0;
		try {
			result = boardDao.deleteAttachment(conn, attachNo);
			commit(conn);
			
		}catch(Exception e) {
			rollback(conn);
			throw e;
			
		}finally {
			close(conn);
		}
		return result;
	}

	public int insertBoardComment(BoardComment boardComment) {
		Connection conn = getConnection();
		int result = 0;
		try {
			result = boardDao.insertBoardComment(conn, boardComment);
			commit(conn);
			
		}catch(Exception e) {
			rollback(conn);
			throw e;
			
		}finally {
			close(conn);
		}
		return result;
	}

	public List<BoardComment> findBoardCommentByBoardNo(int boardNo) {
		Connection conn = getConnection();
		List<BoardComment> commentList = boardDao.findBoardCommentByBoardNo(conn, boardNo);
		close(conn);
		return commentList;
	}

	public int deleteBoardComment(int no) {
		Connection conn = getConnection();
		int result = 0;
		try {
			result = boardDao.deleteBoardComment(conn, no);
			commit(conn);
			
		}catch(Exception e) {
			rollback(conn);
			throw e;
			
		}finally {
			close(conn);
		}
		return result;
	}
}

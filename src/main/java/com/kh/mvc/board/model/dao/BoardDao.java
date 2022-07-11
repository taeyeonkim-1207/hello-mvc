package com.kh.mvc.board.model.dao;

import static com.kh.mvc.common.JdbcTemplate.close;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.kh.mvc.board.model.dto.Attachment;
import com.kh.mvc.board.model.dto.Board;
import com.kh.mvc.board.model.dto.BoardComment;
import com.kh.mvc.board.model.dto.BoardExt;
import com.kh.mvc.board.model.dto.CommentLevel;
import com.kh.mvc.board.model.exception.BoardException;

/**
 * 
 * Properties 쿼리관리객체 - /sql/board/board-query.properties load!
 * 
 * DML
 * 
 * DQL
 * - PreparedStatement객체 
 * - 미완성쿼리/값대입
 * - 실행
 * - ResultSet처리 (Board객체)
 * - PreparedStatement/ResultSet 반환
 *
 */
public class BoardDao {

	private Properties prop = new Properties();

	public BoardDao() {
		//properties파일 가져오기
		String filename = BoardDao.class.getResource("/sql/board/board-query.properties").getPath();
		try {
			prop.load(new FileReader(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Board> findAll(Connection conn, Map<String, Object> param) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<Board> list = new ArrayList<>();
		String sql = prop.getProperty("findAll");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, (int) param.get("start"));
			pstmt.setInt(2, (int) param.get("end"));
			
			rset = pstmt.executeQuery();
			
// handleBoardReusltSet: 재사용을 위해 만듬
			while(rset.next()) {
//				Board
				BoardExt board = handleBoardResultSet(rset);
//				attach없어
				board.setAttachCount(rset.getInt("attach_count"));
				board.setCommentCount(rset.getInt("comment_count"));
				list.add(board);
			}
			
		} catch (SQLException e) {
			throw new BoardException("게시글 목록 조회 오류!", e);
		} finally {
			close(rset);
			close(pstmt);
		}
		return list;
	}

	//게시글 가져오기
	private BoardExt handleBoardResultSet(ResultSet rset) throws SQLException {
		int no = rset.getInt("no");
		String title = rset.getString("title");
		String writer = rset.getString("writer");
		String content = rset.getString("content");
		int readCount = rset.getInt("read_count");
		Timestamp regDate = rset.getTimestamp("reg_date");
		return new BoardExt(no, title, writer, content, readCount, regDate);
//		return new Board(no, title, writer, content, readCount, regDate);
	
	}

	public int getTotalContent(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int totalContent = 0;
		String sql = prop.getProperty("getTotalContent");

//			머 대입할 값 없으니 바로 실행
		try {
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if(rset.next())
				totalContent = rset.getInt(1);
		} catch (SQLException e) {
			throw new BoardException("총 게시물수 조회 오류!", e);
		} finally {
			close(rset);
			close(pstmt);
		}
		
		return totalContent;
	}

	public int insertBoard(Connection conn, Board board) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("insertBoard"); //여기서 쓴 물음표들 dao에서 채워주는거임
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getTitle());
			pstmt.setString(2, board.getWriter());
			pstmt.setString(3, board.getContent());
		
			result = pstmt.executeUpdate();
			
		}catch(SQLException e) {
			throw new BoardException("게시글 등록 오류!", e);
		}finally {
			close(pstmt);
		}
		
		return result;
	}

	public int getLastBoardNo(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int boardNo = 0;
		String sql = prop.getProperty("getLastBoardNo");
		
		try {
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if(rset.next()) {
				boardNo = rset.getInt(1);
			}
		}catch(SQLException e) {
			throw new BoardException("생성된 게시글 번호조회 오류!", e);
		}finally {
			close(rset);
			close(pstmt);
		}
		
		return boardNo;
	}

	public int insertAttachment(Connection conn, Attachment attach) {
		PreparedStatement pstmt=null;
		int result = 0;
		String sql = prop.getProperty("insertAttachment");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, attach.getBoardNo()); //fk
			pstmt.setString(2, attach.getOriginalFilename());
			pstmt.setString(3, attach.getRenamedFilename());
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new BoardException("첨부파일 등록 오류!", e);
		}finally {
			close(pstmt);
		}
		return result;
	}

	public Board findByNo(Connection conn, int no) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Board board = null;
		String sql = prop.getProperty("findByNo");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, no);
			rset = pstmt.executeQuery();
			while(rset.next()) { //한건조회(if문가능)
				board = handleBoardResultSet(rset);
			}
		} catch (SQLException e) {
			throw new BoardException("게시글 1건 조회 오류!",e);
		}finally {
			close(rset);
			close(pstmt);
		}
		return board;
	}

	public List<Attachment> findAttachmentByBoardNo(Connection conn, int boardNo) {
		PreparedStatement pstmt =  null;
		ResultSet rset = null;
		List<Attachment> attachments = new ArrayList<>();
		String sql = prop.getProperty("findAttachmentByBoardNo");

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			rset = pstmt.executeQuery();
			while(rset.next()) {
				attachments.add(handleAttachmentResultSet(rset));
			}
		} catch (SQLException e) {
			throw new BoardException("게시글별 첨부파일 조회 오류!",e);
		}finally {
			close(rset);
			close(pstmt); 
		}
		
		return attachments;
	}

	private Attachment handleAttachmentResultSet(ResultSet rset) throws SQLException {
		Attachment attach = new Attachment();
		attach.setNo(rset.getInt("no"));
		attach.setBoardNo(rset.getInt("board_no"));
		attach.setOriginalFilename(rset.getString("original_filename"));
		attach.setRenamedFilename(rset.getString("renamed_filename"));
		attach.setRegDate(rset.getTimestamp("reg_date"));
		return attach;
	}

	public int updateReadCount(Connection conn, int no) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("updateReadCount");
		
		try {
			pstmt = conn.prepareStatement(sql);
			//물음표 채우쟈
			pstmt.setInt(1, no);
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new BoardException("조회수 증가 오류!", e);
		}finally {
			close(pstmt);
		}
		
		return result;
	}

	public Attachment findAttachmentByNo(Connection conn, int no) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Attachment attach = null;
		String sql = prop.getProperty("findAttachmentByNo");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, no);
			rset = pstmt.executeQuery();
			while(rset.next()) {
				attach = handleAttachmentResultSet(rset);
			}
			
		} catch (SQLException e) {
			throw new BoardException("첨부파일 한건 조회 오류!", e);
		}finally {
			close(rset);
			close(pstmt);
		}
		
		
		return attach;
	}

	public int deleteBoard(Connection conn, int no) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("deleteBoard");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, no);
			result = pstmt.executeUpdate();
		} 
		catch (SQLException e) {
			throw new BoardException("게시글 삭제 오류!", e);
		}
		finally {
			close(pstmt);
		}
		return result;
	}

	public int updateBoard(Connection conn, BoardExt board) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("updateBoard");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, board.getTitle());
			pstmt.setString(2, board.getContent());
			pstmt.setInt(3, board.getNo());
			
			result = pstmt.executeUpdate();
		} 
		catch (SQLException e) {
			throw new BoardException("게시글 수정 오류!", e);
		}
		finally {
			close(pstmt);
		}
		return result;
	}

	public int deleteAttachment(Connection conn, int attachNo) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("deleteAttachment");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, attachNo );
			result = pstmt.executeUpdate();
		} 
		catch (SQLException e) {
			throw new BoardException("첨부파일 삭제 오류!", e);
		}
		finally {
			close(pstmt);
		}
		return result;
	}

	public int insertBoardComment(Connection conn, BoardComment boardComment) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("insertBoardComment");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardComment.getCommentLevel().getValue());
			pstmt.setString(2, boardComment.getWriter());
			pstmt.setString(3, boardComment.getContent());
			pstmt.setInt(4, boardComment.getBoardNo());
//			pstmt.setInt(5, boardComment.getCommentRef()); //fk위반
			
			//DB에 0들어가면 안대!
			pstmt.setObject(5, boardComment.getCommentRef() == 0 ? 
									null : 
										boardComment.getCommentRef());
			result = pstmt.executeUpdate();
		} 
		catch (SQLException e) {
			throw new BoardException("댓글/답글 등록 오류!", e);
		}
		finally {
			close(pstmt);
		}
		return result;
		}

	public List<BoardComment> findBoardCommentByBoardNo(Connection conn, int boardNo) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		//반환타입
		List<BoardComment> commentList = new ArrayList<>();
		String sql = prop.getProperty("findBoardCommentByBoardNo");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			rset = pstmt.executeQuery();
			while(rset.next()) {
				commentList.add(handelBoardCommentResultSet(rset));
			}
		} catch (SQLException e) {
			throw new BoardException("게시글별 댓글 조회 오류!", e);
		}finally {
			close(rset);
			close(pstmt);
		}

		return commentList;
	}

	private BoardComment handelBoardCommentResultSet(ResultSet rset) throws SQLException   {
		int no = rset.getInt("no");
		CommentLevel commentLevel = CommentLevel.valueOf(rset.getInt("comment_level"));
		String writer = rset.getString("writer");
		String content = rset.getString("content");
		int boardNo = rset.getInt("board_no");
		int commentRef = rset.getInt("comment_ref"); //null인경우 0을 반환
		Timestamp regDate = rset.getTimestamp("reg_date");
		return new BoardComment(no, commentLevel, writer, content, boardNo, commentRef, regDate);
	}

	public int deleteBoardComment(Connection conn, int no) {
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = prop.getProperty("deleteBoardComment");
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, no);
			result = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			throw new BoardException("댓글/답글 삭제 오류!", e);
		}finally {
			close(pstmt);
		}

		return result;
		}

	}


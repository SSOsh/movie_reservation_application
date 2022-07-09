package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBClose;
import db.DBConnection;
import dto.MemberDto;
import dto.UserReserveDto;

public class MemberDao {

	private static MemberDao dao = new MemberDao();

	private MemberDao() {
		DBConnection.initConnection();
	}

	public static MemberDao getInstance() {
		return dao;
	}

	// 회원 추가
	public boolean addMember(MemberDto dto) {

		String sql = " insert into user(id, pwd, name, email, phone, auth) " + " values(?, ?, ?, ?, ?, 0) ";

		Connection conn = null;
		PreparedStatement psmt = null;
		int count = 0;

		try {
			conn = DBConnection.getConnection();

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, dto.getId());
			psmt.setString(2, dto.getPwd());
			psmt.setString(3, dto.getName());
			psmt.setString(4, dto.getEmail());
			psmt.setString(5, dto.getPhone());

			count = psmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBClose.close(conn, psmt, null);
		}

		return count > 0 ? true : false;
	}

	// getId
	public boolean getId(String id) {

		String sql = " SELECT ID " + " FROM user " + " WHERE ID=? ";
		/*
		 * String sql = " SELECT COUNT(*) " + " FROM memb " + " WHERE ID=? ";
		 */

		Connection conn = null; // DB 연결
		PreparedStatement psmt = null; // Query문을 실행
		ResultSet rs = null; // 결과 취득

		boolean findId = false;

		try {
			conn = DBConnection.getConnection();

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, id);

			rs = psmt.executeQuery();
			if (rs.next()) {
				findId = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBClose.close(conn, psmt, rs);
		}

		return findId;
		// true 반환하면 이미 있는 id 
	}

	// 로그인하기
	public MemberDto login(MemberDto dto) {
		String sql = " select id, name, email, phone, auth " + " from user " + " where id=? and pwd=? ";

		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		MemberDto mem = null;

		try {
			conn = DBConnection.getConnection();
			System.out.println("1/3 login success");

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, dto.getId());
			psmt.setString(2, dto.getPwd());

			rs = psmt.executeQuery();
			System.out.println("2/3 login success");

			if (rs.next()) {
				String id = rs.getString(1);
				String name = rs.getString(2);
				String email = rs.getString(3);
				String phone = rs.getString(4);
				int auth = rs.getInt(5);

				mem = new MemberDto(id, null, name, email, phone, auth);
				System.out.println(dto.getId());
			}
			System.out.println("3/3 login success");

		} catch (SQLException e) {
			System.out.println("login fail");
		} finally {
			DBClose.close(conn, psmt, rs);
		}

		return mem;
	}

	// 아이디 / 비밀번호 찾기 (회원정보 찾기)
	public MemberDto findMember(String name, String email) {

		String sql = " SELECT ID, PWD " + " FROM user " + " WHERE name=? AND email=? ";

		Connection conn = null; // DB 연결
		PreparedStatement psmt = null; // Query문을 실행
		ResultSet rs = null; // 결과 취득

		MemberDto mem = null;

		try {
			conn = DBConnection.getConnection();
			System.out.println("1/3 findMember success");

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, name);
			psmt.setString(2, email);

			rs = psmt.executeQuery();
			System.out.println("2/3 findMember success");

			if (rs.next()) {
				String id = rs.getString(1);
				String pwd = rs.getString(2);

				mem = new MemberDto(id, pwd, null, null, null, 3);
				System.out.println(mem.getId());
			}
			System.out.println("3/3 logfindMember success");

		} catch (SQLException e) {
			System.out.println("logfindMember fail");
		} finally {
			DBClose.close(conn, psmt, rs);
		}
		return mem;
	}

	public boolean withdraw(MemberDto dto) {
		String sql = " delete " + " FROM user " + " WHERE pwd=? ";

		Connection conn = null; // DB 연결
		PreparedStatement psmt = null; // Query문을 실행
		ResultSet rs = null; // 결과 취득

		String id = "";
		MemberDto mem = null;
		int count = 0;

		try {
			conn = DBConnection.getConnection();
			System.out.println("1/3 withdraw success");

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, dto.getPwd());

			count = psmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBClose.close(conn, psmt, null);
		}

		return (count == 1) ? true : false;
	}
	
	public List<UserReserveDto> findreserve(MemberDto dto) {
		// user_reservation_location 이용부분
		String sql = " select seq, reservation_seq, location_seq "
					+ " from user_reservation_location "
					+ " where user_id = ? ";
		
		Connection conn = null; // DB 연결
		PreparedStatement psmt = null; // Query문을 실행
		ResultSet rs = null; // 결과 취득

		List<UserReserveDto> list = new ArrayList<UserReserveDto>();
		UserReserveDto udto = null;
		
		int seq = -1;
		int reservation_seq = -1;
		int location_seq = -1;
		int movie_seq = -1;
		
		String title = "";
		String city = "";
		String date = ""; 	// wdate + rdate + movie_seq
		String wdate = "";
		String rdate = "";
		
		
		try {
			conn = DBConnection.getConnection();
			
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, dto.getId());

			rs = psmt.executeQuery();
			
			if (rs.next()) {
				seq = rs.getInt(1);
				reservation_seq = rs.getInt(2);
				location_seq = rs.getInt(3);
				
				city = findCity(location_seq);
				date = findDate(reservation_seq);
				
				wdate = date.substring(0, 10);
				rdate = date.substring(10, 20);
				movie_seq = Integer.parseInt(date.substring(20));
				
				title = findTitle(movie_seq);
				
				udto = new UserReserveDto(title, city, wdate, rdate);
				
				list.add(udto);
			}
		} catch (SQLException e) {
			System.out.println("findreserve fail");
		} finally {
			DBClose.close(conn, psmt, rs);
		}
		
		return list;
	}
	
	public String findCity(int location_seq) {
		String sql = " select city, city_detail "
				+ " from location "
				+ " where seq= ? ";
		
		Connection conn = null; // DB 연결
		PreparedStatement psmt = null; // Query문을 실행
		ResultSet rs = null; // 결과 취득
		
		String city = "";
		String city_name = "";			// 서울
		String city_detail = "";	// 강남
	
		try {
			conn = DBConnection.getConnection();
			
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, location_seq + "");
	
			rs = psmt.executeQuery();
			
			if (rs.next()) {
				city_name = rs.getString(1);
				city_detail = rs.getString(2);
				
				city = city_name + " " + city_detail;
			}
		} catch (SQLException e) {
			System.out.println("findreserve fail");
		} finally {
			DBClose.close(conn, psmt, rs);
		}
			
		return city;
	}
	
	public String findDate(int reservation_seq) {
		String sql = " select movie_seq, wdate, rdate " 
				 + " from reservation " 
				 + " where seq= ? ";
		 
		 Connection conn = null; // DB 연결 
		 PreparedStatement psmt = null; // Query문을 실행 
		 ResultSet rs = null; // 결과 취득
		 
		 int movie_seq = -1; 
		 String wdate = ""; 
		 String rdate = "";
		 String date = null;
		 
		 try { conn = DBConnection.getConnection();
		 
			 psmt = conn.prepareStatement(sql); 
			 psmt.setString(1, reservation_seq + "");
			 
			 rs = psmt.executeQuery();
			 
			 if (rs.next()) { 
				 movie_seq = rs.getInt(1);
				 wdate = rs.getString(2);
				 rdate = rs.getString(3);
				 wdate = wdate.substring(0, 10);
				 rdate = rdate.substring(0, 10);
				 date = wdate + rdate + movie_seq;
			 }
		 
		 } catch (SQLException e) { 
			 System.out.println("findreserve fail"); 
		 } finally { 
			 DBClose.close(conn, psmt, rs); 
		 }
		 
		return date;
	}
	
	public String  findTitle(int movie_seq) {
		String sql = " SELECT title "
				+ "FROM movie "
				+ "where seq= ? ";
	
		Connection conn = null; // DB 연결
		PreparedStatement psmt = null; // Query문을 실행
		ResultSet rs = null; // 결과 취득
		
		String title = "";
		
		try {
			conn = DBConnection.getConnection();
			
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, movie_seq + "");
		
			rs = psmt.executeQuery();
			
			if (rs.next()) {
				title = rs.getString(1);
			}
			
		} catch (SQLException e) {
			System.out.println("findreserve fail");
		} finally {
			DBClose.close(conn, psmt, rs);
		}
		
		return title;
	}
	
}

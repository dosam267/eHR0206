package com.ehr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDaoJdbc implements UserDao {
static final Logger LOG= Logger.getLogger(UserDao.class);
	
	private RowMapper<User> userMapper = new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User tmp=new User();
			tmp.setU_id(rs.getString("u_id"));
			tmp.setName(rs.getString("name"));
			tmp.setPasswd(rs.getString("passwd"));
			tmp.sethLevel(Level.valueOf(rs.getInt("h_level")));
			tmp.setLogin(rs.getInt("login"));
			tmp.setRecommend(rs.getInt("recommend"));
			tmp.setEmail(rs.getString("email"));
			tmp.setRegDt(rs.getString("reg_dt"));
			
			tmp.setNum(rs.getInt("rnum"));
			tmp.setTotalCnt(rs.getInt("total_cnt"));
			return tmp;
		}
	};
	
	private JdbcTemplate jdbcTemplate;
	
	

	private DataSource dataSource;
	
	public UserDaoJdbc() {	}
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		
		this.dataSource = dataSource;
	}
	@Override
	public int update(User user) {
		int flag =0;
		StringBuilder sb=new StringBuilder();
		sb.append(" UPDATE users            \n");
		sb.append(" SET                     \n");
		sb.append("     name = ?        \n");
		sb.append("     ,passwd = ?      \n");
		sb.append("     ,h_level = ?     \n");
		sb.append("     ,login = ?       \n");
		sb.append("     ,recommend = ?   \n");
		sb.append("     ,email = ?       \n");
		sb.append(" 	,reg_dt =sysdate \n");
		sb.append("WHERE u_id=? \n");
	
		
		LOG.debug("=============================");
		LOG.debug("01. sql=\n"+sb.toString());
		LOG.debug("=============================");	
		
		LOG.debug("=============================");
		LOG.debug("02. param=\n"+user);
		LOG.debug("=============================");	
		Object[] args= {
				   user.getName()
				   ,user.getPasswd()
				   ,user.gethLevel().intValue()
				   ,user.getLogin()
				   ,user.getRecommend()
				   ,user.getEmail()
				   ,user.getU_id()
			
	};
		LOG.debug("=============================");
		LOG.debug("02.1 args=\n"+args);
		LOG.debug("=============================");	
		
		return jdbcTemplate.update(sb.toString(), args);
	
	}
	@Override
	public List<User> retrieve(Search vo) {
		//U_ID : 10
		//NAME : 20
		//EMAIL: 30
		
		StringBuilder param=new StringBuilder();
		
		if(null !=vo && !"".equals(vo.getSearchDiv())) {
			if("10".equals(vo.getSearchDiv())) {
				//아이디검색
				param.append("WHERE u_id like '%'|| ?|| '%' \n");
			}
			else if("20".equals(vo.getSearchDiv())) {
				param.append("WHERE name like '%'|| ?|| '%' \n");
			}
			else if("30".equals(vo.getSearchDiv())) {
				param.append("WHERE email like '%'|| ?|| '%' \n");
			}
		}
		StringBuilder sb=new StringBuilder();
		sb.append(" SELECT T1.*,T2.*                                              \n");
		sb.append(" FROM                                                          \n");
		sb.append(" (                                                             \n");
		sb.append(" 	SELECT   b.u_id                                           \n");
		sb.append("             ,b.name                                           \n");
		sb.append("             ,b.passwd                                         \n");
		sb.append("             ,b.h_level                                        \n");
		sb.append("             ,b.login                                          \n");
		sb.append("             ,b.recommend                                      \n");
		sb.append("             ,b.email                                          \n");
		sb.append("             ,to_char(b.reg_dt,'yyyy/mm/dd') reg_dt             \n");
		sb.append("             ,b.rnum                                          \n");
		sb.append("     FROM                                                      \n");
		sb.append("     (                                                         \n");
		sb.append("     select ROWNUM AS rnum,A.*                                 \n");
		sb.append("     FROM(                                                     \n");
		sb.append("         SELECT  *                                             \n");
		sb.append("         FROM users                                            \n");
		//---------------------------------------------------------------------------
		// WHERE
		//---------------------------------------------------------------------------
		sb.append("         --search                                              \n");
		sb.append(param.toString());
		sb.append("         ORDER BY reg_dt DESC                                  \n");
//		sb.append("     )A WHERE ROWNUM <=(&page_size*(&page_num-1)+&page_size)   \n");
//		sb.append("     )B WHERE b.rnum>=(&page_size*(&page_num-1)+1)             \n");
		sb.append("     )A WHERE ROWNUM <=(?*(?-1)+?)   \n");
		sb.append("     )B WHERE b.rnum>=(?*(?-1)+1)             \n");
		sb.append(" )T1                                                           \n");
		sb.append("                                                               \n");
		sb.append(" CROSS JOIN                                                \n");
		sb.append("     (   SELECT count(*) total_cnt                             \n");
		sb.append("         FROM USERS                                            \n");
		//---------------------------------------------------------------------------
		// WHERE
		//---------------------------------------------------------------------------
		sb.append("            --search                                           \n");
		sb.append(param.toString());
		sb.append("     ) T2														\n");	
		
		LOG.debug("=============================");
		LOG.debug("01. sql=\n"+sb.toString());
		LOG.debug("=============================");	
		
		LOG.debug("=============================");
		LOG.debug("02. param=\n"+vo);
		LOG.debug("=============================");	
		
		//param to List<Object>
		List<Object> listArgs=new ArrayList<Object>();
		
		if(null !=vo && !"".equals(vo.getSearchDiv())) {
			listArgs.add(vo.getSearchWord());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
			listArgs.add(vo.getSearchWord());
			
		}else {
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
		}
		
		LOG.debug("=============================");
		LOG.debug("02. listArgs=\n"+listArgs);
		LOG.debug("=============================");	
		
		return jdbcTemplate.query(sb.toString()
				,listArgs.toArray()
				, userMapper);
	}
	
	
	@Override
	public User get(String id)  {
		User outVO=null;
			
		//----------------------------------------
		//02.SQL작성
		//----------------------------------------		
		StringBuilder sb=new StringBuilder();
		sb.append("  SELECT                                    \n");
		sb.append("      u_id,                                 \n");
		sb.append("      name,                                 \n");
		sb.append("      passwd,                               \n");
		sb.append("      h_level,                              \n");
		sb.append("      login,                                \n");
		sb.append("      recommend,                            \n");
		sb.append("      email,                                \n");
		sb.append("      TO_CHAR(reg_dt,'YYYY/MM/DD') reg_dt,   \n");
		sb.append("      1 as rnum,                                \n");
		sb.append("      1 as total_cnt                                \n");
		//rowmapper를 retrive와 공유해 아래 컬럼추가		
		sb.append("  FROM                                      \n");
		sb.append("      users                                 \n");
		sb.append("  WHERE u_id = ?                            \n"); 
		
		LOG.debug("=============================");
		LOG.debug("02. sql=\n"+sb.toString());
		LOG.debug("=============================");		
		
		outVO=jdbcTemplate.queryForObject(sb.toString()
				,new Object[] {id} 
				, userMapper);
		
		//----------------------------------------
		//06.outVO==null 예외발생
		//----------------------------------------
		if(null == outVO) {
			throw new EmptyResultDataAccessException(1);
		}
		
		
		
		
		return outVO;
	}

	@Override
	public int add(User user) {
	int flag = 0;
		
		//----------------------------------------
		//02.SQL작성
		//----------------------------------------		
		StringBuilder sb=new StringBuilder();
		sb.append(" INSERT INTO users (     \n");
		sb.append("     u_id,               \n");
		sb.append("     name,               \n");
		sb.append("     passwd,             \n");
		sb.append("     h_level,            \n");
		sb.append("     login,              \n");
		sb.append("     recommend,          \n");
		sb.append("     email,              \n");
		sb.append("     reg_dt              \n");
		sb.append(" ) VALUES (              \n");
		sb.append("     ?,                  \n");
		sb.append("     ?,                  \n");
		sb.append("     ?,                  \n");
		sb.append("     ?,                  \n");
		sb.append("     ?,                  \n");
		sb.append("     ?,                  \n");
		sb.append("     ?,                  \n");
		sb.append("     SYSDATE             \n");
		sb.append(" )						\n");
		
		LOG.debug("=============================");
		LOG.debug("02. sql=\n"+sb.toString());
		LOG.debug("=============================");		
		Object[] args= {user.getU_id()
					   ,user.getName()
					   ,user.getPasswd()
					   ,user.gethLevel().intValue()
					   ,user.getLogin()
					   ,user.getRecommend()
					   ,user.getEmail()
				
		};
		LOG.debug("=============================");
		LOG.debug("02. param=\n"+user.toString());
		LOG.debug("=============================");	
		
		 flag=jdbcTemplate.update(sb.toString(), args);
		return flag;
		
	}

	@Override
	public int deleteUser(User user) {
		String query=" DELETE FROM users WHERE u_id = ? \n";
//		int flag =jdbcContext.executeSql(query,user);
		Object[]args = {user.getU_id()};
		int flag=jdbcTemplate.update(query, args);
		
		return flag;
	}

	@Override
	public int count(String uId) {
		int cnt = 0;//조회 count
		
		//----------------------------------------
		//02.SQL작성
		//----------------------------------------		
		StringBuilder sb=new StringBuilder();
		sb.append(" SELECT COUNT(*) cnt      \n");
		sb.append(" FROM users               \n");
		sb.append(" WHERE u_id like ?        \n");
		
		LOG.debug("=============================");
		LOG.debug("02. sql=\n"+sb.toString());
		LOG.debug("=============================");		
		
		

		cnt = jdbcTemplate.queryForObject(sb.toString()
				, new Object[] { "%"+uId+"%"}
				, Integer.class);
		
		LOG.debug("=============================");
		LOG.debug("04. Run cnt=\n"+cnt);
		LOG.debug("=============================");		
		
		return cnt;
	}

	@Override
	public List<User> getAll() {
StringBuilder sb=new StringBuilder();
		
		sb.append(" SELECT                                  \n");
		sb.append("     u_id,                               \n");
		sb.append("     name,                               \n");
		sb.append("     passwd,                             \n");
		sb.append("     h_level,                            \n");
		sb.append("     login,                              \n");
		sb.append("     recommend,                          \n");
		sb.append("     email,                              \n");
		sb.append("      TO_CHAR(reg_dt,'YYYY/MM/DD') reg_dt,   \n");
		sb.append("      1 as rnum,                                \n");
		sb.append("      1 as total_cnt                                \n");
		//rowmapper를 retrive와 공유해 아래 컬럼추가		
		sb.append(" FROM  users                             \n");
		sb.append("ORDER BY u_id \n");
		
		LOG.debug("=============================");
		LOG.debug("02. sql=\n"+sb.toString());
		LOG.debug("=============================");		
		
		List<User> list=jdbcTemplate.query(sb.toString(), userMapper);
		return list;
	}
	
	


}

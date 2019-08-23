package com.ehr;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoJunitFinal {

	private Logger LOG=Logger.getLogger(UserDaoJunitFinal.class);
	
	@Autowired
	ApplicationContext  context;
	
	private UserDao dao;
	private User user01;
	private User user02;
	private User user03;
	private User user04;
	
	
	@Before
	public void setUp() {
		user01=new User("j01_144","김도연01","1234", Level.BASIC, 1, 0,"dosam267@gmail.com","2019/08/23");
		user02=new User("j02_144","김도연02","1234", Level.SILVER, 51, 10,"dosam267@gmail.com","2019/08/23");
		user03=new User("j03_144","김도연03","1234", Level.GOLD, 51, 30,"dosam267@gmail.com","2019/08/23");
		dao = context.getBean("userDao", UserDaoJdbc.class);
		LOG.debug("==============================");
		LOG.debug("=01 context="+context);
		LOG.debug("=01 dao="+dao);
		LOG.debug("==============================");
	}
	
	
	@After
	public void tearDown() {
		LOG.debug("^^^^^^^^^^^^^^^^^^^^^^");
		LOG.debug("99 tearDown()");
		LOG.debug("^^^^^^^^^^^^^^^^^^^^^^");		
	}
	@Test
	public void update() 
	{
		LOG.debug("==============================");
		LOG.debug("=1. 기존 데이터 삭제=");
		LOG.debug("==============================");	
		dao.deleteUser(user01);
		dao.deleteUser(user02);
		dao.deleteUser(user03);
		LOG.debug("==============================");
		LOG.debug("=2. 데이터 입력=");
		LOG.debug("==============================");
		
		dao.add(user01);
			
		
		LOG.debug("==============================");
		LOG.debug("=3. 입력데이터를 수정한다=");
		LOG.debug("==============================");
		//user01=new User("j01_144","김도연01","1234", Level.BASIC, 1, 0,"dosam267@gmail.com","2019/08/23");
		user01.setName("김도연01_u");
		user01.setPasswd("1234_u");
		user01.sethLevel(Level.GOLD);
		user01.setLogin(99);
		user01.setRecommend(999);
		user01.setEmail("dosam267_@gmail.com");
		dao.update(user01);
		
		LOG.debug("==============================");
		LOG.debug("=4. 수정한 데이터와 3번을 비교한다.");
		LOG.debug("==============================");
		User vsUser=dao.get(user01.getU_id());
		
		assertTrue(user01.equals(vsUser));
	}
	
	
	@Test
	@Ignore
	public void retrieve(){
		Search vo=new Search(10,1,"","");
		List<User> list=dao.retrieve(vo);
		
		for(User user :list) {
			LOG.debug(user);
		}
	}
		
	
	
	@Test
	@Ignore
	public void getAll() throws SQLException {
//		dao.deleteUser(user01);
//		dao.deleteUser(user02);
//		dao.deleteUser(user03);
		
		List<User> list= dao.getAll();
		for(User user :list) {
			LOG.debug(user);
		}
	}
	
	
	@Test(expected = EmptyResultDataAccessException.class)
	@Ignore
	public void getFailure()throws ClassNotFoundException, SQLException {

		
		
		//-------------------------------------------
		//삭제
		//-------------------------------------------
		dao.deleteUser(user01);
		dao.deleteUser(user02);
		dao.deleteUser(user03);
		assertThat(dao.count("_144"), is(0));
		
		dao.get("unkonwnUserId");
	}
	
	@Test
	@Ignore
	public void count() throws ClassNotFoundException, SQLException {

		//-------------------------------------------
		//삭제
		//-------------------------------------------
		dao.deleteUser(user01);
		dao.deleteUser(user02);
		dao.deleteUser(user03);
		assertThat(dao.count("_144"), is(0));
		
		//-------------------------------------------
		//1건추가
		//-------------------------------------------	
		dao.add(user01);
		assertThat(dao.count("_144"), is(1));
		//-------------------------------------------
		//1건추가
		//-------------------------------------------	
		dao.add(user02);
		assertThat(dao.count("_144"), is(2));
		//-------------------------------------------
		//1건추가
		//-------------------------------------------	
		dao.add(user03);
		assertThat(dao.count("_144"), is(3));
			
		
	}
	
	@Test(timeout = 5000) //1.JUnit에게 테스트용 메소드임을 알려줌
	@Ignore
	public void addAndGet() {//2. public
		//j01_ip: j01_124

		
			dao.deleteUser(user01);
			dao.deleteUser(user02);
			dao.deleteUser(user03);
			assertThat(dao.count("_144"), is(0));
			
			
			LOG.debug("==============================");
			LOG.debug("=01 단건등록=");
			LOG.debug("==============================");			
			
			int flag = dao.add(user01);
			flag = dao.add(user02);
			flag = dao.add(user03);			
			assertThat(dao.count("_144"), is(3));
			
			LOG.debug("==============================");
			LOG.debug("=01.01 add flag="+flag);
			LOG.debug("==============================");
			
			assertThat(flag,is(1));
			
			LOG.debug("==============================");
			LOG.debug("=02 단건조회");
			LOG.debug("==============================");			
			
			User  userOne = dao.get(user01.getU_id());
			
			assertThat(userOne.getU_id(), is(user01.getU_id()));
			assertThat(userOne.getName(), is(user01.getName()));
			assertThat(userOne.getPasswd(), is(user01.getPasswd()));

		
	}
	
}

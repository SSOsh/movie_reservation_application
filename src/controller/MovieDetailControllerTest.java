package controller;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import dao.MovieDetailDao;

class MovieDetailControllerTest {

//	@Before
//	void before() throws IOException {
//		
//	}
	
//	@Test
//	void test() throws IOException {
//		Document doc = Jsoup.connect("http://www.cgv.co.kr/movies/detail-view/?midx=82120").get();
//		
//		String find = "div.cgvwrap div.container div.contents div.wrap-movie-detail";
//		String getDetail = "div.box-contents div.spec dd";
//		Elements test = doc.select(getDetail);// + getDetail);
//		String actor = test.get(2).text();
//		String[] actors = actor.split(",");
//		
//		actors[0].replace("<", "");
//		actors[actors.length - 1].replace(">", "");
//		
//		for (int i=0;i<actors.length;i++) {
//			actors[i] = actors[i].trim();
//		}
//		
//		assertEquals(actors[0], "톰 크루즈");
//	}
//	
	@Test
	void test() throws IOException {
		MovieDetailDao dao = MovieDetailDao.getInstance();
		dao.getMovieDetail(null);
	}
}
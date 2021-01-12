package top.scraft.picmanserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.scraft.picmanserver.dao.PictureLibraryDao;

import javax.annotation.Resource;

@SpringBootTest
class PicmanServerApplicationTests {

	@Resource
	private PictureLibraryDao pld;

	@Test
	void contextLoads() {
	}

	@Test
	void myTest() {
//		System.out.println(pld.existsByLidAndUsers_Said(1L, 2L));
	}

}

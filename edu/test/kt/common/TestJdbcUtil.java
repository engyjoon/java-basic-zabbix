package kt.common;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import org.junit.Test;

public class TestJdbcUtil {

	@Test
	public void testGetConnection() {
		Connection conn = JdbcUtil.getConnection();
		assertNotNull(conn);
		System.out.println(conn);
		JdbcUtil.close(conn);
	}

}

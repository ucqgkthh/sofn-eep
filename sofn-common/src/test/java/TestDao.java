import com.sofn.common.dao.DictDao;
import org.junit.Test;

public class TestDao {

    @Test
    public void testGet() {
        System.out.println(new DictDao().getByTypeAndKey("sex","man"));
    }

}

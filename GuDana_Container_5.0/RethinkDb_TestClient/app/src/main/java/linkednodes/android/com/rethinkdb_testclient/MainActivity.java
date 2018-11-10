package linkednodes.android.com.rethinkdb_testclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.exc.ReqlError;
import com.rethinkdb.gen.exc.ReqlQueryLogicError;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;




public class MainActivity extends AppCompatActivity {

    public static final RethinkDB r = RethinkDB.r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init connection
        Connection conn = r.connection().hostname("localhost").port(28015).connect();
        r.db("test").tableCreate("authors").run(conn);
    }
}
